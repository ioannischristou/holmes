/*
 * Code is distibuted as Open Source, under the LGPL2 license, without any waranty of fitness of use.
 */

package gr.ait.holmes.utils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * class controlling messaging to various output streams. Used for logging
 * purposes as an alternative to log4j.
 * The class methods are all thread-safe (i.e. reentrant)
 * @author itc
 */
public class Messenger {
	/**
	 * _os is the <CODE>PrintStream</CODE> on which this <CODE>Messenger</CODE> 
	 * object sends its messages.
	 */
  private PrintStream _os=null;
	/**
	 * _dbgLvl declared volatile in a manner that is consistent even for JDK1.4
	 * memory model. Default value is <CODE>Integer.MAX_VALUE</CODE> for printing
	 * all messages of any debug level.
	 */
  private volatile int _dbgLvl=Integer.MAX_VALUE;  // default: print all
	/**
	 * _showTimeStamp forces each message to print the timestamp in the beginning
	 * of the message.
	 */
	private boolean _showTimeStamp;  // default: false
	/**
	 * all <CODE>Messenger</CODE> objects "live" in this map, which holds them by
	 * name. Default messenger is named "default", and goes to 
	 * <CODE>System.err</CODE> (stderr) print stream.
	 */
  private static final HashMap _instances=new HashMap();  // map<String name, 
	                                                        //     Messenger m>


  /**
   * private constructor in accordance with the Singleton(s) Design Pattern.
   * @param os OutputStream
   * @throws IllegalArgumentException if the os argument is null
   */
  private Messenger(OutputStream os) throws IllegalArgumentException {
    if (os==null) 
			throw new IllegalArgumentException("Messenger.ctor(): null arg");
    _os = new PrintStream(os);
  }


  /**
   * close the PrintStream stream associated with this Messenger, flushing any
   * unwritten content before.
   */
  public void close() {
    if (_os != null) {
      _os.flush();
      _os.close();
      _os = null;
    }
  }


  protected void finalize() throws Throwable {
    try {
      if (_os != null) {
        _os.flush();
        _os.close();
        _os = null;
      }
    }
    finally {
      super.finalize();  // recommended practice
    }
  }


  /**
   * get the default Messenger object
   * @return Messenger
   */
  public synchronized static Messenger getInstance() {
    Messenger instance = (Messenger) _instances.get("default");
    if (instance==null) {
      instance = new Messenger(System.err);
      _instances.put("default", instance);
    }
    return instance;
  }


  /**
   * get the Messenger associated with the given name
   * @param name String
   * @return Messenger
   */
  public synchronized static Messenger getInstance(String name) {
    Messenger instance = (Messenger) _instances.get(name);
    return instance;
  }


  /**
   * associate a Messenger with the given name with the PrintStream passed in.
   * @param name String
   * @param os OutputStream
   */
  public synchronized static void setInstance(String name, OutputStream os) {
    _instances.put(name, new Messenger(os));
  }

	
	/**
	 * utility print method for object arrays.
	 * @param array Object[]
	 * @return String
	 */
	public static String toString(Object[] array) {
		String ret="[";
		if (array==null) ret += "<null>";
		else {
			for (int i=0; i<array.length; i++) {
				ret += array[i];
				if (i<array.length-1) ret += ",";
			}
		}
		ret += "]";
		return ret;
	}


  /**
   * set a "debug level" to be later used for printing out messages according
   * to the debug level of the message. Not synchronized since 
	 * <CODE>_dbgLvl</CODE> is volatile, and is only read/written, not incremented
	 * or modified in other ways.
   * @param lvl int
   */
  public void setDebugLevel(int lvl) {
    _dbgLvl = lvl;
  }
	

	/**
	 * get the current "debug level" of this Messenger object.
	 * @return int
	 */
	public int getDebugLvl() {
		return _dbgLvl;
	}
	
	
	/**
	 * force this Messenger to print the current time when showing any message via
	 * <CODE>msg(String)</CODE>.
	 */
	public synchronized void setShowTimeStamp() {
		_showTimeStamp = true;
	}
	
	
	/**
	 * set the value of the member <CODE>_showTimeStamp</CODE>, used in showing
	 * time-stamps before every message.
	 * @param v boolean
	 */
	public synchronized void setShowTimeStamp(boolean v) {
		_showTimeStamp = v;
	}
	

	/**
	 * get the current value of the member <CODE>_showTimeStamp</CODE> of this
	 * Messenger.
	 * @return boolean
	 */
	public synchronized boolean getShowTimeStamp() {
		return _showTimeStamp;
	}
	
	
  /**
   * sends the msg to the PrintStream of this Messenger iff the debug level lvl
   * is less than or equal to the debug level set by a prior call to
   * <CODE>setDebugLevel(dlvl)</CODE>. When the debug-level argument is not 
	 * enough to get printed, the method does not synchronize (though the access
	 * to _dbgLvl is essentially a "memory barrier".)
   * @param msg String
   * @param lvl int
   */
  public void msg(String msg, int lvl) {
    if (lvl <= _dbgLvl) {
			synchronized (this) {
				if (_os!=null) {
					if (_showTimeStamp) {
						Date date = new Date(System.currentTimeMillis());
						DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
						String dateFormatted = formatter.format(date);
						_os.print("["+dateFormatted+"]:");
					}
		      _os.println(msg);
				  _os.flush();
				}
			}
		}
  }
}

