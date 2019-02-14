package gr.ait.holmes.server.commands;

import java.io.Serializable;

public final class LoadCommand extends Command implements Serializable {

    private int r0;
    private int r1;

    public LoadCommand(int r0, int r1) {
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
