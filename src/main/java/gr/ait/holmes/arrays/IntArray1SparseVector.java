/*
 * Code is distibuted as Open Source, under the LGPL2 license, without any waranty of fitness of use.
 */

package gr.ait.holmes.arrays;

import java.util.ArrayList;


/**
 * sparse int[] implementation of VectorIntf, maintaining two arrays,
 * an index array, and a values array. Useful when dealing with very
 * high-dimensional vectors that are sparse to a significant degree (e.g. in
 * Graph Theory, one may create vectors in as many dimensions as there are nodes
 * in the graph, but each vector may have only as many non-zero components as it
 * has immediate neighbors).
 * Notice that getCoord/setCoord operations can be much more costly than the
 * same operations when operating with a DblArray1Vector (where the operations
 * are O(1) -constant time simple memory accesses). But on the other hand,
 * iterations over the non-zero elements can be made much faster, as well as 
 * related inner-product operations etc.
 * Notice also that the class is not thread-safe, i.e. none of the methods can
 * be considered as reentrant in a multi-threaded environment. This choice is
 * made for speed considerations. Clients must therefore ensure on their own
 * that the application is race-condition free when using this class. Notice 
 * also that this class only supports zero as the default value of components.
 * @author itc
 */
public class IntArray1SparseVector implements SparseVectorIntf {
  //private static final long serialVersionUID = ;
  private int[] _indices=null;  // _indices[0] indicates the first non-zero element
  private int[] _values=null;  // _values[0] is the value of the _indices[0]-th element of the array
  private int _ilen;  // value of last non-zero element in _indices & _values arrays
  private int _n;  // vector dimension


  /**
   * constructs the zero sparse vector in n-dimensional space.
   * @param n int the number of dimensions
   * @throws IllegalArgumentException if n &le; 0
   */
  public IntArray1SparseVector(int n) throws IllegalArgumentException {
    if (n<=0) throw new IllegalArgumentException("dimensions must be >= 1");
    _n = n;
  }


  /**
   * private constructor.
   * @param indices int[] must be in ascending order
   * @param values double[] corresponds to int values for each index in the indices array
   * @param n int total length of the vector
   * @throws IllegalArgumentException if indices and values don't match or are
	 * null or if n is too small or if indices are not stored in ascending order 
	 * or if values do not hold int values or if some of its components are zero
   */
  private IntArray1SparseVector(int[] indices, double[] values, int n) throws IllegalArgumentException {
    if (indices==null || values==null || indices.length!=values.length)
      throw new IllegalArgumentException("Arguments null or dimensions don't match");
    if (n<=indices[indices.length-1])
      throw new IllegalArgumentException("dimension mismatch");
    final int ilen = indices.length;
    _indices = new int[ilen];
    for (int i=0; i<ilen; i++) {
			if (i>0 && indices[i]<indices[i-1]) throw new IllegalArgumentException("indices not in ASC order");
			_indices[i] = indices[i];
		}
    _values = new int[ilen];
    for (int i=0; i<ilen; i++) {
			int vi = (int) Math.round(values[i]);
			if (Double.compare(values[i], Math.round(values[i]))!=0)  // values must be integer quantities
				throw new IllegalArgumentException("values["+i+"]="+values[i]);  
			if (vi==0)
				throw new IllegalArgumentException("zero component "+i);
			_values[i] = vi;  // used to be (int) values[i]
		}  
    _n = n;
    _ilen = ilen;
  }


