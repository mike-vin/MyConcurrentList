import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.*;

class MyConcurrentArrayListTest {
  static MyConcurrentArrayList<Integer> intList;
  static MyConcurrentArrayList<String> stringList;

  @BeforeAll
  static void before() {
    intList = new MyConcurrentArrayList<>();
    stringList = new MyConcurrentArrayList<>();
  }

  @Test
  void size() {
    intList.clear();

    assertTrue(0 == intList.size());
    intList.add(99);
    assertFalse(intList.get(0) == null);
    assertFalse(intList.get(0) == 11);

    intList.clear();
  }

  @Test
  void isEmpty() {
    intList.clear();

    assertTrue(intList.isEmpty());
    intList.add(99);
    assertFalse(intList.isEmpty());
    assertTrue(99 == intList.get(0));

    intList.clear();
  }

  @Test
  void contains() {
    intList.clear();

    intList.add(99);
    assertTrue(intList.contains(99));
    assertFalse(intList.contains(66));

    intList.clear();
  }

  @Test
  void iterator() {
    intList.clear();

    intList.add(99);
    Iterator<Integer> iterator = intList.iterator();

    assertNotNull(iterator);
    assertTrue(iterator.hasNext());

    assertTrue(iterator.next() == 99);
    try {
      assertFalse(iterator.hasNext());
      iterator.next();
    } catch (Exception e) {
      assertEquals(e.getClass(), IndexOutOfBoundsException.class);
    }
    iterator.remove();
    assertTrue(intList.isEmpty());

    intList.clear();
  }


  @Test
  void listIterator() {
    intList.clear();

    ListIterator<Integer> listIterator = intList.listIterator();
    assertFalse(listIterator.hasPrevious());
    assertFalse(listIterator.hasNext());
    try {
      listIterator.previousIndex();
    } catch (Exception e) {
      assertEquals(e.getClass(), IndexOutOfBoundsException.class);
    }
    try {
      listIterator.nextIndex();
    } catch (Exception e) {
      assertEquals(e.getClass(), IndexOutOfBoundsException.class);
    }
    try {
      intList.listIterator(1);
    } catch (Exception e) {
      assertEquals(e.getClass(), IndexOutOfBoundsException.class);
    }

    listIterator.add(123);
    listIterator.add(456);
    listIterator.add(789);
    assertTrue(listIterator.hasNext());
    assertTrue(listIterator.next() == 123);

    assertTrue(listIterator.hasNext());
    assertTrue(listIterator.next() == 456);

    assertTrue(listIterator.hasNext());
    assertTrue(listIterator.next() == 789);


    assertTrue(listIterator.hasPrevious());
    assertTrue(listIterator.previous() == 456);

    assertTrue(listIterator.hasPrevious());
    assertTrue(listIterator.previous() == 123);

    assertFalse(listIterator.hasPrevious());
    try {
      listIterator.previous();
    } catch (Exception e) {
      assertEquals(e.getClass(), IndexOutOfBoundsException.class);
    }

    listIterator = intList.listIterator(2);
    listIterator.add(777);// new value to index 2
    assertTrue(intList.size() == 4);// new size!
    assertTrue(listIterator.next() == 789); // moved right
    assertTrue(listIterator.previous() == 777);//index 2

    intList.clear();
  }


  @Test
  void toArray() {
    intList.clear();

    intList.add(111);
    Object[] objects = intList.toArray();
    assertTrue(objects[0].equals(111));

    intList.clear();
  }

  @Test
  void toSomeArray() {
    intList.clear();

    intList.add(777); // init...
    intList.add(99); // init...
    intList.add(66);
    intList.add(11);
    intList.add(0);

    Integer[] testArr = new Integer[2];
    Integer[] newIntegers = intList.toArray(testArr);
    //System.out.println(Arrays.toString(newIntegers));
    intList.clear();
  }

  @Test
  void add() {
    intList.clear();

    assertTrue(intList.add(1987));
    assertTrue(intList.get(0) == 1987);

    intList.clear();
    for (int i = 0; i < 120; i++)  // in testing mode MyConcurrentArrayList.length  is 10 !
      intList.add(i);

    assertTrue(intList.size() > 11);
    assertTrue(intList.size() == 120);

    intList.clear();
  }

