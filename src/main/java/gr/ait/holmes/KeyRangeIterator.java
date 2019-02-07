/*
 * Code is distibuted as Open Source, under the LGPL2 license, without any waranty of fitness of use.
 */
package gr.ait.holmes;

/**
 * auxiliary class for iterating over a contiguous range of keys.
 *
 * @author itc
 */
public class KeyRangeIterator {

  private final long _start;
  private final long _end;
  private volatile long _current;

  /**
   * sole constructor.
   *
   * @param start long
   * @param end long
   */
  KeyRangeIterator(long start, long end) {
    _start = start;
    _end = end;
    _current = start - 1;
  }

  /**
   * obtain the next long in the iteration.
   *
   * @return long
   * @throws IllegalStateException if the iteration has ended, and the method is
   * called again (unchecked).
   */
  public long next() {
    ++_current;
    if (_current > _end) {
      throw new IllegalStateException("Iter out of bounds");
    }
    return _current;
  }

  /**
   * tests if the iteration is over or not.
   *
   * @return boolean
   */
  public boolean hasNext() {
    return _current < _end;
  }

}
