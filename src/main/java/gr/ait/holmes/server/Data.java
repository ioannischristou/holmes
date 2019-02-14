package gr.ait.holmes.server;

import gr.ait.holmes.server.exceptions.RangeAlreadySet;
import gr.ait.holmes.server.exceptions.SizeMismatch;
import gr.ait.holmes.server.exceptions.NotInRange;
import gr.ait.holmes.server.exceptions.InvalidRange;
import gr.ait.holmes.server.exceptions.RangeNotSet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author sefr
 */

public class Data {

    private static final String FileName = "Data.dat";

    private static Object[] data = null;
    private static int range0 = -1;
    private static int range1 = -1;

    private Data() {

    }

    private static void error(String msg, boolean toExit) {
        System.err.println(msg);
        if (toExit) {
            System.err.println("Server exiting");
            System.exit(-1);
        }
    }
    
    /* range >= r0 and < r1 */
    private static void setRange(int r0, int r1) throws InvalidRange, RangeAlreadySet {
        if (validRange(range0, range1)) {
            throw new RangeAlreadySet();
        }
        if (!validRange(r0, r1)) {
            throw new InvalidRange();
        }
        range0 = r0;
        range1 = r1;
    }

    private static boolean validRange(int r0, int r1) {
        return r0 >= 0 && r0 <= r1;
    }

    private static boolean inRange(int r0, int r1) throws RangeNotSet, InvalidRange {
        if (!validRange(range0, range1)) {
            throw new RangeNotSet();
        }
        if (!validRange(r0, r1)) {
            throw new InvalidRange();
        }
        return r0 >= range0 && r1 <= range1;
    }

    public static void save() {
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

    public static void load(int r0, int r1) throws InvalidRange, RangeAlreadySet, SizeMismatch {
        setRange(r0, r1);
        try {
            FileInputStream fis = new FileInputStream(FileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (Object[]) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            error(FileName + " not found", true);
        } catch (IOException e) {
            error("IO Exception while reading " + FileName, true);
        } catch (ClassNotFoundException e) {
            error("Internal error while reading " + FileName, true);
        }
        if (r1 - r0 != data.length) {
            throw new SizeMismatch();
        }
    }

    public static Object[] get(int r0, int r1) throws RangeNotSet, InvalidRange, NotInRange {
        if (!inRange(r0, r1)) {
            throw new NotInRange();
        }

        Object[] ret = new Object[r1 - r0];
        for (int i = 0; i < r1 - r0; i++) {
            ret[i] = data[r0 - range0 + i];
            data[r0 - range0 + i] = null;
        }
        return ret;
    }

    public static Object[] read(int r0, int r1) throws RangeNotSet, InvalidRange, NotInRange {
        if (!inRange(r0, r1)) {
            throw new NotInRange();
        }

        Object[] ret = new Object[r1 - r0];
        for (int i = 0; i < r1 - r0; i++) {
            ret[i] = data[r0 - range0 + i];
        }
        return ret;
    }

    public static void put(Object[] d, int r0, int r1) throws RangeNotSet, InvalidRange, NotInRange, SizeMismatch {
        if (!inRange(r0, r1)) {
            throw new NotInRange();
        }
        if (r1 - r0 != d.length) {
            throw new SizeMismatch();
        }
        for (int i = 0; i < d.length; i++) {
            data[r0 - range0 + i] = d[i];
        }
    }
}
