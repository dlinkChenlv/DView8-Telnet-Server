package dlink.telnet.server;

import dlink.telnet.common.TelnetCommand;
import dlink.telnet.common.TelnetCommandAndCon;
import dlink.telnet.common.TelnetResultMap;
import dlink.telnet.common.TelnetTaskMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;


/** 
 * <Description> 执行telnet操作的线程 <br> 
 *  
 * @author ChenLv <br>
 * @version 1.0 <br>
 * @CreateDate 2018年6月22日 <br>
 * @since V1.0 <br>
 * @see dlink.telnet.server <br>
 */
public class TelnetOperation extends Thread {
    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(TelnetOperation.class);

    private TelnetCommand telnetCommand;
    private BlockingQueue<TelnetCommand> blockingQueue;
    private TelnetCommandAndCon telnetCommandLastTime=null;
    private boolean  ifConnetFirst;
    private UUID taskId=null;
    private static TelnetServer telnetServer;
    public TelnetOperation(UUID taskId) {
        telnetServer=new telnetServerImpl();
        this.taskId=taskId;
        ifConnetFirst=false;
        blockingQueue = TelnetTaskMap.getTelnetTaskMap().getTelnetCommandQueue(taskId);
        //shServers=new SshServerImpl();
    }

    @Override
    public void run() {
    	TelnetCommand TelnetCommandcc=null;
        while (true) {
            try {

            	telnetCommand = blockingQueue.take();
                if (telnetCommand != null && taskId.equals(telnetCommand.getTaskId())) { 
                    if(telnetCommand.getHost() !=null) {
                    	telnetCommandLastTime=telnetServer.connect(telnetCommand.getHost(), telnetCommand.getPort());
                    	if(telnetCommandLastTime.getRestInfo()==null) {
                    		telnetCommandLastTime.setRestInfo("正在连接"+telnetCommand.getHost()+"...无法打开到主机的连接。 在端口 +"+telnetCommand.getPort()+"+: 连接失败");               		
                    	}else {
                    		ifConnetFirst=true;
                    	}
                    	TelnetResultMap.getTelnetTaskMap().addTelnetResultMaps(telnetCommand.getTaskId(),telnetCommandLastTime.getRestInfo());
                    	LOG.info(telnetCommandLastTime.getRestInfo());
                    }else if(ifConnetFirst){
                    	TelnetCommandcc=telnetServer.login(telnetCommand,telnetCommandLastTime.getTelnetClient());
                    	LOG.info(TelnetCommandcc.getRestInfo());
                    	TelnetResultMap.getTelnetTaskMap().addTelnetResultMaps(telnetCommand.getTaskId(),TelnetCommandcc.getRestInfo());
                    	ifConnetFirst=false;
                    }else {
                    	TelnetCommandcc=telnetServer.sendCmd(telnetCommand,telnetCommandLastTime.getTelnetClient());
                    	TelnetResultMap.getTelnetTaskMap().addTelnetResultMaps(telnetCommand.getTaskId(),TelnetCommandcc.getRestInfo());

                    	LOG.info(TelnetCommandcc.getRestInfo());
                    	if(TelnetCommandcc.getRestInfo().indexOf("Login incorrect")!=-1) {
                    		TelnetTaskMap.getTelnetTaskMap().removeTelnetTaskMaps(taskId);
                    		System.out.println("登陆失败，用户名或密码错误！");
                    		break;
                    	}
                    	
                    }
                    
                    telnetCommand=null;
                  
                }else {
                	//TelnetCommandQueue.getTelnetCommandQueue().produce(telnetCommand);
                	//Thread.sleep(100000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
