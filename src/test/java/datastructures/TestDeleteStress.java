package datastructures;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Random;

/**
 * This file should contain any tests that check and make sure your
 * delete method is efficient.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDeleteStress extends TestDoubleLinkedList {
    @Test(timeout=SECOND)
    public void testExample() {
        // Feel free to modify or delete this dummy test.
        assertTrue(true);
        assertEquals(3, 3);
    }
    
    @Test(timeout=SECOND)
    public void testManyDeletes() {
        int cap = 1000;
        int stringLength = 100;
        String validChars = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random rand = new Random();
        rand.setSeed(12345);

        IList<String> list = new DoubleLinkedList<>();
        IList<String> refList = new DoubleLinkedList<>();

        for (int i = 0; i < cap; i++) {
            String entry = "";
            for (int j = 0; j < stringLength; j++) {
                int charIndex = rand.nextInt(validChars.length());
                entry += validChars.charAt(charIndex);
            }

            list.add(entry);
            if (i % 100 == 0) {
                refList.add(entry);
            }
        }
        for (int i = 0; i < refList.size(); i++) {

                String entry = refList.delete(i);
                assertEquals(2 * i * 100, list.indexOf(entry));
                assertFalse(refList.contains(entry));
        }
    }         
    
    @Test(timeout=SECOND)
    public void testNullDelete() {
        IList<Integer> list = new DoubleLinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.set(2, null);
        list.delete(2);
        
        assertListMatches(new Integer[]{1, 2, 4}, list);
    }
    
    @Test(timeout= SECOND)
    public void testIteratorAndDelete() {
        int cap = 1000;
        int stringLength = 100;
        
        String validChars = "abcdefghijklmnopqrstuvwxyz0123456789";
        
        Random rand = new Random();
        rand.setSeed(12345);

        IList<String> list = new DoubleLinkedList<>();

        for (int i = 0; i < 5; i++) {
            for (int l = 0; l < cap; l++) {
                String entry = "";
                for (int m = 0; m < stringLength; m++) {
                    int charIndex = rand.nextInt(validChars.length());
                    entry += validChars.charAt(charIndex);
                }

                list.add(entry);
            }
            Iterator<String> iter = list.iterator();
            for (int j = 1; j < list.size(); j++) {
                list.delete(j);
                for (int k = 0; k < 5; k++) {
                    assertTrue(iter.hasNext());
                }
                assertEquals(list.get(j - 1), iter.next());
            }
        }
    }
}
