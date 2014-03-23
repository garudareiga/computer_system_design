package edu.berkeley.cs162;

import static org.junit.Assert.*;

import org.junit.Test;

public class KVMessageTest {

	@Test
	public void testKVMessageString() {
		try {
			KVMessage kvmsg = new KVMessage("resp");
			System.out.println(kvmsg.getMsgType());
		} catch (KVException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//fail("Not yet implemented");
	}

	@Test
	public void testKVMessageStringString() {
		try {
			KVMessage kvmsg = new KVMessage("resp", "Error Message");
			System.out.println(kvmsg.toXML());
		} catch (KVException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//fail("Not yet implemented");
	}

	@Test
	public void testKVMessageSocket() {
		//fail("Not yet implemented");
	}

	@Test
	public void testToXML() {
		//fail("Not yet implemented");
	}

	@Test
	public void testSendMessage() {
		//fail("Not yet implemented");
	}

}
