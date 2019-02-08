/*
 * Code is distibuted as Open Source, under the LGPL2 license, without any waranty of fitness of use.
 */

package gr.ait.holmes.arrays;

import gr.ait.holmes.utils.Messenger;
import java.util.List;
import java.util.ArrayList;

/**
 * sparse double[] implementation of VectorIntf, maintaining two arrays,
 * an index array, and a values array. Useful when dealing with very
 * high-dimensional vectors that are sparse to a significant degree (e.g. in
 * Information Retrieval, one may create vectors in 50.000 dimensions, but
 * each vector may have only a few tens or hundreds of non-zero components).
 * Notice that getCoord/setCoord operations can be much more costly than the
 * same operations when operating with a DblArray1Vector (where the operations
 * are O(1) -constant time simple memory accesses).
 * Notice also that the class is not thread-safe, i.e. none of the methods can
 * be considered as reentrant in a multi-threaded environment. This choice is
 * made for speed considerations. Clients must therefore ensure on their own
 * that the application is race-condition free when using this class. 
 * @author itc
 */
public class DblArray1SparseVector implements SparseVectorIntf {
  private static final long serialVersionUID = 6365520149403267798L;
  private int[] _indices=null;  // _indices[0] indicates the first non-default 
	                              // element
  private double[] _values=null;  // _values[0] is the value of the 
	                                // _indices[0]-th element of the array
  private int _ilen;  // pos of last non-default element in _indices & _values 
	                    // arrays
  private int _n;  // vector dimension
	private double _defVal = 0.0;  // default value for components not set is 
	                               // really final field

	
  /**
   * constructs the zero sparse vector in n-dimensional space.
   * @param n int the number of dimensions
   * @throws IllegalArgumentException if n&le;0
   */
  public DblArray1SparseVector(int n) throws IllegalArgumentException {
    if (n<=0) throw new IllegalArgumentException("dimensions must be >= 1");
    _n = n;
  }
	
	
	/**
	 * same as 1-arg constructor, but also specifies default value for not-yet-set
	 * components (default is normally zero).
	 * @param n int
	 * @param def double
	 * @throws IllegalArgumentException if 1-arg constructor throws
	 */
	public DblArray1SparseVector(int n, double def) 
		throws IllegalArgumentException {
		this(n);
		_defVal=def;
	}


