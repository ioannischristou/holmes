package gr.ait.holmes;

import gr.ait.holmes.server.exceptions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author sefr
 */
public class DataNodeImpl implements DataNodeIntf {

  private static final String FileName = "Data.dat";

  private Serializable[] data = null;
  private long range0 = -1;
  private long range1 = -1;

  private DataNodeImpl(KeyRange kr) throws InvalidRange, SizeMismatch {

    range0 = kr.getStart();
    range1 = kr.getEnd();
    try {
      FileInputStream fis = new FileInputStream(FileName);
      ObjectInputStream ois = new ObjectInputStream(fis);
      data = (Serializable[]) ois.readObject();
      ois.close();
      fis.close();
    } catch (FileNotFoundException e) {
      error(FileName + " not found", true);
    } catch (IOException e) {
      error("IO Exception while reading " + FileName, true);
    } catch (ClassNotFoundException e) {
      error("Internal error while reading " + FileName, true);
    }
    if (range1 - range0 != data.length) {
      throw new SizeMismatch();
    }
  }

  private static void error(String msg, boolean toExit) {
    System.err.println(msg);
    if (toExit) {
      System.err.println("Server exiting");
      System.exit(-1);
    }
  }

  public void save() {
    try {
      FileOutputStream fos = new FileOutputStream(FileName);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(data);
      oos.close();
      fos.close();
    } catch (IOException e) {
      error("IO Exception while writing " + FileName, true);
    }
  }

  public Serializable getValue(long primarykey, long secondarykey) throws NotInRange {
    if (primarykey < range0 || primarykey > range1) {
      throw new NotInRange();
    }
    return data[primarykey - range0];
  }

  public Serializable getValueIfPresent(long primarykey, long secondarykey) throws NotInRange {
    if (primarykey < range0 || primarykey > range1) {
      throw new NotInRange();
    }
    return data[primarykey - range0];
  }

  public List<Serializable> getValuesForRange(KeyRangeSet primarykeys, long secondarykey) throws NotInRange {
    ArrayList<Serializable> vals = new ArrayList<Serializable>();

    for (long i = 0; i < primarykeys.getNumRanges(); i++) {
      KeyRange kr = primarykeys.getRange(i);
      long start = kr.getStart();
      long end = kr.getEnd();
      if (start < range0 || end > range1) {
        throw new NotInRange();
      }
      vals.add(data[i - start]);
    }
    return vals;
  }

  public void putTuple(long primarykey, long secondarykey, Serializable value) throws NotInRange {
    if (primarykey < range0 || primarykey > range1) {
      throw new NotInRange();
    }
    data[primaryKey] = value;
  }

  public Serializable removeTuple(long primarykey, long secondarykey) throws NotInRange {
    if (primarykey < range0 || primarykey > range1) {
      throw new NotInRange();
    }
    Serializable ret = data[primaryKey];
    data[primaryKey] = null;
    return ret;
  }
}