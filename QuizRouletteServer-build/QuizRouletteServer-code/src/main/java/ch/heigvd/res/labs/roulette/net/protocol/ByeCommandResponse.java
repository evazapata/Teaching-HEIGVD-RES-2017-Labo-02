package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 * 
 * @author Ludovic Delafontaine & Denise Gemesio
 */
public class ByeCommandResponse {

  private String status;
  private int numberOfCommands;

  public ByeCommandResponse() {
  }

  public ByeCommandResponse(String status) {
    this.status = status;
    this.numberOfCommands = 0;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getNumberOfCommands() {
    return numberOfCommands;
  }

  public void newCommand() {
    numberOfCommands++;
  }

}
