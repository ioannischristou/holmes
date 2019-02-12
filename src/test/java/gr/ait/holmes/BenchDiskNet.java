/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ait.holmes;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 *
 * @author sefr
 */
public class BenchDiskNet {

    private static final String FName = "data.dat";
    private static final int BSIZE = 0x40000000; // 2^30 = 1Gb

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        byte[] data = new byte[BSIZE];
        long t0, t1;

        t0 = System.currentTimeMillis();
        for (int i = 0; i < BSIZE; i++) {
            data[i] = (byte) (i % 256);
        }
        t1 = System.currentTimeMillis();

        System.out.println("Time for array initialization: " + (t1 - t0));

        try {
            t0 = System.currentTimeMillis();
            FileOutputStream fos = new FileOutputStream(FName);
            fos.write(data);
            fos.close();
            t1 = System.currentTimeMillis();

            System.out.println("Time for disk write: " + (t1 - t0));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        for (int i = 0; i < BSIZE; i++) {
            data[i] = 0;
        }

        try {
            t0 = System.currentTimeMillis();
            FileInputStream fis = new FileInputStream(FName);
            int nr = fis.read(data);
            fis.close();
            t1 = System.currentTimeMillis();

            if (nr != BSIZE) {
                System.out.println("Incorrect number of bytes read");
            } else {
                System.out.println("Time for disk read: " + (t1 - t0));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
