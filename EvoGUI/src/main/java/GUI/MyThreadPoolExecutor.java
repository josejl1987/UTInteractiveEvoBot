
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package GUI;

//~--- JDK imports ------------------------------------------------------------

import java.util.*;

/**
 *
 * @author Jose
 */
import java.util.concurrent.*;

class MyThreadPoolExecutor {
    int                                poolSize      = 4;
    int                                maxPoolSize   = 4;
    long                               keepAliveTime = 600;
    ThreadPoolExecutor                 threadPool    = null;
    final ArrayBlockingQueue<Runnable> queue         = new ArrayBlockingQueue<Runnable>(5);

    public MyThreadPoolExecutor() {
        threadPool = new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, queue);
    }

    public void runTask(Runnable task) {

        // System.out.println("Task count.."+threadPool.getTaskCount() );
        // System.out.println("Queue Size before assigning the
        // task.."+queue.size() );
        threadPool.execute(task);

        // System.out.println("Queue Size after assigning the
        // task.."+queue.size() );
        // System.out.println("Pool Size after assigning the
        // task.."+threadPool.getActiveCount() );
        // System.out.println("Task count.."+threadPool.getTaskCount() );
        System.out.println("Task count.." + queue.size());
    }

    public void shutDown() {
        threadPool.shutdown();
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
