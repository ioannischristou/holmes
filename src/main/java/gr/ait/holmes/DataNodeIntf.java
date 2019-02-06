/*
 * Code is distibuted as Open Source, under the LGPL2 license, without any waranty of fitness of use.
 */

package gr.ait.holmes;

import java.io.Serializable;
import java.util.List;


/**
 * interface for data nodes, holding the tuples in the Holmes-space.
 * @author itc
 */
public interface DataNodeIntf {

  public Serializable getValueForKey(long primarykey, long secondarykey);
  
  
  public Serializable getValueForKeyIfPresent(long primarykey, 
                                              long secondarykey);
  
  
  public List<ROTupleIntf> getTuplesForRange(KeyRangeSet primarykeys, long secondarykey);
  
  
  public void putTuple(ROTupleIntf tuple);
  
  
  public ROTupleIntf removeTupleForKey(long primarykey, long secondarykey);
  
}

