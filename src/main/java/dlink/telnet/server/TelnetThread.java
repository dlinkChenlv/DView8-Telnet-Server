package dlink.telnet.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dlink.telnet.common.TelnetCommand;

import dlink.telnet.common.TelnetTaskMap;
import dlink.telnet.common.TelnetTaskQueue;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/** 
 * <Description> <br> 
 *  
 * @author Chenlv <br>
 * @version 1.0 <br>
 * @CreateDate 2018年6月21日 <br>
 * @since V1.0 <br>
 * @see dlink.telnet.server <br>
 */
public class TelnetThread extends Thread {
    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(TelnetThread.class);
    private final ExecutorService executor;//java的线程池
    private TelnetCommand telnetCommand;
    private HashMap<UUID, TelnetOperation> telnetCommandTaskMaps = new HashMap<>();
    public TelnetThread() {
        this.executor = Executors.newFixedThreadPool(100);
    }
    @Override
    public void run() {
        while (true) {
            try {
            	telnetCommand = TelnetTaskQueue.getTelnetCommandQueue().consume();

            	if(TelnetTaskMap.getTelnetTaskMap().getTelnetCommandQueue(telnetCommand.getTaskId())==null) {
//            		
//            		telnetCommandTaskMaps.put(telnetCommand.getTaskId(), new TelnetOperation(telnetCommand.getTaskId()));
//                    executor.submit(telnetCommandTaskMaps.get(telnetCommand.getTaskId()));
            		
            		BlockingQueue<TelnetCommand> blockingQueue = new LinkedBlockingQueue<TelnetCommand>(1);
              	   blockingQueue.put(telnetCommand);
              	   TelnetTaskMap.getTelnetTaskMap().addTelnetTaskMaps(telnetCommand.getTaskId(),blockingQueue );
              	  telnetCommandTaskMaps.put(telnetCommand.getTaskId(), new TelnetOperation(telnetCommand.getTaskId()));
                   executor.submit(telnetCommandTaskMaps.get(telnetCommand.getTaskId()));
                    
            	}else {
            	TelnetTaskMap.getTelnetTaskMap().getTelnetCommandQueue(telnetCommand.getTaskId()).put(telnetCommand);
            		
            		
//            		telnetCommandTaskMaps.get(telnetCommand.getTaskId()).notify();
            	}        
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
