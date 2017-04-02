/*package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
*/
/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti
 */
/*
public class RouletteV2ClientHandler implements IClientHandler {

  public RouletteV2ClientHandler(IStudentsStore store) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of gen  
  }

  @Override
  public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
*/

package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientHandler implements IClientHandler {

  final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());

  private final IStudentsStore store;

  public RouletteV2ClientHandler(IStudentsStore store) {
    this.store = store;
  }

  @Override
  public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

    writer.println("Hello. Online HELP is available. Will you find it?");
    writer.flush();

    String command;
    boolean done = false;
    ByeCommandResponse bcResponse = new ByeCommandResponse();
    while (!done && ((command = reader.readLine()) != null)) {
      LOG.log(Level.INFO, "COMMAND: {0}", command);
      switch (command.toUpperCase()) {
         case RouletteV2Protocol.CMD_RANDOM:
          bcResponse.newCommand();
          RandomCommandResponse rcResponse = new RandomCommandResponse();
          try {
            rcResponse.setFullname(store.pickRandomStudent().getFullname());
          } catch (EmptyStoreException ex) {
            rcResponse.setError("There is no student, you cannot pick a random one");
          }
          writer.println(JsonObjectMapper.toJson(rcResponse));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_HELP:
          bcResponse.newCommand();
          writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
          break;
        case RouletteV2Protocol.CMD_INFO:
          bcResponse.newCommand();
          InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(response));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LOAD:
          bcResponse.newCommand();
          int currentNbOfStudents = store.getNumberOfStudents();
          writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
          writer.flush();
          store.importData(reader);
          LoadCommandResponse lcResponse = new LoadCommandResponse();
          lcResponse.setNumberOfNewStudents(store.getNumberOfStudents() - currentNbOfStudents);
          lcResponse.setStatus("success");
          writer.println(JsonObjectMapper.toJson(lcResponse));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_BYE:
          bcResponse.newCommand();
          bcResponse.setStatus("success");
          writer.println(JsonObjectMapper.toJson(bcResponse));
          writer.flush();
          done = true;
          break;
        case RouletteV2Protocol.CMD_CLEAR:
          bcResponse.newCommand();
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LIST:
          bcResponse.newCommand();
          writer.println(JsonObjectMapper.toJson(store.listStudents()));
          writer.flush();
          break;
        default:
          writer.println("Huh? please use HELP if you don't know what commands are available.");
          writer.flush();
          break;
      }
      writer.flush();
    }

  }

}
