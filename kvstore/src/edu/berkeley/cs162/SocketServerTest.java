package edu.berkeley.cs162;

import java.io.IOException;
import java.net.*;

import org.junit.Test;

public class SocketServerTest {

    @Test
    public void test() {
        try {           
            int port = 8081;
            String hostName = InetAddress.getLocalHost().getHostName();
            SocketServer server = new SocketServer(hostName, port);
            server.connect();
            //server.run();
            server.stop();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
