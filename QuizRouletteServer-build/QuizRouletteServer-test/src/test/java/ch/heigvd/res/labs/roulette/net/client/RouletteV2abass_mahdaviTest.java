package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;



/**
 *
 * @author abass mahdavi beware github account: abass-mahdavi (not
 * abass_mahdavi)
 */
@Ignore
public class RouletteV2abass_mahdaviTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "abass-mahdavi")
    public void aStudentShallNotBeInsertedMoreThanOnce() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("Albert Einstein");
        assertEquals(1, client.getNumberOfStudents());
        client.loadStudent("Albert Einstein");
        assertEquals(1, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "abass-mahdavi")
    public void byeMeansConnectionIsClosed() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        java.io.OutputStream os = ((Socket) client).getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));
        client.connect("localhost", port);
        writer.println(RouletteV2Protocol.CMD_BYE);
        writer.flush();
        assertFalse(client.isConnected());
    }

    @Test
    @TestAuthor(githubId = "abass-mahdavi")
    public void clearMeansNoMoreStudent() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("Albert Einstein");
        assertEquals(1, client.getNumberOfStudents());
        java.io.OutputStream os = ((Socket) client).getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));
        writer.println(RouletteV2Protocol.CMD_CLEAR);
        writer.flush();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "abass-mahdavi")
    public void itShouldBePossibleForMoreThanOneClientToConnectToARouletteServer() throws Exception {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client1 = new RouletteV2ClientImpl();
        assertFalse(client1.isConnected());
        client1.connect("localhost", port);
        IRouletteV2Client client2 = new RouletteV2ClientImpl();
        assertFalse(client2.isConnected());
        client2.connect("localhost", port);
        assertTrue(client1.isConnected());
        assertTrue(client2.isConnected());
    }

    @Test
    @TestAuthor(githubId = "abass-mahdavi")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }
}
