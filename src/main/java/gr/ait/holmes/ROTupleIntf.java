/*
 * Code is distibuted as Open Source, under the LGPL2 license, without any waranty of fitness of use.
 */

package gr.ait.holmes;

import java.io.Serializable;


/**
 * Base Tuple definition in Holmes. A <CODE>ROTupleIntf</CODE> object specifies 
 * a key that is 64-bit long, together with an optional secondary key that is 
 * also a 64-bit long, and of course, its associated value. Base tuples are
 * Read-Only, meaning that once they are in tuple-space (Holmes-space) their 
 * value object cannot be modified. Keys are always read-only.
 * 
 * @author Ioannis T. Christou
 */
public interface ROTupleIntf extends Serializable {
  
  /**
   * get the secondary key for this tuple object.
   * @return long default is zero
   */
  default public long getSecondaryKey() {
    return 0;
  }
  
  /**
   * get the primary key for this tuple.
   * @return long
   */
  public long getKey();
  
  
  /**
   * return the value of this object.
   * @return Serializable
   */
  public Serializable getObject();
  
}