  /**
   * public constructor.
   * @param indices int[] must be in ascending order
   * @param values double[] corresponds to values for each index in the indices 
	 * array must not contain any zeros
   * @param n int total length of the vector
   * @throws IllegalArgumentException if any component of values is equal to the
	 * default value or if dimensions don't match or if indices are not ascending
   */
  public DblArray1SparseVector(int[] indices, double[] values, int n) 
		throws IllegalArgumentException {
    if (indices==null || values==null || indices.length!=values.length)
      throw new IllegalArgumentException("Arguments null or dimensions "+
				                                 "don't match");
    if (n<=indices[indices.length-1])
      throw new IllegalArgumentException("dimension mismatch");
    final int ilen = indices.length;
    _indices = new int[ilen];
    _values = new double[ilen];
    for (int i=0; i<ilen; i++) {
			_indices[i] = indices[i];
			if (i>0 && _indices[i]<=_indices[i-1]) 
				throw new IllegalArgumentException("indices not in ascending order");
			if (Double.compare(values[i],0.0)==0)  // _defVal==0 in this constructor
				throw new IllegalArgumentException("values array contains zero element"+
					                                 " in position "+i);
			_values[i] = values[i];
		}
    _n = n;
    _ilen = ilen;
  }

	
  /**
   * public constructor.
	 * @param defVal double the default value for the components of this vector
   * @param indices int[] must be in ascending order
   * @param values double[] corresponds to values for each index in the indices 
	 * array must not contain any defVal value
   * @param n int total length of the vector
   * @throws IllegalArgumentException if any component of values is equal to the
	 * default value or if dimensions don't match or if indices don't ascend
   */
  public DblArray1SparseVector(double defVal, int[] indices, double[] values, 
		                           int n) throws IllegalArgumentException {
    if (indices==null || values==null || indices.length!=values.length)
      throw new IllegalArgumentException("Arguments null or dimensions "+
				                                 "don't match");
    if (n<=indices[indices.length-1])
      throw new IllegalArgumentException("dimension mismatch");
    final int ilen = indices.length;
    _indices = new int[ilen];
    _values = new double[ilen];
    for (int i=0; i<ilen; i++) {
			_indices[i] = indices[i];
			if (i>0 && _indices[i]<=_indices[i-1])
				throw new IllegalArgumentException("indices not in ascending order");
			if (Double.compare(defVal, values[i])==0) 
				throw new IllegalArgumentException("values array contains default "+
					                                 "element in position "+i);
			_values[i] = values[i];
		}
    _n = n;
    _ilen = ilen;
		_defVal = defVal;
  }

	
  /**
   * protected constructor, only used from <CODE>newInstance(),newCopy()</CODE>
	 * and sub-classes.
	 * @param defVal double the default value for the components of this vector
   * @param indices int[] must be in ascending order (doesn't check)
   * @param values double[] corresponds to values for each index in the indices 
	 * array must not have any value equal to defVal
   * @param n int total length of the vector
	 * @param ilen int the actual length of the _indices,_values array
   * @throws IllegalArgumentException if any component of values is equal to the
	 * default value
   */
  protected DblArray1SparseVector(double defVal, int[] indices, double[] values, 
		                              int n, int ilen) 
		throws IllegalArgumentException {
    _indices = new int[ilen];
    _values = new double[ilen];
    for (int i=0; i<ilen; i++) {
			_indices[i] = indices[i];
			if (Double.compare(defVal, values[i])==0) 
				throw new IllegalArgumentException("values array contains default "+
					                                 "element in position "+i);
			_values[i] = values[i];
		}
    _n = n;
    _ilen = ilen;
		_defVal = defVal;
  }	
	

