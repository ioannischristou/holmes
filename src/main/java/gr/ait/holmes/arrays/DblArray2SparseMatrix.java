/*
 * Code is distibuted as Open Source, under the LGPL2 license, without any waranty of fitness of use.
 */

package gr.ait.holmes.arrays;


/**
 * sparse 2-D matrix class containing doubles. The representation is row-based.
 * @author itc
 */
public class DblArray2SparseMatrix implements MatrixIntf {
	private DblArray1SparseVector[] _rows;
	
	
	/**
	 * normal constructor specifies the number of rows and columns of this matrix.
	 * @param rows int
	 * @param cols int
	 */
	public DblArray2SparseMatrix(int rows,int cols) {
		_rows = new DblArray1SparseVector[rows];
		for (int i=0; i<rows; i++) {
			_rows[i] = new DblArray1SparseVector(cols);
		}
	}
	
	
	/**
	 * copy constructor performs deep copy of the argument passed in.
	 * @param A 2D matrix
	 */
	public DblArray2SparseMatrix(DblArray2SparseMatrix A) {
		int rows = A.getNumRows();
		_rows = new DblArray1SparseVector[rows];
		for (int i=0; i<rows; i++) {
			_rows[i] = (DblArray1SparseVector) A.getIthRow(i).newInstance();
		}
	}
	
	
	/**
	 * return the a_{i,j} element of this matrix.
	 * @param i int the row number starting from zero.
	 * @param j int the column number starting from zero.
	 * @return double the element at [i,j] position.
	 */
	public double getCoord(int i, int j) {
		return _rows[i].getCoord(j);
	}
	
	
	/**
	 * get the number of rows of this matrix.
	 * @return int
	 */
	public int getNumRows() { return _rows.length; }
	
	
	/**
	 * get the number of columns of this matrix.
	 * @return int
	 */
	public int getNumCols() { return _rows[0].getNumCoords(); }
	
	
	/**
	 * sets the value of the a_{i,j} element of this matrix.
	 * @param i int the row number starting from zero.
	 * @param j int the column number starting from zero.
	 * @param val double the value to set for the element at [i,j] position.
	 */
	public void setCoord(int i, int j, double val) {
    _rows[i].setCoord(j, val);
	}
	
	
	/**
	 * get the i-th row of this matrix.
	 * @param r int row number starting from zero.
	 * @return DblArray1SparseVector
	 */
	public DblArray1SparseVector getIthRow(int r) {
		return _rows[r];
	}
	
	
	/**
	 * reset this matrix to the zero matrix.
	 */
	public void reset() {
		for (int i=0; i<_rows.length; i++) {
			_rows[i].reset();
		}
	}
	
	
	public String toString() {
		String res = "";
		for (int row=0; row<_rows.length; row++) {
			res += "| ";
			for (int col=0; col<_rows[0].getNumCoords(); col++) {
				res += _rows[row].getCoord(col);
				if (col<_rows[0].getNumCoords()-1) res += " ";
				else res += " |\n";
			}
		}
		return res;
	}
}
