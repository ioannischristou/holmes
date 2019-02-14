/*
 * Code is distibuted as Open Source, under the LGPL2 license, without any waranty of fitness of use.
 */

package gr.ait.holmes.arrays;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import gr.ait.holmes.utils.Messenger;


/**
 * class represents very large matrices that are stored on disk but cannot be
 * easily retrieved in memory all at once. The class therefore utilizes soft-
 * references to hold chunks of the matrix in memory, with enough intelligence
 * to retrieve data not currently in memory from disk. Representation is row-
 * based.
 * @author itc
 */
public final class BigDiskSupportedDblArray2SparseMatrix implements MatrixIntf {
  private final SoftReference<DblArray1SparseVector>[] _rows;  // data
  private final String _datafile;  // underlying data source
  private final int _r;
  private final int _c;
  
  private final static int _MIN_ROWS_CHUNK_2_READ = 100000;

  private ReentrantReadWriteLock _rwRowChunkLocks[];
  

  /**
   * construct a big matrix of sparse vectors, supported by an underlying
   * text file that serves as the data source. No data are read at construction
   * time; only the first line of the file containing the so-called header 
   * describing the matrix dimensions is read, and instead when rows or elements 
   * are requested, pieces of the data are read from disk and saved into 
   * soft-references in memory.
   * @param filename String
   * @throws IOException 
   */
  public BigDiskSupportedDblArray2SparseMatrix(String filename) 
    throws IOException {
    _datafile = filename;
    BufferedReader br = new BufferedReader(new FileReader(_datafile));
    String line = br.readLine();
    br.close();
    String[] data = line.split(" ");
    _r = Integer.parseInt(data[0]);
    _c = Integer.parseInt(data[1]);
    _rows = new SoftReference[_r];
    int num_chunks = _r / (int) Math.min(_MIN_ROWS_CHUNK_2_READ, _r);
    if (_r % num_chunks > 0) ++num_chunks;
    _rwRowChunkLocks = new ReentrantReadWriteLock[num_chunks];
    for (int i=0; i<_rwRowChunkLocks.length; i++) 
      _rwRowChunkLocks[i] = new ReentrantReadWriteLock();
  }
  
  
  /**
   * get the number of rows of this matrix.
   * @return int
   */
  public int getNumRows() {
    return _r;
  }
  
  
  /**
   * get the number of columns of this matrix.
   * @return int
   */
  public int getNumCols() {
    return _c;
  }
  
  
  /**
   * the main operation of this object: get the value of its (i,j)-th cell.
   * @param i int in [0,#rows-1]
   * @param j int in [0,#cols-1]
   * @return double
   * @throws IndexOutOfBoundsException if the arguments are out-of-bounds 
   * (unchecked)
   * @throws IOException if the data source for some reason is not available 
   * (unchecked)
   */
  public double getCoord(int i, int j) {
    checkRanges(i,j);
    double result = Double.NaN;
    int start_chunk = getChunkReadLock(i);
    SoftReference<DblArray1SparseVector> rowrefi = _rows[i];
    if (rowrefi==null) {  // chunk into which i-th row belongs not read yet
      try {
        _rwRowChunkLocks[start_chunk].readLock().unlock();
        _rwRowChunkLocks[start_chunk].writeLock().lock();
        rowrefi = _rows[i];
        if (rowrefi!=null) {  // check again, maybe now the chunk's been read
          DblArray1SparseVector ri = rowrefi.get();
          if (ri!=null) return ri.getCoord(j);
        }
        if (_rows[i]==null) {
          BufferedReader br=null;
          try {
            br = new BufferedReader(new FileReader(_datafile));
            br.readLine();  // go past header
            int cnt=0;
            while (cnt++<start_chunk) {
              br.readLine();
            }
            for (int c=0; c<_MIN_ROWS_CHUNK_2_READ; c++) {
              String line = br.readLine();
              if (line==null) break;  // EOF
              StringTokenizer st = new StringTokenizer(line," ");
              DblArray1SparseVector v = new DblArray1SparseVector(_c);
              while (st.hasMoreTokens()) {
                String token = st.nextToken();
                String[] nds = token.split(",");
                int dim = Integer.parseInt(nds[0])-1;
                double val = Double.parseDouble(nds[1]);
                v.setCoord(dim, val);
              }
              _rows[start_chunk+c] = new SoftReference<>(v);
              if (start_chunk+c==i) {
                rowrefi = _rows[i];
                result = v.getCoord(j);
              }
            }
            return result;
          }
          catch (IOException e) {
            throw new IllegalStateException("data file no longer available");
          }
          finally {
            try {
              if (br!=null) br.close();
            }
            catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
        // else fall-off to reading again
      }
      finally {
        _rwRowChunkLocks[start_chunk].writeLock().unlock();
      }
    }  // _rows[i]==null
    // rowrefi not null, but maybe actual ref is gc'ed
    DblArray1SparseVector vi = rowrefi.get();
    if (vi==null) {  // chunk was read, but row-i evicted by gc
      // only read row i
      try {
        _rwRowChunkLocks[start_chunk].readLock().unlock();
        _rwRowChunkLocks[start_chunk].writeLock().lock();
        vi = _rows[i].get();  // check the soft-ref value again
        if (vi==null) {
          BufferedReader br=null;
          try {
            br = new BufferedReader(new FileReader(_datafile));
            br.readLine();  // go past header
            int cnt=0;
            while (cnt++<i) {
              br.readLine();
            }
            for (int c=0; c<1; c++) {
              String line = br.readLine();
              StringTokenizer st = new StringTokenizer(line," ");
              DblArray1SparseVector v = new DblArray1SparseVector(_c);
              while (st.hasMoreTokens()) {
                String token = st.nextToken();
                String[] nds = token.split(",");
                int dim = Integer.parseInt(nds[0])-1;
                double val = Double.parseDouble(nds[1]);
                v.setCoord(dim, val);
              }
              _rows[i+c] = new SoftReference<>(v);  // put it back in
              if (c==0) result = v.getCoord(j);
            }
            return result;
          }
          catch (IOException e) {
            throw new IllegalStateException("data file no longer available");
          }
          finally {
            try {
              if (br!=null) br.close();
            }
            catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
        else return vi.getCoord(j);
      }
      finally {
        _rwRowChunkLocks[start_chunk].writeLock().unlock();
      }      
    }  // vi==null
    else {
      _rwRowChunkLocks[start_chunk].readLock().unlock();
      return vi.getCoord(j);
    }
  }
  
  
  /**
   * always throws for now.
   * @param i int
   * @param j int
   * @param val double
   */
  public void setCoord(int i, int j, double val) {
    throw new UnsupportedOperationException("setCoord() not yet supported");
  }
  
  
  /**
   * get the entire i-th row of this matrix as a sparse vector. Obviously, we
   * assume the row fits in main memory.
   * @param i int in [0,_r-1]
   * @return DblArray1SparseVector 
   * @throws IndexOutOfBoundsException if the argument is not within range.
   */
  public DblArray1SparseVector getIthRow(int i) {
    checkRanges(i,0);
    int start_chunk = getChunkReadLock(i);
    boolean read_locked = true;
    boolean write_locked = false;
    DblArray1SparseVector ri=null;
    try {
      SoftReference<DblArray1SparseVector> refi = _rows[i];
      if (refi!=null) {
        ri = refi.get();
        if (ri!=null) return ri;
      }
      // read entire chunk
      _rwRowChunkLocks[start_chunk].readLock().unlock();
      read_locked=false;
      _rwRowChunkLocks[start_chunk].writeLock().lock();
      write_locked = true;
      // now check again
      refi = _rows[i];
      if (refi!=null) {
        ri = refi.get();
        if (ri!=null) return ri;
      }
      // no luck, do the work  
      //BufferedReader br=null;
      try(BufferedReader br=new BufferedReader(new FileReader(_datafile))) {
        //br = new BufferedReader(new FileReader(_datafile));
        br.readLine();  // go past header
        int cnt=0;
        while (cnt++<start_chunk) {
          br.readLine();
        }
        for (int c=0; c<_MIN_ROWS_CHUNK_2_READ; c++) {
          String line = br.readLine();
          if (line==null) break;  // EOF
          StringTokenizer st = new StringTokenizer(line," ");
          DblArray1SparseVector v = new DblArray1SparseVector(_c);
          while (st.hasMoreTokens()) {
            String token = st.nextToken();
            String[] nds = token.split(",");
            int dim = Integer.parseInt(nds[0])-1;
            double val = Double.parseDouble(nds[1]);
            v.setCoord(dim, val);
          }
          _rows[start_chunk+c] = new SoftReference<>(v);
          if (start_chunk+c==i) {
            ri = v;
          }
        }
        return ri;
      }
      catch (IOException e) {
        throw new IllegalStateException("data file no longer available");
      }    
    }
    finally {
      if (read_locked) _rwRowChunkLocks[start_chunk].readLock().unlock();
      if (write_locked) _rwRowChunkLocks[start_chunk].writeLock().unlock();
    }
  }
  
  
  /**
   * checks the range of the parameters. If not correct, it throws.
   * @param r int must be in [0,_r-1]
   * @param c int must be in [0,_c-1]
   * @throws IndexOutOfBoundsException if either of the parameters are not 
   * within range.
   */
  private void checkRanges(int r, int c) {
    if (r<0 || r>=_r || c<0 || c>=_c) 
      throw new IndexOutOfBoundsException("Indices ("+r+","+c+") out of range");
  }
  
  
  /**
   * finds the index of the chunk into which row belongs, and gets a read-lock
   * on it.
   * @param row int
   * @return int the chunk-index of the row passed in
   */
  private int getChunkReadLock(int row) {
    int start_chunk = row / _MIN_ROWS_CHUNK_2_READ;
    _rwRowChunkLocks[start_chunk].readLock().lock();
    return start_chunk;
  }
}