  /**
   * public constructor making a copy of the vector passed in, and multiplying
   * each element by the multFactor passed in. This constructor creates a vector
	 * whose default values are zero.
   * @param indices int[] elements must be in ascending order
   * @param values double[] must not have any zero
   * @param n int
   * @param multFactor double must not be zero
   * @throws IllegalArgumentException if any indices or values is null or their
	 * dimensions don't match or if n &le; indices[indices.length-1] or if any
	 * component of values has zero value of if indices don't ascend
   */
  public DblArray1SparseVector(int[] indices, double[] values, 
		                           int n, double multFactor) 
		throws IllegalArgumentException {
    if (indices==null || values==null || indices.length!=values.length)
      throw new IllegalArgumentException("Arguments null or dimensions "+
				                                 "don't match");
    if (n<=indices[indices.length-1])
      throw new IllegalArgumentException("dimension mismatch");
		if (Double.compare(multFactor, 0.0)==0) {  // _defVal is by default 0
			_n=n;
			return;
		}
    final int ilen = indices.length;
    _indices = new int[ilen];
    _values = new double[ilen];
    for (int i=0; i<ilen; i++) {
			_indices[i] = indices[i];
			if (i>0 && _indices[i]<=_indices[i-1]) 
				throw new IllegalArgumentException("indices don't ascend");
			_values[i] = values[i]*multFactor;
			if (Double.compare(_values[i],0.0)==0)  // _defVal is 0
				throw new IllegalArgumentException("default (zero) value for values["+
					                                 i+"]");
		}
    _n = n;
    _ilen  = ilen;
  }

	
  /**
   * public constructor making a copy of the vector passed in, and multiplying
   * each element by the multFactor passed in. Notice that there is no 
	 * requirement that the new values components must not be at the default 
	 * value, since it's not reasonable to expect the caller to ensure this 
	 * condition is not violated.
   * @param indices int[] elements must be in ascending order
   * @param values double[] must not contain any element with value equal to
	 * defVal/multFactor
   * @param n int
   * @param multFactor double
	 * @param defVal double default value of components
   * @throws IllegalArgumentException if any indices or values is null or their
	 * dimensions don't match or if n &le; indices[indices.length-1] or if indices 
	 * don't ascend
   */
  public DblArray1SparseVector(int[] indices, double[] values, 
		                           int n, double multFactor, double defVal) 
		throws IllegalArgumentException {
    if (indices==null || values==null || indices.length!=values.length)
      throw new IllegalArgumentException("Arguments null or dimensions don't match");
    if (n<=indices[indices.length-1])
      throw new IllegalArgumentException("dimension mismatch");
		final Messenger mger = Messenger.getInstance();
    final int ilen = indices.length;
    _indices = new int[ilen];
    _values = new double[ilen];
		_defVal = defVal;
		int j=0;
    for (int i=0; i<ilen; i++) {
			_indices[j] = indices[i];
			if (i>0 && _indices[i]<=_indices[i-1])
				throw new IllegalArgumentException("indices don't ascend");
			_values[j++] = values[i]*multFactor;
			if (Double.compare(_values[j-1],defVal)==0) {  
				// throw new IllegalArgumentException("default value for _values["+i+"]");
				mger.msg("DblArray1SparseVector.<init>(6 args): default value "+_defVal+
					       " for _values["+i+"]",2);
				--j;
				// reset j-th pos for _indices and _values
				_indices[j]=0;
				_values[j]=0;
			}
		}
    _n = n;
    _ilen  = j;
  }

		
  /**
   * protected constructor making a copy of the vector passed in and multiplying
   * each element by the multFactor passed in. Only used from 
	 * <CODE>newCopy()</CODE> and sub-classes.
   * @param indices int[] the first ilen elements must be in ascending order 
	 * (doesn't check)
   * @param values double[] the first ilen elements must not contain any element 
	 * equal to defVal/multFactor
   * @param n int
   * @param multFactor double
	 * @param defVal double default value of components
	 * @param ilen int
	 * @throws IllegalArgumentException if values[i]==defVal for some component
   */
  protected DblArray1SparseVector(int[] indices, double[] values, 
		                              int n, double multFactor, double defVal, 
                                  int ilen) 
		throws IllegalArgumentException {
    _indices = new int[ilen];
    _values = new double[ilen];
		_defVal = defVal;
    for (int i=0; i<ilen; i++) {
			_indices[i] = indices[i];
			_values[i] = values[i]*multFactor;
			if (Double.compare(_values[i],defVal)==0)  
				throw new IllegalArgumentException("default value for _values["+i+"]");
		}
    _n = n;
    _ilen  = ilen;
  }
	

  /**
   * return new (unmanaged) VectorIntf object containing a copy of the data of 
	 * this object.
   * @return VectorIntf
   */
  public VectorIntf newCopy() {
    if (_indices==null) return new DblArray1SparseVector(_n, _defVal);
    DblArray1SparseVector r = 
			new DblArray1SparseVector(_defVal, _indices, _values, _n, _ilen);
		return r;
  }


