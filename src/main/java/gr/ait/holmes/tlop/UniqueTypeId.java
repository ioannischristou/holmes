/*
 * Code is distibuted as Open Source, under the LGPL2 license, without any waranty of fitness of use.
 */

package gr.ait.holmes.tlop;

/**
 *
 * @author itc
 */
public final class UniqueTypeId {
  static int _id = -1;
  
  public static synchronized int getNextId() {
    if (_id==PoolableObjectFactoryIntf._MAX_NUM_POOLED_TYPES)
      throw new IllegalStateException("No more unique type ids available");
    return ++_id;
  }
}

