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
public class GetCommand extends Command implements Serializable {
    
    private int r0, r1;
    
    public GetCommand(int r0, int r1) {
        this.r0 = r0;
        this.r1 = r1;
    }
    
    public int getLow() {
        return r0;
    }
    
    public int getHigh() {
        return r1;
    }
}
