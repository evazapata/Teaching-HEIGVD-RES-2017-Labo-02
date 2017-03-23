package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.data.StudentsStoreImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

   private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
   private Socket socket = null;
   private BufferedReader br = null;
   private PrintWriter pw = null;
   StudentsStoreImpl iss = new StudentsStoreImpl();
   InfoCommandResponse icr = new InfoCommandResponse();
   
   // DONE :D
   @Override
   public void connect(String server, int port) throws IOException {
      socket = new Socket(server, port);
      br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      pw = new PrintWriter(socket.getOutputStream());
      
      // Pour éviter de garder un ancien message envoyé par le serveur
      br.readLine();
   }

   // DONE :D
   @Override
   public void disconnect() throws IOException {
      socket.close();
   }
   
   // DONE :D
   @Override
   public boolean isConnected() {
      return socket != null && socket.isConnected();
   }

   // Semi-DONE :D, juste le petit doute à régler
   @Override
   public void loadStudent(String fullname) throws IOException {
      pw.write(RouletteV1Protocol.CMD_LOAD + '\n');
      pw.flush();
      
      Student s = new Student(fullname);
      
      // Petit doute de où stoker les étudiants...
      iss.addStudent(s);
   }

   // Appelle juste la fonction au-dessus avec un doute donc semi-DONE
   @Override
   public void loadStudents(List<Student> students) throws IOException {
      for (Student s : students) {
         loadStudent(s.getFullname());
      }
   }

   // DONE :D
   @Override
   public Student pickRandomStudent() throws EmptyStoreException, IOException {
      pw.write(RouletteV1Protocol.CMD_INFO + '\n');
      pw.flush();
      
      String number = br.readLine();
      
      if (JsonObjectMapper.parseJson(number, InfoCommandResponse.class).getNumberOfStudents() < 1) {
         throw new EmptyStoreException();
      } else {
         pw.write(RouletteV1Protocol.CMD_RANDOM + '\n');
         pw.flush();
      
         String student = br.readLine();
      
         return new Student(JsonObjectMapper.parseJson(student, RandomCommandResponse.class).getFullname());
      }
   }

   // Apparemment un bug ici, mais c'est pas normal car j'utilise le même code au-dessus pour pickRandomStudent()... haha
   @Override
   public int getNumberOfStudents() throws IOException {
      pw.write(RouletteV1Protocol.CMD_INFO + '\n');
      pw.flush();
      
      String number = br.readLine();
      
      return JsonObjectMapper.parseJson(number, InfoCommandResponse.class).getNumberOfStudents();
   }

   // DONE!
   @Override
   public String getProtocolVersion() throws IOException {
      // On écrit à RouletteProtocol pour lui demander l'info correspondant à la commande
      pw.write(RouletteV1Protocol.CMD_INFO + '\n');
      // On envoie la question
      pw.flush();
      
      // On récupère la version
      String version = br.readLine();
      
      // On parse le string et on renvoie la version
      return JsonObjectMapper.parseJson(version, InfoCommandResponse.class).getProtocolVersion();
   }
}