  /**
   * private constructor representing the (sparse) vector that results from 
	 * multiplying every element in the sparse-array representation given by the 
	 * first three arguments with the value given in the fourth argument, and 
	 * keeping only the integer part of the multiplication.
	 * Note: this constructor assumes indices and values have length exactly as
	 * much as the <CODE>_ilen</CODE> of the newly constructed vector should be.
   * @param indices int[] elements must be in ascending order
   * @param values double[]
   * @param n int
   * @param multFactor double
   * @throws IllegalArgumentException if indices and values don't match or are
	 * null or if n is too small or if indices are not stored in ascending order
	 * or if some component of values is zero
   */
  private IntArray1SparseVector(int[] indices, double[] values, int n, 
					                     double multFactor) 
	    throws IllegalArgumentException {
    if (indices==null || values==null || indices.length!=values.length)
      throw new IllegalArgumentException("Arguments null or dimensions don't match");
    if (n<=indices[indices.length-1])
      throw new IllegalArgumentException("dimension mismatch");
		if (Double.compare(multFactor, 0.0)==0) {
			_n = n;
			return;
		}
    final int ilen = indices.length;
    _indices = new int[ilen];
    for (int i=0; i<ilen; i++) {
			if (i>0 && indices[i]<=indices[i-1]) throw new IllegalArgumentException("indices not in ASC order");
			_indices[i] = indices[i];
		}
    _values = new int[ilen];
    for (int i=0; i<ilen; i++) {
			_values[i] = (int) (values[i]*multFactor);  // keep only the integer part
			if (_values[i]==0)
				throw new IllegalArgumentException("zero component "+i);
		}
    _n = n;
    _ilen  = ilen;
  }


  /**
   * public constructor representing the (sparse) vector that results from 
	 * multiplying every element in the sparse-array representation given by the 
	 * first three arguments with the value given in the fourth argument.
   * @param indices int[] elements must be in ascending order
   * @param values int[]
   * @param n int
	 * @param ilen int
   * @param multFactor int
   * @throws IllegalArgumentException if indices and values don't match or are
	 * null or if n is too small or if indices are not stored in ascending order
	 * or if some component of i is zero
   */
  public IntArray1SparseVector(int[] indices, int[] values, int n, int ilen,
					                     int multFactor) 
	    throws IllegalArgumentException {
    if (indices==null || values==null || indices.length!=values.length)
      throw new IllegalArgumentException("Arguments null or dimensions don't match");
    if (n<=indices[indices.length-1])
      throw new IllegalArgumentException("dimension mismatch");
    //final int ilen = indices.length;
    _indices = new int[ilen];
    for (int i=0; i<ilen; i++) {
			if (i>0 && indices[i]<=indices[i-1])
				throw new IllegalArgumentException("indices not in ASC order");
			_indices[i] = indices[i];
		}
    _values = new int[ilen];
    for (int i=0; i<ilen; i++) {
			_values[i] = values[i]*multFactor;
			if (_values[i]==0)
				throw new IllegalArgumentException("zero component "+i);
		}
    _n = n;
    _ilen  = ilen;
  }


  /**
   * return a new VectorIntf object containing a copy of the data of this object.
   * @return VectorIntf
   */
  public VectorIntf newCopy() {
    if (_indices==null) return new IntArray1SparseVector(_n);
    return new IntArray1SparseVector(_indices, _values, _n, _ilen, 1);
  }


  /**
   * create new copy of this vector, and multiply each component by the
   * integer part of the multFactor argument.
   * @param multFactor double must hold int value
   * @return VectorIntf
   */
  public VectorIntf newCopyMultBy(double multFactor) {
    if (_indices==null) return new IntArray1SparseVector(_n);
    return new IntArray1SparseVector(_indices, _values, _n, _ilen, (int) multFactor);
  }
	
	
	/**
	 * return a new <CODE>VectorIntf</CODE> object containing a copy of the data 
	 * of this object guaranteeing that the returned object is un-managed 
	 * (not part of a pool).
	 * @return VectorIntf
	 */
	public VectorIntf newInstance() {
    if (_indices==null) return new IntArray1SparseVector(_n);
    return new IntArray1SparseVector(_indices, _values, _n, _ilen, 1);		
	}


