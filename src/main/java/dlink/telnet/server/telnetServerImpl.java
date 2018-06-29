package dlink.telnet.server;

import dlink.telnet.common.TelnetCommand;
import dlink.telnet.common.TelnetCommandAndCon;
import dlink.telnet.util.readUntil;

import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;


/** 
 * <Description> 执行telnet相关操作的实现类  <br> 
 *  
 * @author ChenLv <br>
 * @version 1.0 <br>
 * @CreateDate 2018年6月22日 <br>
 * @since V1.0 <br>
 * @see dlink.telnet.server <br>
 */
public class telnetServerImpl implements TelnetServer {
	 /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(telnetServerImpl.class);
    private static final String ORIG_CODEC = "ISO8859-1";
    private static final String TRANSLATE_CODEC = "UTF-8";
    private static final char prompt = '$'; 

    @Override
    public TelnetCommandAndCon connect(String host, Integer port)  {
         InputStream telnetin;
         PrintStream telnetout;
         TelnetClient telnet = null;
    	TelnetCommandAndCon ifConnect=new TelnetCommandAndCon();
        telnet = new TelnetClient("VT100");// VT100 VT52 VT220 VTNT ANSI
        try {
            telnet.connect(host, port);
            telnetin = telnet.getInputStream();
            telnetout = new PrintStream(telnet.getOutputStream());
            String sb; 
			Thread.sleep(100);			
            sb=new readUntil().read("login:", telnet, telnetin);  
            ifConnect.setRestInfo(new String(sb.toString().getBytes("ISO8859-1"),TRANSLATE_CODEC));
        } catch (IOException e) {
        	ifConnect=new TelnetCommandAndCon();
        } catch (InterruptedException e) {
			e.printStackTrace();
		}
        ifConnect.setTelnetClient(telnet);
        return ifConnect;
    }
    @Override
    public TelnetCommand login(TelnetCommand telnetCommand,TelnetClient telnet) {
    	 InputStream telnetin=telnet.getInputStream();;
         PrintStream telnetout = new PrintStream(telnet.getOutputStream());
        try {
            String sb;
            write(telnetCommand.getCommand(),telnetout);
            write("\n",telnetout);     
            sb=new readUntil().read("Password:", telnet, telnetin); 
            Thread.sleep(1000);	
            if(telnetCommand.getRestInfo()==null) {
            	telnetCommand.setRestInfo(new String(sb.toString().getBytes(ORIG_CODEC),TRANSLATE_CODEC));
            }else {
            	telnetCommand.setRestInfo(telnetCommand.getRestInfo()+"\n"+new String(sb.toString().getBytes(ORIG_CODEC),TRANSLATE_CODEC));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        LOG.info(telnetCommand.getCommand());
        return telnetCommand;
    }
    @Override
    public TelnetCommand sendCmd(TelnetCommand telnetCommand,TelnetClient telnet) {
    	 InputStream telnetin=telnet.getInputStream();;
         PrintStream telnetout = new PrintStream(telnet.getOutputStream());
        try {
            String sb;        
        write(telnetCommand.getCommand(),telnetout);
        write("\n",telnetout);
        Thread.sleep(1000);	
        sb=new readUntil().read(prompt+"", telnet, telnetin);
        if(telnetCommand.getRestInfo()==null) {
        	telnetCommand.setRestInfo(new String(sb.toString().getBytes(ORIG_CODEC),TRANSLATE_CODEC));
        }else {
        	telnetCommand.setRestInfo(telnetCommand.getRestInfo()+"\n"+new String(sb.toString().getBytes(ORIG_CODEC),TRANSLATE_CODEC));
        }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        LOG.info(telnetCommand.getCommand());
        return telnetCommand;
    }

    public void write(String s,PrintStream telnetout) {
        try {
            telnetout.write(s.getBytes());
            telnetout.flush();
        } catch (Exception e) {
        }
    }


    
}
