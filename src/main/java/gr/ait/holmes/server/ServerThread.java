/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ait.holmes.server;

import gr.ait.holmes.server.commands.ExitCommand;
import gr.ait.holmes.server.commands.PutCommand;
import gr.ait.holmes.server.commands.SaveCommand;
import gr.ait.holmes.server.commands.LoadCommand;
import gr.ait.holmes.server.commands.QuitCommand;
import gr.ait.holmes.server.commands.GetCommand;
import gr.ait.holmes.server.commands.Command;
import gr.ait.holmes.server.commands.ReadCommand;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 *
 * @author sefr
 */
public class ServerThread extends Thread {

    private Socket cskt = null;
    private static ObjectInputStream ois = null;
    private static ObjectOutputStream oos = null;

    public ServerThread(Socket cskt) {
        this.cskt = cskt;
    }

    private void error(String msg, boolean toExit) {
        System.err.println(msg);
        if (toExit) {
            System.err.println("Server exiting");
            cleanUp();
            System.exit(-1);
        }
    }

    private void cleanUp() {
        if (oos != null) {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (ois != null) {
            try {
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (cskt != null) {
            try {
                cskt.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        try {
            ois = new ObjectInputStream(cskt.getInputStream());
            oos = new ObjectOutputStream(cskt.getOutputStream());
        } catch (IOException e) {
            error("Error while opening streams to the client socket", false);
            return;
        }

        while (true) {
            Command c = null;
            try {
                c = (Command) ois.readObject();
                if (c instanceof ExitCommand) {
                    cleanUp();
                    break;
                }
                if (c instanceof QuitCommand) {
                    System.err.println("CEFS Server exiting");
                    System.exit(0);
                }
                oos.writeObject(execute(c));
            } catch (ClassNotFoundException e) {
                error("Internal error: non-command object read from the client",
                        false);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static Command execute(Command c) {
        c.setResult(null);
        c.setException(null);
        try {
            if (c instanceof LoadCommand) {
                int r0 = ((LoadCommand) c).getLow();
                int r1 = ((LoadCommand) c).getHigh();
                Data.load(r0, r1);
                c.setResult(new Object());
            } else if (c instanceof SaveCommand) {
                Data.save();
                c.setResult(new Object());
            } else if (c instanceof GetCommand) {
                int r0 = ((GetCommand) c).getLow();
                int r1 = ((GetCommand) c).getHigh();
                Object[] res = Data.get(r0, r1);
                c.setResult(res);
            } else if (c instanceof ReadCommand) {
                int r0 = ((ReadCommand) c).getLow();
                int r1 = ((ReadCommand) c).getHigh();
                Object[] res = Data.read(r0, r1);
                c.setResult(res);
            } else if (c instanceof PutCommand) {
                int r0 = ((PutCommand) c).getLow();
                int r1 = ((PutCommand) c).getHigh();
                Object[] data = ((PutCommand) c).getData();
                Data.put(data, r0, r1);
                c.setResult(new Object());
            } else {
                c.setException(new Exception("Command not recofnized"));
            }
        } catch (Exception e) {
            c.setException(e);
        }
        return c;
    }
}
