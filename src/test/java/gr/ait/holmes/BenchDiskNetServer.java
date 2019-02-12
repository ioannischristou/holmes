/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ait.holmes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author sefr
 */
public class BenchDiskNetServer {

    private static int ServerPort = 10000;
    private static final String FName = "data.dat";
    private static final int BSIZE = 100000000;

    private static void error(String msg, boolean toExit) {
        System.err.println("Data Server: " + msg);
        if (toExit) {
            System.err.println("Data Server exiting.");
            System.exit(-1);
        }
    }

    public static void main(String[] args) {

        byte[] data = new byte[BSIZE];
        long t0, t1;

        System.out.println("Data Server starting.");

        try {
            t0 = System.currentTimeMillis();
            FileInputStream fis = new FileInputStream(FName);
            int nr = fis.read(data);
            fis.close();
            t1 = System.currentTimeMillis();

            if (nr != BSIZE) {
                System.out.println("Data Server: Incorrect number of bytes read");
            } else {
                System.out.println("Data Server: " + (t1 - t0) + "ms for disk read.");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        ServerSocket sskt = null;

        try {
            sskt = new ServerSocket(ServerPort);
        } catch (IOException e) {
            error("Can not open server socket", true);
        }

        Socket cskt = null;
        try {
            cskt = sskt.accept();
        } catch (IOException e) {
            error("Error while accepting client request", true);
        }

        DataInputStream dis = null;
        DataOutputStream dos = null;
        try {
            dis = new DataInputStream(cskt.getInputStream());
            dos = new DataOutputStream(cskt.getOutputStream());
        } catch (IOException e) {
            error("Error while opening streams to the client socket", true);
        }

        while (true) {
            try {
                int n = dis.readInt();
                if (n <= 0) {
                    break;
                }
                t0 = System.currentTimeMillis();
                dos.write(data, 0, n);
                t1 = System.currentTimeMillis();
                System.out.println("Data Server: " + (t1 - t0) + "ms for socket write (" + n + " bytes).");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if (dis != null) {
                dis.close();
            }
            if (dos != null) {
                dos.close();
            }
        } catch (IOException e) {
            System.err.println("Can not close data streams to server.");
        }

        System.out.println("Data Server exiting.");
    }
}
