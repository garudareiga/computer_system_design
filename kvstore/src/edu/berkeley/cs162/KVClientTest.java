package edu.berkeley.cs162;

import java.io.IOException;
import java.net.*;

import org.junit.Test;

public class KVClientTest {

    @Test
    public void test() {
        try {
            int port = 8080;
            String hostName = InetAddress.getLocalHost().getHostName();
            ServerSocket server = new ServerSocket(port);
            
            KVClient kvClient = new KVClient(hostName, port);
            kvClient.put("9", "Tony Parker");
            server.close();
        } catch (KVException e) {
            System.out.println(e.getMsg().getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