  /**
   * create new (unmanaged) copy of this vector, and multiply each component by 
   * the multFactor argument.
   * @param multFactor double
   * @return VectorIntf
   */
  public VectorIntf newCopyMultBy(double multFactor) {
    if (_indices==null) {
			// incorrect to return new DblArray1SparseVector(_n,_defVal); unless
			// _defVal is zero or multFactor is one.
			if (Double.compare(_defVal,0.0)==0 || Double.compare(multFactor,1.0)==0) 
				return new DblArray1SparseVector(_n,_defVal);
			// oops, vector is fully dense now
			final double val = _defVal*multFactor;
			int[] inds = new int[_n];
			double[] vals = new double[_n];
			for (int i=0; i<_n; i++) {
				inds[i] = i;
				vals[i] = val;
			}
			return new DblArray1SparseVector(_defVal,inds,vals,_n);
		}
		// check if _defVal==multFactor==0 as short-cut
		if (Double.compare(_defVal, 0.0)==0 && Double.compare(multFactor,0.0)==0)
			return new DblArray1SparseVector(_n);
    DblArray1SparseVector r = 
			new DblArray1SparseVector(_indices, _values, _n, multFactor, 
				                        _defVal, _ilen);
		return r;
  }

	
	/**
   * return a new VectorIntf object containing a copy of the data of this object
	 * guaranteeing that the return object is not managed (not part of any pool).
	 * @return VectorIntf  // DblArray1SparseVector
	 */
	public VectorIntf newInstance() {
    if (_indices==null) return new DblArray1SparseVector(_n,_defVal);
    DblArray1SparseVector r = 
			new DblArray1SparseVector(_defVal, _indices, _values, _n, _ilen);
		return r;
	}
	
	
	/**
	 * return a new VectorIntf object containing a copy of the data of this object
	 * guaranteeing that the return object is not managed (not part of any pool)
	 * and also having as default value for components the one specified in the
	 * argument. This means that when invoking this method on a sparse vector of 
	 * all zeros (having _defVal=0), with argument defVal=1 the result is a sparse 
	 * vector of all ones (with _defVal=1).
	 * @param defVal double
	 * @return VectorIntf  // DblArray1SparseVector
	 */
	public VectorIntf newInstance(double defVal) {
    if (_indices==null) return new DblArray1SparseVector(_n,defVal);
		// if (Double.compare(defVal, _defVal)==0) return newInstance();  // useless
		int[] indices = new int[_ilen];
		double[] values = new double[_ilen];
		int ilen=0;
		for (int i=0; i<_ilen; i++) {
			if (Double.compare(_values[i],defVal)!=0) {  // ok, put it in
				indices[ilen] = _indices[i];
				values[ilen++] = _values[i];
			}
		}
		DblArray1SparseVector r = new DblArray1SparseVector(_n, defVal);
		r._indices = indices;
		r._values = values;
		r._ilen = ilen;
		return r;		
	}
	

  /**
   * return a DblArray1SparseVector object containing as data the arg passed in.
   * The resulting vector will have the same default value as this. Linear 
	 * complexity in the length of the argument array.
   * @param arg double[]
   * @throws IllegalArgumentException
   * @return VectorIntf
   */
  public VectorIntf newInstance(double[] arg) throws IllegalArgumentException {
    if (arg==null) throw new IllegalArgumentException("null arg");
    final int n = arg.length;
    List inds = new ArrayList();
    List vals = new ArrayList();
    for (int i=0; i<n; i++) {
      if (Double.compare(arg[i], _defVal) != 0) {
        inds.add(new Integer(i));
        vals.add(new Double(arg[i]));
      }
    }
    final int ilen = inds.size();
    int[] indices = new int[ilen];
    for (int i=0; i<ilen; i++) 
			indices[i] = ((Integer) inds.get(i)).intValue();
    double[] values = new double[ilen];
    for (int i=0; i<ilen; i++) 
			values[i] = ((Double) vals.get(i)).doubleValue();
    // below code is too slow without reason
    //DblArray1SparseVector r = 
		//	new DblArray1SparseVector(_defVal, indices, values, n);
		DblArray1SparseVector r = new DblArray1SparseVector(n,_defVal);
		r._ilen=ilen;
		r._indices=indices;
		r._values=values;
		return r;
  }


  /**
   * return the number of coordinates of this VectorIntf object.
   * @return int
   */
  public int getNumCoords() { return _n; }

	
	/**
	 * return the default value of components for this vector.
	 * @return double
	 */
	public double getDefaultValue() {
		return _defVal;
	}

	
  /**
   * return a double[] representing this VectorIntf object. Should not really
   * be used as it defeats the purpose of this implementation, but if the
   * array representation is absolutely needed, then this method will return it.
   * @return double[]
   */
  public double[] getDblArray1() {
    double[] x = new double[_n];
		if (Double.compare(_defVal, 0.0)!=0) {  // set all to default vals
			for (int i=0; i<_n; i++) x[i]=_defVal;
		}
		// override for the non-def-valued components
    for (int i=0; i<_ilen; i++) x[_indices[i]] = _values[i];
    return x;
  }


