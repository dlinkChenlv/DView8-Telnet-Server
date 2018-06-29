package dlink.telnet.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.telnet.TelnetClient;

/** 
 * <Description> 读取telent执行结果的util <br> 
 *  
 * @author ChenLv <br>
 * @version 1.0 <br>
 * @CreateDate 2018年6月22日 <br>
 * @since V1.0 <br>
 * @see dlink.telnet.util <br>
 */
public class readUntil {
	private static final String ORIG_CODEC = "ISO8859-1";
    private static final String TRANSLATE_CODEC = "UTF-8";
    private static final char sudoPrompt = '#'; 
	public String read(String pattern,TelnetClient telnet,InputStream telnetin) {
		
		try {  
            char lastChar = pattern.charAt(pattern.length() - 1);  
            StringBuffer sb = new StringBuffer();  
            char ch = (char) telnetin.read();  
            boolean issudo = false;
            boolean ansiControl = false;
            while (true) {  
                if (ch == '\033') {//vt100控制码都是以\033开头的。
                    ansiControl = true;
                    int code2 = telnetin.read();
                    char cc = (char) code2;
                    if (cc == '[' || cc == '(') {
                    }
                }
                if (ansiControl) {
                    if (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z')
                            || ch == '"') {
                        ansiControl = false;
                    }
                }else {
                	sb.append(ch);
                }
                if (ch == lastChar) {  
                    if (sb.toString().endsWith(pattern) ) {  
                        return sb.toString();  
                    }                      
                }
                if (ch == sudoPrompt) {  
                    if (sb.toString().endsWith(sudoPrompt+"")) {  
                        return sb.toString();  
                    }                      
                }
                if (ch == ' ') {
                	String sbTmp=sb.toString();
                	if(sbTmp.indexOf("Login incorrect")!=-1) {
                		return sb.toString();  
                	}
                	if(sbTmp.indexOf("[sudo]")!=-1 ) {
                		return sb.toString(); 
                		
                	}
                }
                
                ch = (char) telnetin.read();  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null; 
    }
	
	
}
