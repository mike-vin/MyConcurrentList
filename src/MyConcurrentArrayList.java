import com.sun.istack.internal.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class MyConcurrentArrayList<T> implements List<T>, Serializable, Cloneable {
  private volatile Object[] MAIN_ARRAY = new Object[1_0/*00_000*/];
  private Lock LOCK = new ReentrantLock();// for example!

  public MyConcurrentArrayList() {
  }

  public MyConcurrentArrayList(int range) {
    synchronized (MAIN_ARRAY) {
      MAIN_ARRAY = new Object[range];
    }
  }

  @Override
  public int size() {
    synchronized (MAIN_ARRAY) {
      int count = 0;
      for (int i = 0; i < MAIN_ARRAY.length; i++) {
        if (MAIN_ARRAY[i] != null) count++;
        else break;
      }
      return count;
    }
  }

  @Override
  public boolean isEmpty() {
    synchronized (MAIN_ARRAY) {
      return this.size() == 0;
    }
  }

  @Override
  public boolean contains(@NotNull Object o) {
    Objects.requireNonNull(o); // no null here, suckers!
    synchronized (MAIN_ARRAY) {
      for (int i = 0; i < size(); i++) if (MAIN_ARRAY[i].equals(o)) return true;
    }
    return false;
  }

  @Override
  public Iterator<T> iterator() {
    return new MyIterator();
  }

  @Override
  public Object[] toArray() {
    synchronized (MAIN_ARRAY) {
      return Arrays.copyOf(MAIN_ARRAY, size());
    }
  }

  @Override
  public <T1> T1[] toArray(@NotNull T1[] inputArr) {
    Objects.requireNonNull(inputArr);
    synchronized (MAIN_ARRAY) {
      int ourSize = size();
      if (inputArr.length < ourSize)// new arr of inputArr & add all content
        return (T1[]) Arrays.copyOf(MAIN_ARRAY, ourSize, inputArr.getClass());
      System.arraycopy(MAIN_ARRAY, 0, inputArr, 0, ourSize);

      return inputArr;
    }
  }

  @Override
  public boolean add(@NotNull T inputObject) {
    LOCK.lock(); // for example!
    Objects.requireNonNull(inputObject);
    int size = size();
    if (size == MAIN_ARRAY.length - 1) {
      increaseMainArrSize(1);
      MAIN_ARRAY[size] = inputObject;
    } else MAIN_ARRAY[size] = inputObject;
    boolean isSuccess = MAIN_ARRAY[size() - 1] == inputObject;
    LOCK.unlock();// for example!
    return isSuccess; // <- check

  }

  private void increaseMainArrSize(int size) {
    int newSize = MAIN_ARRAY.length + size + MAIN_ARRAY.length / 3;
    MAIN_ARRAY = Arrays.copyOf(MAIN_ARRAY, newSize);
  }

  @Override
  public boolean remove(@NotNull Object o) {
    Objects.requireNonNull(o);
    synchronized (MAIN_ARRAY) {
      for (int i = 0; i < size(); i++) {

        if (MAIN_ARRAY[i].equals(o)) {

          for (int j = i; j < size(); j++) {
            if (j != MAIN_ARRAY.length - 1) MAIN_ARRAY[j] = MAIN_ARRAY[j + 1];
            else MAIN_ARRAY[MAIN_ARRAY.length - 1] = null;
          }

        }
      }
      return !contains(o);
    }
  }

  @Override
  public boolean containsAll(@NotNull Collection<?> inputCollection) {
    Objects.requireNonNull(inputCollection);
    synchronized (MAIN_ARRAY) {
      for (Object innerObj : inputCollection)
        if (!contains(innerObj)) return false;
      return true;
    }
  }

  @Override
  public boolean addAll(@NotNull Collection<? extends T> inputCollection) {
    Objects.requireNonNull(inputCollection);
    synchronized (MAIN_ARRAY) {
      int ourSize = size();
      Object[] inputARR = inputCollection.toArray();
      if ((MAIN_ARRAY.length - ourSize - inputCollection.size()) > 0) { // can contain all
        for (Object innerobject : inputARR) add((T) innerobject);
        return true;
      } else {
        increaseMainArrSize(inputARR.length);
        for (Object innerobject : inputARR) add((T) innerobject);
        return true;
      }
    }
  }

  @Override
  public boolean addAll(int index, @NotNull Collection<? extends T> inputCollection) {
    Objects.requireNonNull(inputCollection);
    synchronized (MAIN_ARRAY) {
      int ourSize = size();
      if (index == ourSize) throw new IndexOutOfBoundsException("index = " + index + ", list size = " + size());

      if (MAIN_ARRAY.length - ourSize - inputCollection.size() < 0) increaseMainArrSize(inputCollection.size());

      Object[] inputArr = inputCollection.toArray();
      Object[] tempArrData = new Object[ourSize - index];

      for (int i = 0; i < tempArrData.length; i++) // saving old data till index (including)
        tempArrData[i] = MAIN_ARRAY[index + i];

      for (int i = 0; i < inputArr.length; i++)// writing new data till index (including)
        if (inputArr[i] != null) MAIN_ARRAY[index + i] = inputArr[i];// no null here!

      ourSize = size();// get relevant new size
      for (int i = 0; i < tempArrData.length; i++)
        MAIN_ARRAY[ourSize + i] = tempArrData[i];
      return true;
    }
  }

  @Override
  public boolean removeAll(@NotNull Collection<?> c) {
    Objects.requireNonNull(c);
    synchronized (MAIN_ARRAY) {
      Object[] inputArr = c.toArray();
      for (int i = 0; i < inputArr.length; i++)
        remove(inputArr[i]);
      return true;
    }
  }

  @Override
  public boolean retainAll(@NotNull Collection<?> inputCollection) { // delete all not equals
    Objects.requireNonNull(inputCollection);
    synchronized (MAIN_ARRAY) {
      for (int i = 0; i < size(); i++)
        if (!inputCollection.contains(MAIN_ARRAY[i])) remove(MAIN_ARRAY[i]);
      return containsAll(inputCollection);
    }
  }

  @Override
  public void clear() {
    synchronized (MAIN_ARRAY) {
      MAIN_ARRAY = new Object[MAIN_ARRAY.length];
    }
  }

  @Override
  public T get(int index) {
    synchronized (MAIN_ARRAY) {
      if (index >= size()) throw new IndexOutOfBoundsException("index = " + index + ", list size = " + size());
      return (T) MAIN_ARRAY[index];
    }
  }

  @Override
  public T set(int index, T element) {
    synchronized (MAIN_ARRAY) {
      Objects.requireNonNull(element);
      if (index >= size()) throw new IndexOutOfBoundsException("index = " + index + ", list size = " + size());
      MAIN_ARRAY[index] = element;
      return (T) MAIN_ARRAY[index];
    }
  }

  @Override
  public void add(int index, T element) {
    Objects.requireNonNull(element);
    synchronized (MAIN_ARRAY) {
      if (index > size() - 1) throw new IndexOutOfBoundsException("index = " + index + ", list size = " + size());
      if (index == size()) {
        MAIN_ARRAY[index + 1] = element;
        return;
      }

      int ourSize = size();
      Object[] tempArr = new Object[ourSize - index];
      for (int i = 0; i < tempArr.length; i++)
        tempArr[i] = MAIN_ARRAY[index + i];

      MAIN_ARRAY[index] = element; // <- add element.

      for (int i = 0; i < tempArr.length; i++)
        MAIN_ARRAY[index + 1] = tempArr[i];
    }
  }

  @Override
  public T remove(int index) {
    synchronized (MAIN_ARRAY) {
      if (index >= size()) throw new IndexOutOfBoundsException("index = " + index + ", list size = " + size());
      Object removingObject = MAIN_ARRAY[index]; // to return.

      if (index == size()) {//last one
        MAIN_ARRAY[index] = null;
        return (T) removingObject;
      }

      int size = size();
      for (int i = index; i < size; i++)
        if (i != MAIN_ARRAY.length - 1) MAIN_ARRAY[i] = MAIN_ARRAY[++index];
        else MAIN_ARRAY[MAIN_ARRAY.length - 1] = null;
      return (T) removingObject;
    }
  }

  @Override
  public int indexOf(Object o) {
    synchronized (MAIN_ARRAY) {

      int size = size();
      for (int i = 0; i < size; i++)
        if (o.equals(MAIN_ARRAY[i]) || MAIN_ARRAY[i] == o) return i;
      return -1;
    }
  }

  @Override
  public int lastIndexOf(Object o) {
    synchronized (MAIN_ARRAY) {
      Objects.requireNonNull(o);
      int size = size() - 1;
      for (int i = size; i >= 0; i--)
        if (MAIN_ARRAY[i].equals(o) || MAIN_ARRAY[i] == o) return i;
      return -1;
    }
  }

  @Override
  public ListIterator<T> listIterator() {
    return new MyListIterator();
  }

  @Override
  public ListIterator<T> listIterator(int index) {
    return new MyListIterator(index);
  }

  @Override
  public List<T> subList(int fromIndex, int toIndex) {
    synchronized (MAIN_ARRAY) {

      if (toIndex > size() || fromIndex > toIndex || fromIndex < 0)
        throw new IndexOutOfBoundsException("fromIndex = " + fromIndex + ", fromIndex = " + fromIndex + " size = " + size());
      List<T> temp = new MyConcurrentArrayList<T>();
      for (int i = fromIndex; i < toIndex; i++)
        temp.add((T) MAIN_ARRAY[i]);
      return temp;
    }
  }

  //______________________________________________________________________________________________________________
//-------------------------------------------------------------------------------------------------------------
  private class MyIterator implements Iterator<T> {
    protected volatile int cursor = -1;

    @Override
    public boolean hasNext() {
      synchronized (MAIN_ARRAY) {
        return MAIN_ARRAY[cursor + 1] != null;
      }
    }

    @Override
    public T next() {
      synchronized (MAIN_ARRAY) {
        if (!hasNext()) throw new IndexOutOfBoundsException("HASN'T NEXT! size is " + size());
        return (T) MAIN_ARRAY[++cursor];
      }
    }

    @Override
    public void remove() {
      synchronized (MAIN_ARRAY) {
        int size = size();
        for (int i = cursor; i <= size; )
          MAIN_ARRAY[i] = MAIN_ARRAY[++i];
      }
    }
  }


  //______________________________________________________________________________________________________________
  private class MyListIterator extends MyIterator implements ListIterator<T> {
    public MyListIterator() {
    }

    public MyListIterator(int index) {
      synchronized (MAIN_ARRAY) {
        if (index < 0 || index > size())
          throw new IndexOutOfBoundsException("index is " + index + " size is " + size());
        super.cursor = index;
      }
    }

    @Override
    public boolean hasPrevious() {
      synchronized (MAIN_ARRAY) {
        return cursor > 0;
      }
    }

    @Override
    public T previous() {
      synchronized (MAIN_ARRAY) {
        return hasPrevious() ? (T) MAIN_ARRAY[--cursor] : null;
      }
    }

    @Override
    public int nextIndex() {
      synchronized (MAIN_ARRAY) {
        if (MAIN_ARRAY[cursor + 1] == null) throw new IndexOutOfBoundsException("HASN'T next! size is " + size());
        return  cursor + 1;
      }
    }

    @Override
    public int previousIndex() {
      synchronized (MAIN_ARRAY) {
        if (cursor <=0) throw new IndexOutOfBoundsException("HASN'T previous ! size is " + size());
        return cursor - 1 ;
      }
    }

    @Override
    public void set(T t) {
      synchronized (MAIN_ARRAY) {
        MAIN_ARRAY[cursor] = t;
      }
    }

    @Override
    public void add(T t) {
      if (cursor == -1) MyConcurrentArrayList.this.add(t);
      else MyConcurrentArrayList.this.add(cursor, t);
    }
  }

  @Override
  public boolean equals(Object o) {
    synchronized (MAIN_ARRAY) {

      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      MyConcurrentArrayList<?> that = (MyConcurrentArrayList<?>) o;

      // Probably incorrect - comparing Object[] arrays with Arrays.equals
      return Arrays.equals(MAIN_ARRAY, that.MAIN_ARRAY);
    }
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(MAIN_ARRAY);
  }

  @Override
  public String toString() {
    synchronized (MAIN_ARRAY) {
      int size = size();
      StringBuilder stringBuilder = new StringBuilder();
      for (int i = 0; i < size; i++) stringBuilder.append(MAIN_ARRAY[i] + "\n");
      return stringBuilder.toString();
    }
  }

  @Override
  protected MyConcurrentArrayList<T> clone() throws CloneNotSupportedException {
    synchronized (MAIN_ARRAY) {
      Object[] clonedCopy = Arrays.copyOf(MAIN_ARRAY, size());
      MyConcurrentArrayList<T> cloneCopyList = new MyConcurrentArrayList<>();
      for (Object bject : clonedCopy) cloneCopyList.add((T) bject);
      return cloneCopyList;
    }
  }
}