  /**
   * return a <CODE>IntArray1SparseVector</CODE> object containing as data the 
	 * arg passed in (an appropriate copy of). Linear complexity in the length of 
	 * the argument array.
   * @param ints double[] must hold integer numbers
   * @throws IllegalArgumentException if ints is null or if some number in it is 
	 * fractional
   * @return VectorIntf
   */
  public VectorIntf newInstance(double[] ints) throws IllegalArgumentException {
    if (ints==null) throw new IllegalArgumentException("null arg");
    final int n = ints.length;
    ArrayList inds = new ArrayList();
    ArrayList vals = new ArrayList();
    for (int i=0; i<n; i++) {
      if (Double.compare(ints[i], 0.0) != 0) {
        if (Double.compare(ints[i], Math.round(ints[i])) != 0) 
					throw new IllegalArgumentException("ints["+i+"]="+ints[i]);
				inds.add(new Integer(i));
        vals.add(new Integer((int) ints[i]));
      }
    }
    final int ilen = inds.size();
    int[] indices = new int[ilen];
    for (int i=0; i<ilen; i++) indices[i] = ((Integer) inds.get(i)).intValue();
    double[] values = new double[ilen];
    for (int i=0; i<ilen; i++) values[i] = ((Integer) vals.get(i)).intValue();
    return new IntArray1SparseVector(indices, values, n);
  }


  /**
   * return the number of coordinates of this <CODE>VectorIntf</CODE> object.
   * @return int
   */
  public int getNumCoords() { return _n; }


  /**
   * return a double[] representing this <CODE>VectorIntf</CODE> object. Should 
	 * not really be used as it defeats the purpose of this implementation, but if 
	 * the array representation is absolutely needed, then this method will return 
	 * it.
   * @return double[]
   */
  public double[] getDblArray1() {
    double[] x = new double[_n];
    for (int i=0; i<_ilen; i++) x[_indices[i]] = _values[i];
    return x;
  }

	
	/**
	 * return a copy of the portion of the <CODE>_indices</CODE> array that 
	 * corresponds to non-zero values.
	 * @return int[] null if there are none.
	 */
	public int[] getNonDefIndices() {
		if (_ilen==0) return null;
		int[] result = new int[_ilen];
		for (int i=0; i<_ilen; i++) result[i]=_indices[i];
		return result;		
	}

	
	/**
	 * return a copy of the portion of the <CODE>_values</CODE> array that 
	 * corresponds to non-zero values.
	 * @return int[] null if there are none.
	 */
	public int[] getNonDefValues() {
		if (_ilen==0) return null;
		int[] result = new int[_ilen];
		for (int i=0; i<_ilen; i++) result[i]=_values[i];
		return result;
	}

	
	/**
	 * default value is always zero.
	 * @return double // zero
	 */
	public double getDefaultValue() {
		return 0;
	}
	

  /**
   * return the i-th coordinate of this VectorIntf (i must be in the set
   * {0,1,2,...<CODE>getNumCoords()</CODE>-1}). Has O(log(_ilen)) worst-case
   * time-complexity where _ilen is the (max.) number of non-zeros in this
   * vector.
   * @param i int
   * @throws IndexOutOfBoundsException if i is not in the set mentioned above
   * @return double holding an integer quantity
   */
  public double getCoord(int i) throws IndexOutOfBoundsException {
    if (i<0 || i>=_n) throw new IndexOutOfBoundsException("index "+i+" out of bounds");
    if (_indices==null || _ilen==0) return 0.0;
    // requires a binary search in the indices.
    if (i<_indices[0] || i>_indices[_ilen-1]) return 0.0;
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
    return 0.0;
  }


