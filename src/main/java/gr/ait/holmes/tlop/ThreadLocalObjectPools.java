/*
 * Code is distibuted as Open Source, under the LGPL2 license, without any waranty of fitness of use.
 */
package gr.ait.holmes.tlop;

/**
 * helper access class for initializing and accessing
 * <CODE>ThreadLocalObjectPool</CODE>'s.
 *
 * @author itc
 */
public final class ThreadLocalObjectPools {

  private static boolean _poolSizeResetAllowed = true;

  private static ThreadLocal<ThreadLocalObjectPool[]> _poolsMap
          = new ThreadLocal();

  
  /**
   * deletes the thread-local pool of object of type T for the current thread,
   * if such a pool exists.
   *
   * @param <T>
   * @param f
   */
  public static <T extends PoolableObject> void
          deleteThreadLocalPool(PoolableObjectFactoryIntf<T> f) {
    ThreadLocalObjectPool[] p = (ThreadLocalObjectPool[]) _poolsMap.get();
    if (p != null && p[f.getUniqueTypeId()] != null) {
      p[f.getUniqueTypeId()] = null;
    }
  }

          
  /**
   * get the thread-local pool of objects of type T for the current thread. If
   * the pool doesn't yet exist, it's created first.
   *
   * @param <T>
   * @param f a factory for managed as well as unmanaged objects of type T
   * @param args any arguments needed by f to construct the T objects
   * @return
   */
  static <T extends PoolableObject> ThreadLocalObjectPool<T>
          getThreadLocalPool(PoolableObjectFactoryIntf<T> f, Object... args) {
    ThreadLocalObjectPool[] p = (ThreadLocalObjectPool[]) _poolsMap.get();
    ThreadLocalObjectPool<T> pool = null;
    if (p == null) {
      synchronized (ThreadLocalObjectPools.class) {
        _poolSizeResetAllowed = false;
      }
      p = new ThreadLocalObjectPool[PoolableObjectFactoryIntf._MAX_NUM_POOLED_TYPES];
      _poolsMap.set(p);
    }
    final int fid = f.getUniqueTypeId();
    pool = p[fid];
    if (pool == null) {
      pool = ThreadLocalObjectPool.<T>newThreadLocalObjectPool(f, args);
      p[fid] = pool;
    }
    return pool;
  }

          
  /**
   * get the thread-local pool of objects of type T for the current thread. If
   * the pool doesn't yet exist, return null.
   *
   * @param <T>
   * @param f a factory for managed as well as unmanaged objects of type T
   * @return
   * @throws IllegalStateException if the current thread does not have a pool
   * for the type associated with f.
   */
  static <T extends PoolableObject> ThreadLocalObjectPool<T>
          getThreadLocalPool(PoolableObjectFactoryIntf<T> f) 
                  throws IllegalStateException {
    ThreadLocalObjectPool[] p = (ThreadLocalObjectPool[]) _poolsMap.get();
    if (p == null) {
      return null;
    }
    final int fid = f.getUniqueTypeId();
    try {
      return p[fid];
    } catch (Exception e) {
      throw new IllegalStateException("thread does not have associated pool");
    }
  }


  /**
   * sets the size of ALL the thread-local pools. Must only be called once,
   * before any other call to methods of this class or any other of related
   * classes.
   *
   * @param poolsize int
   * @throws IllegalStateException if there has been a call to any of the other
   * methods of the family of PoolableObject classes.
   * @throws IllegalArgumentException if poolsize &le; 0.
   */
  public static synchronized void setPoolSize(int poolsize)
          throws IllegalStateException, IllegalArgumentException {
    if (!_poolSizeResetAllowed) {
      throw new IllegalStateException("N/A");
    }
    _poolSizeResetAllowed = false;
    ThreadLocalObjectPool.setPoolSize(poolsize);
  }

  
  /**
   * return the size of every thread-local pool. May be called at any time.
   *
   * @return int
   */
  public static int getPoolSize() {
    return ThreadLocalObjectPool.getPoolSize();
  }

}
