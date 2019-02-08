/*
 * Code is distibuted as Open Source, under the LGPL2 license, without any waranty of fitness of use.
 */

package gr.ait.holmes.arrays;

import gr.ait.holmes.tlop.*;
import java.util.HashMap;


/**
 * double[] implementation of VectorIntf.
 * The class is not thread-safe (to avoid paying locking costs).
 * Clients must ensure no race-conditions exist when using this class.
 * The class is also made "poolable" via the Thread-Local Object Pool Design
 * Pattern (for this reason it extends the PoolableObject class of the tlop 
 * library.)
 * @author itc
 */
public class DblArray1Vector extends PoolableObject implements VectorIntf {
  // private final static long serialVersionUID=-1953111744690308391L;
	private final static boolean _USE_POOLS=true;  // compile-time flag indicates use of pools or not
  private final static HashMap<Integer, DblArray1VectorFactory>
          _fMap = _USE_POOLS ? new HashMap<>() : null;
  
	private double _x[]=null;
  /*
	private static long _totalNumObjs=0;
	*/

	/**
	 * public constructor creating a point in the origin of R^n.
	 * @param n int
	 * @throws IllegalArgumentException
	 */
	public DblArray1Vector(int n) throws IllegalArgumentException {
    super(null);
		if (n<=0) throw new IllegalArgumentException("n<=0 passed");
		_x = new double[n];  // init to zero.
    /*
    synchronized (DblArray1Vector.class) {
      ++_totalNumObjs;
    }
    */
	}


  /**
   * public constructor making a copy of the argument.
   * @param x double[]
   * @throws IllegalArgumentException
   */
  public DblArray1Vector(double[] x) throws IllegalArgumentException {
    super(null);
    if (x==null) throw new IllegalArgumentException("null arg passed");
    final int n = x.length;
    _x = new double[n];
    for (int i=0; i<n; i++) _x[i]=x[i];
    /*
    synchronized (DblArray1Vector.class) {
      ++_totalNumObjs;
    }
    */
  }


  /**
   * public constructor making a copy of the array passed in, and multiplying
   * each element by the multFactor passed in.
   * @param x double[]
   * @param multFactor double
   * @throws IllegalArgumentException
   */
  public DblArray1Vector(double[] x, double multFactor) throws IllegalArgumentException {
    super(null);
    if (x==null) throw new IllegalArgumentException("null arg passed");
    final int n = x.length;
    _x = new double[n];
    for (int i=0; i<n; i++) _x[i]=x[i]*multFactor;
    /*
    synchronized (DblArray1Vector.class) {
      ++_totalNumObjs;
    }
    */
  }


  /**
   * this constructor is to be used only from the DblArray1VectorPool for
   * constructing managed objects (re-claimable ones).
	 * @param n int the vectors' fixed size
   * @param pool ThreadLocalObjectPool&lt;DblArray1Vector&gt;
   */
  DblArray1Vector(int n, ThreadLocalObjectPool<DblArray1Vector> pool) {
    super(pool);
    if (_USE_POOLS==false) throw new IllegalArgumentException("_USE_POOLS==false: this ctor should not be used");
		_x = new double[n];
    /*
    synchronized (DblArray1Vector.class) {
      ++_totalNumObjs;
    }
    */
  }
  
  
  /**
   * implements the abstract method in PoolableObject super-class, as a no-op,
   * since the <CODE>createObject()</CODE> method in 
   * <CODE>DblArray1VectorFactory</CODE> already does what it must.
   * @param args 
   */
  public void setData(Object... args) {
    // no-op
  }


  /**
   * return a new VectorIntf object containing a copy of the data of this object.
	 * This implementation will try to fetch a vector from the thread-local object
	 * pool if _USE_POOLS is true.
   * @return VectorIntf
   */
  public VectorIntf newCopy() {
    // DblArray1Vector x = new DblArray1Vector(_x);
		final int n = _x.length;
		DblArray1Vector x = DblArray1Vector.newInstance(n);
		for (int i=0; i<n; i++) {
			x.setCoord(i, _x[i]);
		}
    return x;
  }


  /**
   * return a new DblArray1Vector that contains a copy of this object's data,
   * but each element in the returned object is multiplied by the passed arg.
   * @param multFactor double
   * @return VectorIntf
   */
  public VectorIntf newCopyMultBy(double multFactor) {
    DblArray1Vector x = new DblArray1Vector(_x, multFactor);
    return x;
  }


  /**
   * return an (unmanaged) DblArray1Vector object containing as data the arg 
	 * passed in.
   * @param arg double[]
   * @return VectorIntf
   */
  public VectorIntf newInstance(double[] arg) {
    return new DblArray1Vector(arg);
  }
	
	
	/**
	 * return an (unmanaged) DblArray1Vector object containing a copy of the data
	 * of this object.
	 * @return VectorIntf
	 */
	public VectorIntf newInstance() {
		return new DblArray1Vector(_x);
	}


	/**
	 * this factory method shall first try to obtain an object from the thread-
	 * local object pool of DblArray1Vector objects (as long as the _USE_POOLS 
	 * compile-time constant flag is true), and if it can't find one (or if the
	 * _USE_POOLS flag is false), it will then produce an unmanaged one. This 
   * method is rather expensive, as it has to synchronize for the look-up of the
   * factory of the right size.
	 * The produced object is guaranteed at origin only if _USE_POOLS is false.
	 * @param n int the vector size or dimension
	 * @return DblArray1Vector
	 */
  public static DblArray1Vector newInstance(int n) {
		if (_USE_POOLS) {
      DblArray1VectorFactory f;
      synchronized (DblArray1Vector.class) {
        f = _fMap.get(n);
        if (f==null) {
          try {
            f = new DblArray1VectorFactory(n);
            _fMap.put(n,f);
          }
          catch (IllegalStateException e) {  // no more unique type ids
            // log the exception
            f = null;
          }
        }
      }
      if (f!=null) return PoolableObject.newInstance(f, n);
    }
		return new DblArray1Vector(n);
  }


