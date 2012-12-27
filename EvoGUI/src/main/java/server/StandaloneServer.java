
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package server;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.log4j.Logger;
import synchro.WorkQueueServer;

/**
 * @author Jose
 */
public class StandaloneServer {

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(StandaloneServer.class);

    public static void main(String args[]) {
        if (logger.isDebugEnabled()) {
            logger.debug("main(String[]) - start");    // $NON-NLS-1$
        }

        WorkQueueServer server = new WorkQueueServer(4000);

        server.run();

        if (logger.isDebugEnabled()) {
            logger.debug("main(String[]) - end");    // $NON-NLS-1$
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
