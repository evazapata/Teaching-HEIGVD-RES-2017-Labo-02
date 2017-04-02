package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 * @author Denise Gemesio
 * @author Ludovic Delafontaine
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

   private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
   private Socket socket = null;
   protected BufferedReader in = null;
   protected PrintWriter out = null;

   @Override
   public void connect(String server, int port) throws IOException {

      // Connect to the server
      socket = new Socket(server, port);

      // Create the inputs/outputs of the server
      in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
      out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

      // Read the welcome line
      in.readLine();
   }

   @Override
   public void disconnect() throws IOException {

      // Send the command to close the connection
      out.write(RouletteV1Protocol.CMD_BYE + '\n');
      out.flush();

      // Close all the local objects
      socket.close();
      in.close();
      out.close();
   }

   @Override
   public boolean isConnected() {

      // Check that the socket is connected
      return socket != null && socket.isConnected();
   }

   @Override
   public void loadStudent(String fullname) throws IOException {

      // Send the load command to the server
      out.write(RouletteV1Protocol.CMD_LOAD + '\n');
      out.flush();

      // Get the response from the server
      in.readLine();

      // Send the student name and the end of data
      out.write(fullname + '\n');
      out.write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER + '\n');
      out.flush();

      // Get the response from the server
      in.readLine();
   }

   @Override
   public void loadStudents(List<Student> students) throws IOException {
      // Load student per student
      for (Student s : students) {
         loadStudent(s.getFullname());
      }
   }

   @Override
   public Student pickRandomStudent() throws EmptyStoreException, IOException {

      // Send the random command
      out.write(RouletteV1Protocol.CMD_RANDOM + '\n');
      out.flush();

      // Get the response from the server
      String json = in.readLine();

      // Transform the response from Json into an object
      RandomCommandResponse response = JsonObjectMapper.parseJson(json, RandomCommandResponse.class);

      // If the server has sent an error, throw an exception
      if (response.getError() != "") {
         throw new EmptyStoreException();
      }

      // Return the student's name
      return new Student(response.getFullname());
   }

   @Override
   public int getNumberOfStudents() throws IOException {

      // Send the info command
      out.write(RouletteV1Protocol.CMD_INFO + '\n');
      out.flush();

      // Get the response
      String json = in.readLine();

      // Transform the response in an object and return the number of students
      return JsonObjectMapper.parseJson(json, InfoCommandResponse.class).getNumberOfStudents();
   }

   @Override
   public String getProtocolVersion() throws IOException {

      // Send the info command
      out.write(RouletteV1Protocol.CMD_INFO + '\n');
      out.flush();

      // Get the response
      String json = in.readLine();

      // Transform the response in an object and returns the protocol version
      return JsonObjectMapper.parseJson(json, InfoCommandResponse.class).getProtocolVersion();
   }
}