  /**
   * set the i-th coordinate of this VectorIntf (i must be in the set
   * {0,1,2,...<CODE>getNumCoords()</CODE>-1}). Repeated calls to this method
   * for indices not in the original construction of this vector will eventually
   * destroy its sparsity. Has O(_ilen) worst-case time-complexity where _ilen
   * is the (max.) number of non-zeros in the array. The time-complexity
   * reduces to O(log(_ilen)) if the element to be set, is already non-zero
   * before the operation.
   * @param i int
   * @param val double must hold an integer value
   * @throws IndexOutOfBoundsException if i is not in the set mentioned above
	 * @throws IllegalArgumentException -if val does not represent an integer
   */
  public void setCoord(int i, double val) throws IndexOutOfBoundsException, 
					                                       IllegalArgumentException {
    if (i<0 || i>=_n) throw new IndexOutOfBoundsException("index "+i+" out of bounds");
		final boolean is_val_0=Double.compare(val,0.0)==0;
		if (Double.compare(val, Math.round(val))!=0) 
			throw new IllegalArgumentException("setCoord("+i+","+val+"):"+
							                           " 2nd argument must represent int");
    if (_indices==null) {
      if (is_val_0) return;  // don't do anything
      _indices = new int[1];
      _indices[0] = i;
      _values = new int[1];
      _values[0] = (int) val;
      _ilen=1;
      return;
    }
		if (_ilen==0) {  // but _indices, _values not null
			if (is_val_0) return;
			_indices[0]=i;
			_values[0]=(int)val;
			_ilen=1;
			return;
		}
    // binary search in indices
    final int ilen = _indices.length;
    int i1 = 0;
    int i2 = _ilen;
    if (_indices[0]==i) {
      if (is_val_0) shrink(0);
      else _values[0] = (int) val;
      return;
    } else if (_indices[_ilen-1] == i) {
      if (is_val_0) shrink(_ilen-1);
      else _values[_ilen-1] = (int) val;
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
      if (is_val_0) shrink(ih);
      else _values[ih] = (int) val;
      return;
    }
    else if (is_val_0) return;  // no change
    // change is necessary
    // if needed, create new arrays to insert the value for <i,val> pair.
    if (_ilen == ilen) {  // increase arrays' capacity 20%
      int[] indices = new int[ilen + ilen / 5 + 1];
      int[] values = new int[ilen + ilen / 5 + 1];
      boolean first_time = true;
      for (int j = 0; j < ilen; j++) {
        if (_indices[j] < i) {
          indices[j] = _indices[j];
          values[j] = _values[j];
        }
        else if (first_time) { // insert <i,val> pair
          indices[j] = i;
          values[j] = (int) val;
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
        values[ilen] = (int) val;
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
      _values[j+1] = (int) val;
    }
    ++_ilen;
  }


  /**
   * the purpose of this routine is to allow a traversal of the non-zeros of
   * this object as follows:
   * <br>
   * <pre>
	 * <CODE>
   * for (int i=0; i&lt;sparsevector.getNumNonZeros(); i++) {
   *   int    pos = sparsevector.getIthNonZeroPos(i);
	 *   int    val = sparsevector.getIthNonZeroVal(i);
   *   // int val = (int) sparsevector.getCoord(pos);
   * }
	 * </CODE>
   * </pre>
   * @param i int
   * @throws IndexOutOfBoundsException if i is out-of-bounds. Always throws if
   * this is the zero vector
   * @return int
   */
  public int getIthNonZeroPos(int i) throws IndexOutOfBoundsException {
    if (i<0 || i>=_ilen) throw new IndexOutOfBoundsException("index "+i+" out of bounds[0,"+_ilen+"] Vector is: "+this);
    return _indices[i];
  }
	
	
	/**
	 * get the i-th non-default value of this vector.
	 * @param i int
	 * @return double the return value is an int upcasted to double
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
	 * returns the i-th non-zero value of this vector.
	 * @param i int
	 * @return int
	 * @throws IndexOutOfBoundsException if i is out-of-bounds.
	 */
	public int getIntIthNonZeroVal(int i) throws IndexOutOfBoundsException {
    if (i<0 || i>=_ilen) 
			throw new IndexOutOfBoundsException("index "+i+" out of bounds[0,"+_ilen+
				                                  "] Vector is: "+this);
    return _values[i];		
	}


  /**
   * modifies this VectorIntf by adding the quantity m*other to it. This
   * operation may destroy the sparse nature of this object.
   * @param m double must hold an int value
   * @param other VectorIntf must hold only int values
   * @throws IllegalArgumentException if other is null or does not have the
   * same dimensions as this vector or does not hold ints only, or if m does not 
	 * hold an int
   */
  public void addMul(double m, VectorIntf other) 
	    throws IllegalArgumentException {
    if (other==null || other.getNumCoords()!=_n)
      throw new IllegalArgumentException("cannot call addMul(m,v) with v "+
                                         "having different dimensions than "+
                                         "this vector");
    for (int i=0; i<_n; i++) {
      setCoord(i, getCoord(i)+m*other.getCoord(i));
    }
  }


  /**
   * divide the components of this vector by the argument h and keep the integer
	 * part.
   * @param h double
   * @throws IllegalArgumentException if h is zero
   */
  public void div(double h) throws IllegalArgumentException {
    if (Double.isNaN(h) || Math.abs(h)<1.e-120)
      throw new IllegalArgumentException("division by (almost) zero or NaN");
    for (int i=0; i<_ilen; i++) {
      _values[i] /= h;
    }
  }


  /**
   * return true iff all components are zero.
   * @return boolean
   */
  public boolean isAtOrigin() {
    return _ilen==0;
  }
	
	
	/**
	 * reset the vector to all zeros.
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
		if (_ilen>0) {
			for (int i=0; i<_ilen-1; i++) x += "("+_indices[i]+","+_values[i]+")"+", ";
			x += "("+_indices[_ilen-1]+","+_values[_ilen-1]+")";
		}
    x += "](_n="+_n+"/ _ilen="+_ilen+")";
    return x;
  }


  /**
   * returns the number of non-zero elements in this vector.
   * @return int
   */
  public int getNumNonZeros() {
    return _ilen;
  }


  /**
   * compute the inner product of this vector with the argument passed in.
   * The operation should be fast when this vector is sparse enough, as it only
   * goes through the non-zero elements of the vector.
   * @param other VectorIntf
   * @throws IllegalArgumentException if other is null of if other's dimension
	 * doesn't match this vector's dimension
   * @return double
   */
  public double innerProduct(VectorIntf other) throws IllegalArgumentException {
    if (other==null || other.getNumCoords()!=_n)
      throw new IllegalArgumentException("dimensions don't match or null argument passed in");
    double sum=0.0;
    for (int i=0; i<_ilen; i++) {
      sum += _values[i]*other.getCoord(_indices[i]);
    }
    return sum;
  }


  /**
   * return the k-th norm of this vector.
   * @param k int
   * @throws IllegalArgumentException if k &le; 0
   * @return double
   */
  public double norm(int k) throws IllegalArgumentException {
    if (k<=0) throw new IllegalArgumentException("k<=0");
    if (k==2) return norm2();  // faster computation
    double res = 0.0;
    for (int i=0; i<_ilen; i++) {
      double absxi = Math.abs(_values[i]);
      res += Math.pow(absxi,k);
    }
    res = Math.pow(res, 1.0/(double) k);
    return res;
  }


  /**
   * short-cut for norm(2). Faster too.
   * @return double
   */
  public double norm2() {
    double res2=0.0;
    for (int i=0; i<_ilen; i++) {
      double xi = _values[i];
      res2 += (xi * xi);
    }
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
    return res;
  }


  /**
   * return true iff the other vector is exactly equal to this.
   * @param other Object
   * @return boolean
   */
  public boolean equals(Object other) {
    if (other==null || other instanceof VectorIntf == false) return false;
    VectorIntf o = (VectorIntf) other;
    if (o.getNumCoords()!=_n) return false;
    if (other instanceof SparseVectorIntf) {  // short-cut
      SparseVectorIntf osv = (SparseVectorIntf) other;
      for (int i = 0; i < _ilen; i++)
        if (Double.compare(_values[i], osv.getCoord(_indices[i])) != 0)
          return false;
      for (int i = 0; i < osv.getNumNonZeros(); i++) {
        int pi = osv.getIthNonZeroPos(i);
        if (Double.compare(osv.getCoord(pi), getCoord(pi)) != 0)
          return false;
      }
      return true;
    }
    else return o.equals(this);  // no short-cut
  }
	
	
	/**
	 * return the first value of the first non-zero element of this vector, if it
	 * exists, else return zero.
	 * @return int
	 */
	public int hashCode() {
		return _ilen > 0 ? _values[0] : 0;
	}


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

