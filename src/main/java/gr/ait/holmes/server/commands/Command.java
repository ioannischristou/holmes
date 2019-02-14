package gr.ait.holmes.server.commands;

import java.io.*;

public abstract class Command implements Serializable {

    protected Object result = null;
    protected Exception ex = null;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Exception getException() {
        return ex;
    }

    public void setException(Exception ex) {
        this.ex = ex;
    }
}
