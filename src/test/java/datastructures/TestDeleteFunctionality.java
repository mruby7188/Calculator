package datastructures;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Random;

/**
 * This class should contain all the tests you implement to verify that
 * your 'delete' method behaves as specified.
 *
 * This test _extends_ your TestDoubleLinkedList class. This means that when
 * you run this test, not only will your tests run, all of the ones in
 * TestDoubleLinkedList will also run.
 *
 * This also means that you can use any helper methods defined within
 * TestDoubleLinkedList here. In particular, you may find using the
 * 'assertListMatches' and 'makeBasicList' helper methods to be useful.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDeleteFunctionality extends TestDoubleLinkedList {
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
    
    @Test(timeout=SECOND)
    public void deleteFromEnd() {
        IList<String> list = makeBasicList();
        int init = list.size();
        assertEquals(list.get(list.size()-1), list.delete(list.size() - 1));
        assertEquals(list.size(), init - 1);
    }
    
    @Test(timeout=SECOND)
    public void deleteFromFront() {
        IList<String> list = makeBasicList();
        String init = list.get(1);
        assertEquals(list.get(0), list.delete(0));
        assertEquals(init, list.get(0));       
    }
    
    @Test(timeout=SECOND)
    public void deleteOutsideListThrowsException() {
        IList<String> list = makeBasicList();
        try {
            list.delete(list.size());
            // We didn't throw an exception? Fail now.
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // Do nothing: this is ok
        }
        list = makeBasicList();
        try {
            list.delete(-1);
            // We didn't throw an exception? Fail now.
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // Do nothing: this is ok
        }
    }
    
    @Test(timeout=SECOND)
    public void deleteAllFromFront() {
        IList<String> list = makeBasicList();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            list.delete(0);
        }
        assertEquals(0, list.size());
    }
    
    @Test(timeout=SECOND)
    public void deleteAllFromBack() {
        IList<String> list = makeBasicList();
        int size = list.size();
        for (int i = size - 1; i >= 0; i--) {
            list.delete(i);
        }
        assertEquals(0, list.size());
    }
    
    @Test(timeout=SECOND)
    public void deleteAlternating() {
        IList<String> list = makeBasicList();
        String end = list.get(list.size() - 2);
        int init = (list.size() - 1) / 2;
        for (int i = 0; i < list.size(); i++) {
            list.delete(i);
        }
        assertEquals(init, list.size());
        assertEquals(list.remove(), end);
    }
}
