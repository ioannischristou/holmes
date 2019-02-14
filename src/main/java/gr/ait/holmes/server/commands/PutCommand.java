/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ait.holmes.server.commands;

import java.io.Serializable;

/**
 *
 * @author sefr
 */
public class PutCommand extends Command implements Serializable {
    
    private Object[] data;
    private int r0, r1;
    
    public PutCommand(Object[] data, int r0, int r1) {
        this.data = data;
        this.r0 = r0;
        this.r1 = r1;
    }
    
    public Object[] getData() {
        return data;
    }
    
    public int getLow() {
        return r0;
    }
    
    public int getHigh() {
        return r1;
    }
}
