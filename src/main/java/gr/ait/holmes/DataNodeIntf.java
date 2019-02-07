/*
 * Code is distibuted as Open Source, under the LGPL2 license, without any waranty of fitness of use.
 */
package gr.ait.holmes;

import gr.ait.holmes.exceptions.*;
import java.io.Serializable;
import java.util.List;

/**
 * interface for data nodes, holding the tuples in the Holmes-space.
 *
 * @author itc
 */
public interface DataNodeIntf {

  public Serializable getValue(long primarykey, long secondarykey) throws NotInRange;

  public Serializable getValueIfPresent(long primarykey,
          long secondarykey) throws NotInRange;

  public List<Serializable> getValuesForRange(KeyRangeSet primarykeys, long secondarykey) throws NotInRange;

  public void putTuple(long primarykey, long secondarykey, Serializable value) throws NotInRange;

  public Serializable removeTuple(long primarykey, long secondarykey) throws NotInRange;

  public void save();
}