  /**
   * return the i-th coordinate of this VectorIntf (i must be in the set
   * {0,1,2,...<CODE>getNumCoords()</CODE>-1}). Has O(log(_ilen)) worst-case
   * time-complexity where _ilen is the (max.) number of non-zeros in this
   * vector (or more generally, non-default-vals).
   * @param i int
   * @throws IndexOutOfBoundsException if i is not in the set mentioned above
   * @return double
   */
  public double getCoord(int i) throws IndexOutOfBoundsException {
    if (i<0 || i>=_n) 
			throw new IndexOutOfBoundsException("index "+i+" out of bounds");
    if (_indices==null || _ilen==0) return _defVal;
    // requires a binary search in the indices.
    if (i<_indices[0] || i>_indices[_ilen-1]) return _defVal;
    else if (i==_indices[0]) return _values[0];
    else if (i==_indices[_ilen-1]) return _values[_ilen-1];
    int i1 = 0;
    int i2 = _ilen;
    int ih = (i1+i2)/2;
    while (i1<i2 && i1<ih) {
      if (_indices[ih] == i) return _values[ih];
      else if (_indices[ih] < i) i1 = ih;
      else i2 = ih;
      ih = (i1+i2)/2;
    }
    return _defVal;
  }


  /**
   * set the i-th coordinate of this VectorIntf (i must be in the set
   * {0,1,2,...<CODE>getNumCoords()</CODE>-1}). Repeated calls to this method
   * for indices not in the original construction of this vector will eventually
   * destroy its sparsity. Has O(_ilen) worst-case time-complexity where _ilen
   * is the (max.) number of non-defaults in the array. The time-complexity
   * reduces to O(log(_ilen)) if the element to be set, is already non-default
   * before the operation.
   * @param i int
   * @param val double
   * @throws IndexOutOfBoundsException if i is not in the set mentioned above
   */
  public void setCoord(int i, double val) throws IndexOutOfBoundsException {
    if (i<0 || i>=_n) 
			throw new IndexOutOfBoundsException("index "+i+" out of bounds");
		final boolean is_val_at_def = Double.compare(val,_defVal)==0;
    if (_indices==null) {
      if (is_val_at_def) return;  // don't do anything
      _indices = new int[1];
      _indices[0] = i;
      _values = new double[1];
      _values[0] = val;
      _ilen=1;
      return;
    }
		if (_ilen==0) {  // but _indices, _values not null
			if (is_val_at_def) return;  // don't do anything
			_indices[0]=i;
			_values[0]=val;
			_ilen=1;
			return;
		}
    // binary search in indices
    final int ilen = _indices.length;
    int i1 = 0;
    int i2 = _ilen;
    if (_indices[0]==i) {
      if (is_val_at_def) shrink(0);
      else _values[0] = val;
      return;
    } else if (_indices[_ilen-1] == i) {
      if (is_val_at_def) shrink(_ilen-1);
      else _values[_ilen-1] = val;
      return;
    }
    int ih = (i1+i2)/2;
    while (i1<i2 && i1<ih) {
      if (_indices[ih] == i) break;
      else if (_indices[ih] < i) i1 = ih;
      else i2 = ih;
      ih = (i1+i2)/2;
    }
    if (_indices[ih]==i) {
      if (is_val_at_def) shrink(ih);
      else _values[ih] = val;
      return;
    }
    else if (is_val_at_def) return;  // no change
    // change is necessary
    // if needed, create new arrays to insert the value for <i,val> pair.
    if (_ilen == ilen) {  // increase arrays' capacity 20%
      int[] indices = new int[ilen + ilen / 5 + 1];
      double[] values = new double[ilen + ilen / 5 + 1];
      boolean first_time = true;
      for (int j = 0; j < ilen; j++) {
        if (_indices[j] < i) {
          indices[j] = _indices[j];
          values[j] = _values[j];
        }
        else if (first_time) { // insert <i,val> pair
          indices[j] = i;
          values[j] = val;
          first_time = false;
          j--;
        }
        else {
          indices[j + 1] = _indices[j];
          values[j + 1] = _values[j];
        }
      }
      if (first_time) {
        indices[ilen] = i;
        values[ilen] = val;
      }
      _indices = indices;
      _values = values;
    }
    else {  // use same arrays as there is capacity
      int j;
      for (j=_ilen-1; j>=0; j--) {
        if (_indices[j]>i) {
          _indices[j+1] = _indices[j];
          _values[j+1] = _values[j];
        }
        else break;
      }
      _indices[j+1] = i;
      _values[j+1] = val;
    }
    ++_ilen;
  }


