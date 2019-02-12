/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ait.holmes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 *
 * @author sefr
 */
public class BenchDiskNetClient {

    private static final int BSIZE = 100000000;
    private static String ServerHost = "localhost";
    private static int ServerPort = 10000;

    private static void error(String msg, boolean toExit) {
        System.err.println("Data Client: " + msg);
        if (toExit) {
            System.err.println("Data Client exiting.");
            System.exit(-1);
        }
    }

    public static void main(String[] args) {

        Socket cskt = null;
        try {
            cskt = new Socket(ServerHost, ServerPort);
        } catch (UnknownHostException e) {
            error("Unknown host " + ServerHost, true);
        } catch (IOException e) {
            error("Can not open socket to host " + ServerHost, true);
        }

        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            dos = new DataOutputStream(cskt.getOutputStream());
            dis = new DataInputStream(cskt.getInputStream());
        } catch (IOException e) {
            error("Can not open connection to " + ServerHost, true);
        }

        byte[] data = new byte[BSIZE];
        while (true) {
            try {
                Scanner scn = new Scanner(System.in);
                System.out.print("Number of bytes to read from server: ");
                int n = scn.nextInt();
                dos.writeInt(n);
                if (n <= 0) {
                    break;
                }
                long t0 = System.currentTimeMillis();
                int nacc = 0;
                while (nacc < n) {
                    int nr = dis.read(data, nacc, n - nacc);
                    nacc += nr;
                }
                long t1 = System.currentTimeMillis();
                System.out.println("Client: " + (t1 - t0) + "ms to read " + n + " bytes from server.");
            } catch (IOException e) {
                System.err.println("Client: error while communicating with the server.");
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
    }
}
