package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.server.IClientHandler;
import ch.heigvd.res.labs.roulette.net.server.RouletteServer;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.*;
import java.net.Socket;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Densise Gemesio
 * @author Ludovic Delafontaine
 */
public class RouletteV2ludelafoTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = {"ludelafo", "evazapata"})
  public void theServerShoudClearTheStudentsDatabase() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    client.loadStudent("sacha");
    client.loadStudent("olivier");
    client.loadStudent("fabienne");
    assertEquals(3, client.getNumberOfStudents());

    client.clearDataStore();
    assertEquals(0, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = {"ludelafo", "evazapata"})
  public void theServerShouldReturnTheStudentList() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    client.loadStudent("sacha");
    client.loadStudent("olivier");
    client.loadStudent("fabienne");
    assertEquals(3, client.getNumberOfStudents());

    List<Student> list = client.listStudents();
    assertEquals(3, list.size());
  }

  @Test
  @TestAuthor(githubId = {"ludelafo", "evazapata"})
  public void theServerShouldBeStartedOnTheRightPort() throws IOException {
    RouletteServer server = roulettePair.getServer();

    assertEquals(RouletteV1Protocol.DEFAULT_PORT, server.getPort());
  }
  
}