  @Test
  void removeByIndex() {
    intList.clear();

    assertTrue(intList.add(1987));
    assertTrue(intList.remove(0) == 1987);
    try {
      intList.remove(0);
    } catch (Exception e) {
      assertEquals(e.getClass(), IndexOutOfBoundsException.class);
    }

    intList.clear();
  }

  @Test
  void removeByObject() {
    stringList.clear();

    assertTrue(stringList.add("new ADD"));
    assertTrue(stringList.remove("new ADD"));
    assertFalse(stringList.contains("new ADD"));
    assertTrue(stringList.size() == 0);

    for (int i = 0; i < 10; i++)
      assertTrue(stringList.add("new ADD-" + i));

    assertEquals(stringList.remove(0), "new ADD-0");
    assertTrue(stringList.remove(2).equals("new ADD-3"));
    assertEquals(stringList.remove(2), "new ADD-4");
    assertNotEquals(stringList.remove(2), "new ADD-4");
    assertEquals(stringList.get(stringList.size() - 1), "new ADD-9");

    while (stringList.size() > 0) stringList.remove(0);
    try {
      stringList.remove(0);
    } catch (Exception e) {
      assertEquals(e.getClass(), IndexOutOfBoundsException.class);
    }

    stringList.clear();
  }

  @Test
  void containsAll() {
    intList.clear();

//int test>
    List<Integer> testTempList1 = new ArrayList<>();
    List<Integer> testTempList2 = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      intList.add(i);
      if (i % 2 == 0) testTempList1.add(i);
      else testTempList2.add(i);
    }
    assertTrue(intList.containsAll(testTempList1));
    assertTrue(intList.containsAll(testTempList2));

