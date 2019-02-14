/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ait.holmes.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/**
 *
 * @author sefr
 */
public class Server {

    /**
     * @param args the command line arguments
     */
    private static final String PropFile = "Server.properties";
    
    private static int ServerPort = 10000;
    private static ServerSocket sskt = null;
    private static Socket cskt = null;

    private static void error(String msg, boolean toExit) {
        System.err.println(msg);
        if (toExit) {
            System.err.println("Server exiting");
            System.exit(-1);
        }
    }

    public static void main(String[] args) {

        System.out.println("CEFS Server v1.0");

        Properties props = new Properties();
        try {
            props.load(new FileInputStream(PropFile));
            ServerPort = Integer.parseInt(props.getProperty("ServerPort"));
        } catch (FileNotFoundException e) {
            error("Server property file not found. Using default values.", false);
        } catch (IOException e) {
            error("Error while reading server properties. Using default values.", false);
        }

        try {
            sskt = new ServerSocket(ServerPort);
        } catch (IOException e) {
            error("Can not open server socket", true);
        }

        while (true) {
            try {
                cskt = sskt.accept();
            } catch (IOException e) {
                error("Error while accepting client request", false);
                continue;
            }
            new ServerThread(cskt).start();
        }
    }
}
