package dlink.telnet.server;

import dlink.telnet.common.TelnetCommand;
import dlink.telnet.common.TelnetCommandAndCon;

import java.io.IOException;

import org.apache.commons.net.telnet.TelnetClient;


/** 
 * <Description> 执行telnet相关操作的接口<br> 
 *  
 * @author ChenLv <br>
 * @version 1.0 <br>
 * @CreateDate 2018年6月21日 <br>
 * @since V1.0 <br>
 * @see dlink.telnet.server <br>
 */
public interface TelnetServer {
	

	 /**
     * Description: 连接telnet服务器<br> 
     *  
     * @author ChenLv<br>
     * @param host     远程主机ip地址
     * @param port     sftp连接端口，null 时为默认端口
     * @return 是否连接成功<br>
     */ 
    public TelnetCommandAndCon connect(String host, Integer port) throws IOException;
    
    /**
     * Description: 登陆telnet服务器<br> 
     *  
     * @author ChenLv <br>
     * @param telnetCommand 执行的命令行
     * @return telnetCommand 执行的命令行以及返回的结果<br>
     */ 
    public TelnetCommand login(TelnetCommand telnetCommand,TelnetClient telnet);
    
    /**
     * Description: 执行命令，返回执行结果<br> 
     *  
     * @author ChenLv<br>
     * @param telnetCommand 执行的命令行
     * @return telnetCommand 执行的命令行以及返回的结果<br>
     */ 
    public TelnetCommand sendCmd(TelnetCommand telnetCommand,TelnetClient telnet);
}
