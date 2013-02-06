/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synchro;

import org.apache.log4j.Logger;

import java.io.Serializable;
import synchro.Job.Estado;

/**
 *
 * @author Jose
 */
public class SyncMessage implements Serializable {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SyncMessage.class);

    
   public Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;   
    }

    public Estado getStatus() {
        return status;
    }

    public void setStatus(Estado status) {
        this.status = status;
    }
    
    int id;
    private Estado status; 
    
    public SyncMessage(int id,Estado stat)
    {
        this.id=id;
        this.status=stat;
    }
}
