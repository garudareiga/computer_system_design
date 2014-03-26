package edu.berkeley.cs162;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.*;

import org.junit.Test;

public class SocketServerTest {

    @Test
    public void test() {
        try {
            //String hostName = "localhost";
            //String ipNumber = "127.0.0.1";
            //System.out.println(String.format("%s's address is %s", hostName, InetAddress.getByName(hostName).getHostAddress()));
            //System.out.println(String.format("%s's name is %s", ipNumber, InetAddress.getByName(ipNumber).getHostName()));

            // Socket Server
            String hostName = InetAddress.getLocalHost().getHostName();
            SocketServer server = new SocketServer(hostName, 8080);
            server.connect();
            server.run();
            // Socket Client
            Socket clientSock = new Socket(hostName, 8080);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