  /**
   * the purpose of this routine is to allow a traversal of the non-def vals of
   * this object as follows:
	 * <br>
	 * <pre>
   * <CODE>
   * for (int i=0; i&lt;sparsevector.getNumNonZeros(); i++) {
   *   int    pos = sparsevector.getIthNonZeroPos(i);
	 *   double val = sparsevector.getIthNonZeroVal(i);
   *   //double val = sparsevector.getCoord(pos);
   * }
   * </CODE>
	 * </pre>.
	 * <p>Notice that when <CODE>_defVal</CODE> of this object is non-zero, then
	 * this method returns the i-th non-default-valued component of this vector.
	 * </p>
   * @param i int
   * @throws IndexOutOfBoundsException if i is out-of-bounds. Always throws if
   * this is the zero vector
   * @return int
   */
  public int getIthNonZeroPos(int i) throws IndexOutOfBoundsException {
    if (i<0 || i>=_ilen) 
			throw new IndexOutOfBoundsException("index "+i+" out of bounds[0,"+
				                                  _ilen+"] Vector is: "+this);
    return _indices[i];
  }
	
	
	/**
	 * get the i-th non-default value of this vector.
	 * @param i int
	 * @return double
	 * @throws IndexOutOfBoundsException if i is out-of-bounds. Always throws if
	 * this is the default vector.
	 */
	public double getIthNonZeroVal(int i) throws IndexOutOfBoundsException {
    if (i<0 || i>=_ilen) 
			throw new IndexOutOfBoundsException("index "+i+" out of bounds[0,"+
				                                  _ilen+"] Vector is: "+this);
    return _values[i];		
	}


  /**
   * modifies this sparse vector by adding the quantity m*other to it. This
   * operation may destroy the sparse nature of this object.
   * @param m double
   * @param other DblArray1SparseVector
   * @throws IllegalArgumentException if other is null or does not have the
   * same dimensions as this vector or if m is NaN. Not checked.
   */
	public void addMul(double m, DblArray1SparseVector other) {
    if (other==null || other.getNumCoords()!=_n || Double.isNaN(m))
      throw new IllegalArgumentException("cannot call addMul(m,v) with v "+
                                         "having different dimensions than "+
                                         "this vector or with m being NaN.");
		if (Double.compare(other.getDefaultValue(),0.0)==0) {
			final int other_nz = other.getNumNonZeros();
			for (int i=0; i<other_nz; i++) {
				int pi = other.getIthNonZeroPos(i);
				double vi = other.getIthNonZeroVal(i);
				setCoord(pi, getCoord(pi)+m*vi);
			}
		} else {  // revert to standard iteration
	    for (int i=0; i<_n; i++) {
		    setCoord(i, getCoord(i)+m*other.getCoord(i));
			}
		}
	}
	

