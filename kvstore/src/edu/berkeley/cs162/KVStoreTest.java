package edu.berkeley.cs162;

import static org.junit.Assert.*;

import org.junit.Test;

public class KVStoreTest {

    @Test
    public void testDumpToFile() {
        KVStore kvStore = new KVStore();
        try {
            kvStore.put("21", "Tim Duncan");
            kvStore.put("9", "Tony Parker");
            kvStore.put("20", "Manu Ginobili");
            kvStore.put("4", "Danny Green");
            kvStore.put("2", "Kawhi Leonard");
            
            String fileName = new String("/home/ray/SpursRoster.xml");
            System.out.println("Dump Spurs roster");
            kvStore.dumpToFile(fileName);
        } catch (KVException e) {
            System.out.println(e.getMsg());
        }
    }

    @Test
    public void testRestoreFromFile() {
        //fail("Not yet implemented");
        String fileName = new String("/home/ray/SpursRoster.xml");
        System.out.println("Read Spurs roster");
        try {
            KVStore kvStore = new KVStore();
            kvStore.restoreFromFile(fileName);
            System.out.println(kvStore.toXML());
        } catch (Exception e) {
            System.out.println(e);
        }  
    }

}
