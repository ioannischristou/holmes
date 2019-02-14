/*
 * Code is distibuted as Open Source, under the LGPL2 license, without any waranty of fitness of use.
 */

package gr.ait.holmes.arrays;

/**
 * interface of matrices in row-representation.
 * @author itc
 */
public interface MatrixIntf {
  /**
   * return the entire r-th row of this matrix as a vector object.
   * @param r int
   * @return VectorIntf
   * @throws IllegalArgumentException unchecked
   */
  public VectorIntf getIthRow(int r);

  
  /**
   * get the (i,j) cell of this matrix.
   * @param i int
   * @param j int
   * @return double
   */
  public double getCoord(int i, int j);
  
  
  /**
   * set the (i,j) cell of this matrix to the specified value.
   * @param i int
   * @param j int
   * @param val double
   */
  public void setCoord(int i, int j, double val);
  
  
  /**
   * get the number of rows of this matrix.
   * @return int
   */
  public int getNumRows();
  
  
  /**
   * get the number of columns of this matrix.
   * @return int
   */
  public int getNumCols();
  
}
