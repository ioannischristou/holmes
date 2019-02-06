/*
 * Code is distibuted as Open Source, under the LGPL2 license, without any waranty of fitness of use.
 */

package gr.ait.holmes;

/**
 * represents a single compact closed from both ends range of primary keys.
 * @author itc
 */
public class KeyRange {
  private final long _start;
  private final long _end;
  
  
  /**
   * sole constructor.
   * @param start long
   * @param end long
   */
  public KeyRange(long start, long end) {
    _start = start;
    _end = end;
  }
  
  
  /**
   * return the starting (inclusive) point of this range.
   * @return long
   */
  public long getStart() {
    return _start;
  }
  
  
  /**
   * return the ending (inclusive) point of this range.
   * @return long
   */
  public long getEnd() {
    return _end;
  }
  
  
  /**
   * return a new iterator over the numbers of this range.
   * @return KeyRangeIterator
   */
  public KeyRangeIterator iterator() {
    return new KeyRangeIterator(_start,_end);
  }
}