  /**
   * modifies this sparse vector by adding the quantity m*other to it. This
   * operation may destroy the sparse nature of this object.
   * @param m double
   * @param other VectorIntf
   * @throws IllegalArgumentException if other is null or does not have the
   * same dimensions as this vector or if m is NaN. Not checked.
   */
  public void addMul(double m, VectorIntf other) {
    if (other==null || other.getNumCoords()!=_n || Double.isNaN(m))
      throw new IllegalArgumentException("cannot call addMul(m,v) with v "+
                                         "having different dimensions than "+
                                         "this vector or with m being NaN.");
    for (int i=0; i<_n; i++) {
      setCoord(i, getCoord(i)+m*other.getCoord(i));
    }
  }


  /**
   * divide the components of this vector by the argument h.
   * @param h double must have absolute value &ge; 1.e-120.
   * @throws IllegalArgumentException if h is (almost) zero (unchecked)
   */
  public void div(double h) {
    if (Double.isNaN(h) || Math.abs(h)<1.e-120)
      throw new IllegalArgumentException("division by (almost) zero or NaN");
		if (Double.compare(_defVal,0.0)==0) {
			for (int i=0; i<_ilen; i++) {
				_values[i] /= h;
			}
		}
		else {  // slow but correct version
			for (int i=0; i<_n; i++) {
				setCoord(i, getCoord(i)/h);
			}
		}
  }


  /**
   * return true iff all components are zero.
   * @return boolean
   */
  public boolean isAtOrigin() {
    if (Double.compare(_defVal, 0.0)==0) return _ilen==0;
		if (_ilen!=_n) return false;
		for (int i=0; i<_n; i++) {
			if ((Double.compare(_values[i], 0.0))!=0) return false;
		}
		return true;
  }

	
	/**
	 * reset the vector to all default values.
	 */
	public void reset() {
		//_indices=null;
		//_values=null;
		_ilen=0;
	}


  /**
   * return a String representation of this VectorIntf object.
   * @return String
   */
  public String toString() {
    String x="[";
    for (int i=0; i<_ilen-1; i++) x += "("+_indices[i]+","+_values[i]+")"+", ";
    if (_ilen>0) {
			x += "("+_indices[_ilen-1]+","+_values[_ilen-1]+")";
		}
		x += " _ilen="+_ilen+" _n="+_n;
    x += " defaultValue="+_defVal+"]";
    return x;
  }


  /**
   * returns the number of non-zero elements in this vector. 
	 * <p>If <CODE>_defVal</CODE> is other than zero, returns the number of 
	 * non-default-valued elements. </p>
   * @return int
   */
  public int getNumNonZeros() {
    return _ilen;
  }


  /**
   * compute the inner product of this vector with the argument passed in.
   * The operation should be fast when this vector is sparse enough, as it only
   * goes through the non-zero elements of the vector (assuming the default 
	 * value for this vector is zero).
   * @param other VectorIntf
   * @throws IllegalArgumentException if vector dimensions don't match
   * @return double
   */
  public double innerProduct(VectorIntf other) throws IllegalArgumentException {
    if (other==null || other.getNumCoords()!=_n)
      throw new IllegalArgumentException("dimensions don't match "+
				                                 "or null argument passed in");
		if (Double.compare(_defVal, 0.0)==0) {
	    double sum=0.0;
			for (int i=0; i<_ilen; i++) {
				sum += _values[i]*other.getCoord(_indices[i]);
			}
	    return sum;
		} else {
			if (other instanceof DblArray1SparseVector) {  // try the other one
				if (Double.compare(((DblArray1SparseVector) other)._defVal,0.0)==0)
					return ((DblArray1SparseVector) other).innerProduct(this);
			}
			// go the hard way
			double sum=0.0;
			for (int i=0; i<_n; i++) {
				sum += getCoord(i)*other.getCoord(i);
			}
			return sum;
		}
  }


  /**
   * return the k-th norm of this vector.
   * @param k int
   * @throws IllegalArgumentException if x==null or if k&le;0
   * @return double
   */
  public double norm(int k) throws IllegalArgumentException {
    if (k<=0) throw new IllegalArgumentException("k<=0");
    if (k==2) return norm2();  // faster computation
    double res = 0.0;
    if (Double.compare(_defVal, 0.0)==0) {
			for (int i=0; i<_ilen; i++) {
				double absxi = Math.abs(_values[i]);
				res += Math.pow(absxi,k);
			}
		} else {
			for (int i=0; i<_ilen; i++) {
				double absxi = Math.abs(_values[i]);
				res += Math.pow(absxi,k);
			}
			res += (_n-_ilen)*Math.pow(Math.abs(_defVal), k);
		}
    res = Math.pow(res, 1.0/(double) k);
    return res;
  }


