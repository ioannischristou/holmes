/*
 * Code is distibuted as Open Source, under the LGPL2 license, without any warranty of fitness of use.
 */
package gr.ait.holmes;

import java.util.List;
import java.util.ArrayList;

/**
 * represents a set of contiguous but disconnected ranges of long numbers.
 *
 * @author itc
 */
public class KeyRangeSet {

  private ArrayList<KeyRange> _ranges;

  public KeyRangeSet(long start, long end) {
    _ranges = new ArrayList<>();
    _ranges.add(new KeyRange(start, end));
  }

  public KeyRangeSet(List<KeyRange> ranges) {
    _ranges = new ArrayList<KeyRange>(ranges);
  }

  public void addRange(KeyRange range) {
    _ranges.add(range);
  }

  public int getNumRanges() {
    return _ranges.size();
  }

  public KeyRange getRange(int i) {
    return _ranges.get(i);
  }

}