  /**
   * return true iff the other vector is exactly equal to this.
   * @param other VectorIntf
   * @return boolean
   */
  public boolean equals(Object other) {
    if (other==null || other instanceof VectorIntf == false) return false;
    VectorIntf o = (VectorIntf) other;
    if (o.getNumCoords() != _x.length) return false;
    for (int i = 0; i < _x.length; i++)
      if (Double.compare(_x[i], o.getCoord(i)) != 0)return false;
    return true;
  }
	
	
	/**
	 * returns the integer part of the first element of this vector.
	 * @return int
	 */
	public int hashCode() {
		return (int) _x[0];
	}


  /**
   * return the number of coordinates of this VectorIntf object.
   * @return int
   */
  public int getNumCoords() { return _x.length; }


  /**
   * return a copy of this object's data member.
   * @return double[]
   */
  public double[] getDblArray1() {
    double[] x = new double[_x.length];
    for (int i=0; i<x.length; i++) x[i] = _x[i];
    return x;
  }


  /**
   * return the i-th coordinate of this VectorIntf (i must be in the set
   * {0,1,2,...<CODE>getNumCoords()</CODE>-1}
   * @param i int
   * @throws IndexOutOfBoundsException if i is not in the set mentioned above
   * @return double
   */
  public double getCoord(int i) throws IndexOutOfBoundsException {
    if (i<0 || i>=_x.length) throw new IndexOutOfBoundsException("index "+i+" out of bounds");
    return _x[i];
  }


  /**
   * set the i-th coordinate of this VectorIntf (i must be in the set
   * {0,1,2,...<CODE>getNumCoords()</CODE>-1}
   * @param i int
   * @param val double
   * @throws IndexOutOfBoundsException if i is not in the set mentioned above
   */
  public void setCoord(int i, double val) throws IndexOutOfBoundsException {
    if (i<0 || i>=_x.length) throw new IndexOutOfBoundsException("index "+i+" out of bounds");
    _x[i] = val;
  }


  /**
   * modifies this VectorIntf by adding the quantity m*other to it.
   * @param m double
   * @param other VectorIntf
   * @throws IllegalArgumentException if other is null or does not have the
   * same dimensions as this vector or if m is NaN
   */
  public void addMul(double m, VectorIntf other) throws IllegalArgumentException {
    if (other==null || other.getNumCoords()!=_x.length || Double.isNaN(m))
      throw new IllegalArgumentException("cannot call addMul(m,v) with v "+
                                         "having different dimensions than "+
                                         "this vector or with m being NaN.");
    for (int i=0; i<_x.length; i++) {
      _x[i] += m*other.getCoord(i);
    }
  }


  /**
   * divide the components of this vector by the argument h.
   * @param h double
   * @throws IllegalArgumentException if h is zero
   */
  public void div(double h) throws IllegalArgumentException {
    if (Double.isNaN(h) || Math.abs(h)<1.e-120)
      throw new IllegalArgumentException("division by (almost) zero or NaN");
    for (int i=0; i<_x.length; i++)
      _x[i] /= h;
  }


  /**
   * return true iff all components are zero.
   * @return boolean
   */
  public boolean isAtOrigin() {
    for (int i=0; i<_x.length; i++)
      if (Double.compare(_x[i], 0.0) != 0) return false;
    return true;
  }


  /**
   * return a String representation of this VectorIntf object.
   * @return String
   */
  public String toString() {
    String x="[";
    for (int i=0; i<_x.length-1; i++) x += _x[i]+", ";
    x += _x[_x.length-1];
    x += "]";
    return x;
  }

	
	/**
	 * package-protected method returns directly the underlying array of data.
	 * The method is obviously not part of the public API, and is only used by
	 * wrapper functions that allow algorithms that work with 
	 * <CODE>DblArray1Vector</CODE> objects, to minimize functions that expect
	 * <CODE>double[]</CODE> arguments only.
	 * @return double[] the underlying <CODE>_x</CODE> data member.
	 */
	double[] get_x() {
		return _x;
	}
	
	// PoolableObjectIntf methods below

	/**
	 * returns an unmanaged (ie not belonging to the thread-local object pool)
	 * DblArray1Vector object that is a cloned (deep) copy of the data in this
	 * object.
	 * @return DblArray1Vector
	 */
	public PoolableObject cloneObject() {
		DblArray1Vector clone = new DblArray1Vector(_x);
		return clone;
	}
  
  /*
  public static synchronized long getTotalNumObjs() {
    return _totalNumObjs;
  }
  */
}


class DblArray1VectorFactory 
  implements PoolableObjectFactoryIntf<DblArray1Vector> {
  private final int _n;
  private final int _typeId;
  
  public DblArray1VectorFactory(int n) {
    _n = n;
    _typeId = UniqueTypeId.getNextId();
  }
  public DblArray1Vector createObject(Object... args) {
    int n = ((Integer)args[0]).intValue();
    return new DblArray1Vector(n);
  }
  public DblArray1Vector createPooledObject(ThreadLocalObjectPool<DblArray1Vector> pool, Object... args) {    
    return new DblArray1Vector(_n,pool);
  }
  public int getUniqueTypeId() { return _typeId; }
}