  /**
   * short-cut for norm(2). Faster too.
   * @throws IllegalArgumentException if x==null
   * @return double
   */
  public double norm2() throws IllegalArgumentException {
    double res2=0.0;
		for (int i=0; i<_ilen; i++) {
			double xi = _values[i];
			res2 += (xi * xi);
		}
		res2 += (_n-_ilen)*_defVal*_defVal;
    return Math.sqrt(res2);
  }


  /**
   * computes the infinity norm of this vector.
   * @return double
   */
  public double normInfinity() {
    double res = 0.0;
    for (int i=0; i<_ilen; i++) {
      final double absxi = Math.abs(_values[i]);
      if (absxi>res) res = absxi;
    }
		if (_n>_ilen && Math.abs(_defVal)>res) return Math.abs(_defVal);
    return res;
  }


  /**
   * return true iff the other vector is exactly equal to this, component-wise.
   * @param other Object
   * @return boolean
   */
  public boolean equals(Object other) {
    if (other==null || other instanceof VectorIntf == false) return false;
    VectorIntf o = (VectorIntf) other;
    if (o.getNumCoords()!=_n) return false;
		if (other instanceof DblArray1SparseVector) {  // short-cut? 
			if (Double.compare(((DblArray1SparseVector)other)._defVal, _defVal)!=0) {
				// no short-cut
				for (int i=0; i<_n; i++) {
					if (Double.compare(getCoord(i), o.getCoord(i))!=0) return false;
				}
				return true;
			} else {
				// short-cut
	      DblArray1SparseVector osv = (DblArray1SparseVector) other;
		    for (int i = 0; i < _ilen; i++) {
			    if (_indices[i] != osv._indices[i] || 
						  Double.compare(_values[i], osv._values[i]) != 0)
				    return false;
				}
				return true;
			}
		}
    if (other instanceof SparseVectorIntf) {  // short-cut
      SparseVectorIntf osv = (SparseVectorIntf) other;
			if (Double.compare(osv.getDefaultValue(), _defVal)!=0) {
				// no short-cut
				for (int i=0; i<_n; i++) {
					if (Double.compare(getCoord(i), o.getCoord(i))!=0) return false;
				}
				return true;
			} else {  // same default-value
				// short-cut
		    for (int i = 0; i < _ilen; i++) {
					int posi = osv.getIthNonZeroPos(i);
			    if (_indices[i] != posi || 
						  Double.compare(_values[i], osv.getCoord(posi)) != 0)
				    return false;
				}
				return true;				
			}
    }
    else return o.equals(this);  // no short-cut
  }
	
	
	/**
	 * return the integer part of the first value of the first non-zero element of 
	 * this vector, if it exists, else return the integer part of the default 
	 * value.
	 * @return int
	 */
	public int hashCode() {
		return _ilen > 0 ? (int) _values[0] : (int) _defVal;
	}


  protected int[] getIndices() { return _indices; }
  protected void setIndices(int[] indices) { _indices = indices; }
  protected double[] getValues() { return _values; }
  protected void setValues(double[] values) { _values = values; }
  protected int getILen() { return _ilen; }
	protected void setILen(int ilen) { _ilen = ilen; }
  protected void incrILen() { ++_ilen; }


  /**
   * reduce the _indices and _values arrays and the _ilen value by removing
   * the value at position pos.
   * @param pos int
   */
  private void shrink(int pos) {
    for (int i=pos; i<_ilen-1; i++) {
      _indices[i] = _indices[i+1];
      _values[i] = _values[i+1];
    }
    --_ilen;
  }
}