    intList.clear();
  }

  @Test
  void addAll() {
    intList.clear();

//int test>
    List<Integer> testTempList1 = new ArrayList<>();
    List<Integer> testTempList2 = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      intList.add(i * -1);
      if (i % 2 == 0) testTempList1.add(i);
      else testTempList2.add(i * 100);
    }

    assertFalse(intList.containsAll(testTempList1));
    assertFalse(intList.containsAll(testTempList2));

    assertTrue(intList.addAll(testTempList1));
    assertTrue(intList.addAll(testTempList2));
    //System.out.println(intList);
    assertTrue(intList.containsAll(testTempList1));
    assertTrue(intList.containsAll(testTempList2));

    intList.clear();
  }

  @Test
  void addAll1() {
    intList.clear();

    List<Integer> testList = new ArrayList<>();
    for (int i = 0; i < 128; i++) { // in testing mode MyConcurrentArrayList.length  is 10 !
      testList.add(i);
      intList.add(i * -1);
    }
    assertTrue(intList.size() == 128);

    intList.addAll(100, testList);
    int size = intList.size();
    //System.out.println(size);
    assertTrue(size == 256);

    //System.out.println(intList);

    intList.clear();
  }

  @Test
  void removeAll() {
    intList.clear();

//int test>
    List<Integer> testTempList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      intList.add(i);
      if (i % 2 == 0) testTempList.add(i);
    }
    assertTrue(intList.removeAll(testTempList));

    assertTrue(intList.get(0) == 1);
    assertTrue(intList.get(1) == 3);
    assertTrue(intList.get(1) == 3);
    assertTrue(intList.get(2) == 5);
    assertTrue(intList.get(3) == 7);
    assertTrue(intList.get(4) == 9);
    assertTrue(intList.size() == 5);

    intList.clear();
  }

  @Test
  void retainAll() {
    intList.clear();

//int test>
    List<Integer> testTempList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      intList.add(i);
      if (i % 2 == 0) testTempList.add(i);
    }
    assertTrue(intList.retainAll(testTempList));

    //System.out.println(intList);
    assertFalse(intList.get(0) == 1);
    assertFalse(intList.get(1) == 3);
    assertFalse(intList.get(0) == 2);
    assertFalse(intList.get(2) == 5);
    assertFalse(intList.get(3) == 7);
    assertFalse(intList.get(4) == 9);
    assertFalse(intList.size() == 10);

    assertTrue(intList.get(0) == 0);
    assertTrue(intList.get(1) == 2);
    assertTrue(intList.get(2) == 4);
    assertTrue(intList.get(3) == 6);
    assertTrue(intList.get(4) == 8);
    assertTrue(intList.size() == 5);


    intList.clear();
  }

  @Test
  void clear() {
    intList.clear();
    stringList.clear();

//int test>
    intList.add(123);
    intList.add(321);
    intList.add(456);
    intList.add(654);
    intList.clear();
    assertTrue(intList.size() == 0);

// strings test>
    for (int i = 0; i < 10; i++)
      assertTrue(stringList.add("new ADD-" + i));

    stringList.clear();
    assertTrue(stringList.size() == 0);

    stringList.clear();
    intList.clear();
  }

  @Test
  void get() {
    intList.clear();
    stringList.clear();

//int test>
    for (int i = 0; i < 10; i++)
      intList.add(i);

    assertTrue(intList.get(0) == 0);
    assertTrue(intList.get(intList.size() - 1) == 9);

//strings test>
    for (int i = 0; i < 10; i++)
      assertTrue(stringList.add("new ADD-" + i));

    assertTrue(stringList.get(0).equals("new ADD-0"));
    assertEquals(stringList.get(stringList.size() - 1), "new ADD-9");

    stringList.clear();
    intList.clear();
  }

  @Test
  void set() {
    intList.clear();
    stringList.clear();

//int test>
    for (int i = 0; i < 10; i++)
      intList.add(i);

    for (int i = 0; i < 10; i++)
      if (i % 2 == 0) intList.set(0, i);
      else intList.set(1, i);

    assertTrue(intList.get(0) == 8);
    assertTrue(intList.get(1) == 9);
    assertTrue(intList.get(intList.size() - 1) == 9);

//strings test>
    for (int i = 0; i < 10; i++)
      assertTrue(stringList.add("new ADD-" + i));

    for (int i = 0; i < 10; i++)
      if (i % 2 == 0) stringList.set(0, "new ADD-" + i);
      else stringList.set(1, "new ADD-" + i);

    assertTrue(stringList.get(0).equals("new ADD-8"));
    assertTrue(stringList.get(1).equals("new ADD-9"));
    assertEquals(stringList.get(stringList.size() - 1), "new ADD-9");

    stringList.clear();
    intList.clear();
  }

  @Test
  void add1() {
    intList.clear();
    stringList.clear();

//int test>
    for (int i = 0; i < 10; i++)
      intList.add(i);
    for (int i = 3; i < 9; i++)
      intList.add(i, 111);

    assertTrue(intList.get(0) == 0);
    assertTrue(intList.get(2) == 2);
    assertTrue(intList.get(3) == 111);
    assertTrue(intList.get(8) == 111);
    assertTrue(intList.get(9) == 9);


//strings test>
    for (int i = 0; i < 10; i++)
      assertTrue(stringList.add("new ADD-" + i));
    for (int i = 3; i < 9; i++)
      stringList.add(i, "new ADD-" + 111);
    assertEquals(stringList.get(0), "new ADD-0");
    assertEquals(stringList.get(2), "new ADD-2");
    assertEquals(stringList.get(3), "new ADD-111");
    assertNotEquals(stringList.get(6), "new ADD-6");//False
    assertEquals(stringList.get(8), "new ADD-111");
    assertEquals(stringList.get(9), "new ADD-9");

    intList.clear();
    stringList.clear();
  }

  @Test
  void indexOf() {
    intList.clear();
    stringList.clear();

//int test>
    for (int i = 10; i > 0; i--)
      intList.add(i);

    //System.out.println(intList.indexOf(10));
    assertTrue(intList.indexOf(10) == 0);
    assertFalse(intList.indexOf(3) == 4); // False
    assertTrue(intList.indexOf(6) == 4); // True
    assertTrue(intList.indexOf(1) == 9);
    assertTrue(intList.indexOf(intList.size() - 1) == 1); //(index-9 = 1)

//strings test>
    for (int i = 10; i > 0; i--)
      assertTrue(stringList.add("new ADD-" + i));

    //System.out.println(Arrays.toString(stringList.toArray()));
    assertFalse(stringList.indexOf("new ADD-9") == 9);//False
    assertTrue(stringList.indexOf("new ADD-8") == 2);
    assertTrue(stringList.indexOf("new ADD-3") == 7);
    assertTrue(stringList.indexOf("new ADD-7") == 3);

    assertEquals(stringList.get(stringList.indexOf("new ADD-1")), "new ADD-1");

    intList.clear();
    stringList.clear();
  }

  @Test
  void lastIndexOf() {
    intList.clear();
    stringList.clear();

//int test>
    for (int i = 10; i > 0; i--)
      intList.add(i - i);


    assertTrue(intList.indexOf(0) == 0);
    assertFalse(intList.indexOf(0) == 9); // False
    assertTrue(intList.lastIndexOf(0) == 9);
    assertTrue(intList.get(5) == 0); //(index-9 = 1)

//strings test>
    for (int i = 10; i > 0; i--)
      assertTrue(stringList.add("new ADD"));

    assertTrue(stringList.lastIndexOf("new ADD") == 9);//main one!
    // not need here>
    assertTrue(stringList.indexOf("new ADD") == 0);
    assertFalse(stringList.indexOf("new ADD") == 9); // False
    assertEquals(stringList.get(5), "new ADD");

    intList.clear();
    stringList.clear();
  }

  @Test
  void subList() {
    intList.clear();

    //int test>
    List<Integer> testList = new ArrayList<>();
    for (int i = 0; i < 30; i++) {
      intList.add(i);
      if (i >= 3 && i <= 17) testList.add(i);
    }
    List<Integer> subList = intList.subList(3, 17);
    for (int i = 0; i < subList.size(); i++) {
      assertTrue(subList.get(i) == testList.get(i));
      assertEquals(subList.get(i), testList.get(i));
    }
    intList.clear();
  }

  @Test
  void syncTest() throws InterruptedException {

    intList = new MyConcurrentArrayList<>(10_000);
    Runnable addTask = () -> {
      for (int i = 0; i < 100; i++) intList.add(i);
    };

    for (int i = 0; i < 100; i++)
      new Thread(addTask).start();

    Thread.sleep(1234);
    System.out.println("size = " + intList.size());
    assertTrue(intList.size() == 10_000);
    intList.clear();
  }

  @Test
  void cloneInt() throws CloneNotSupportedException {
    intList.clear();
    for (int i = 0; i < 30; i++)
      intList.add(i);
    MyConcurrentArrayList<Integer> clonedList = intList.clone();
    clonedList.set(0, 789);
    clonedList.set(1, 456);
    clonedList.set(2, 123);

    assertFalse(intList.get(0) == clonedList.get(0));
    assertNotEquals(intList.get(0), clonedList.get(0));

    assertFalse(intList.get(1) == clonedList.get(1));
    assertNotEquals(intList.get(1), clonedList.get(1));

    assertFalse(intList.get(2) == clonedList.get(2));
    assertNotEquals(intList.get(0), clonedList.get(0));

    assertTrue(intList.get(3) == clonedList.get(3));
    assertEquals(intList.get(3), clonedList.get(3));

    intList.clear();
  }

  @Test
  void cloneString() throws CloneNotSupportedException {
    stringList.clear();
    for (int i = 0; i < 30; i++)
      stringList.add("stringList-i");
    MyConcurrentArrayList<String> clonedList = stringList.clone();
    clonedList.set(0, "stringList" + 789);
    clonedList.set(1, "stringList" + 456);
    clonedList.set(2, "stringList" + 123);

    assertFalse(stringList.get(0) == clonedList.get(0));
    assertNotEquals(stringList.get(0), clonedList.get(0));

    assertFalse(stringList.get(1) == clonedList.get(1));
    assertNotEquals(stringList.get(1), clonedList.get(1));

    assertFalse(stringList.get(2) == clonedList.get(2));
    assertNotEquals(stringList.get(0), clonedList.get(0));

    assertTrue(stringList.get(3) == clonedList.get(3));
    assertEquals(stringList.get(3), clonedList.get(3));

    stringList.clear();
  }
}