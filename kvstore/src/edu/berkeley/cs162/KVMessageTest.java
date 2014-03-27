package edu.berkeley.cs162;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.*;

import org.junit.Test;

public class KVMessageTest {

	@Test
	public void testKVMessageString() {
		try {
			KVMessage kvmsg = new KVMessage("resp");
			assertEquals("resp", kvmsg.getMsgType());
			//System.out.println(kvmsg.getMsgType());
		} catch (KVException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testKVMessageStringString() {
		try {
			KVMessage kvmsg = new KVMessage("resp", "Error Message");
			assertEquals("resp", kvmsg.getMsgType());
			assertEquals("Error Message", kvmsg.getMessage());
			//System.out.println(kvmsg.toXML());
		} catch (KVException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testKVMessage() {
        try {
            int port = 8080;
            String hostName = InetAddress.getLocalHost().getHostName();
            ServerSocket server = new ServerSocket(port);
            
            KVClient kvClient = new KVClient(hostName, port);
            kvClient.put("9", "Tony Parker");
            
            Socket sock = null;
            while (sock == null) {
                sock = server.accept();
                if (sock != null) {
                    KVMessage kvmsg = new KVMessage(sock);
                    System.out.println(kvmsg.toXML());
                }
            }
            server.close();
        } catch (KVException e) {
            System.out.println(e.getMsg().getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
