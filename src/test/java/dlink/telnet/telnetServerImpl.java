package dlink.telnet;

import dlink.telnet.common.TelnetCommand;
import dlink.telnet.server.TelnetServer;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Created by 91680 on 2018.6.15.
 */
public class telnetServerImpl  {
    private static final String ORIG_CODEC = "ISO8859-1";
    private static final String TRANSLATE_CODEC = "UTF-8";
    private InputStream telnetin;
    private PrintStream telnetout;
    private TelnetClient telnet = null;

    public boolean connect(String host, Integer port)  {
        boolean ifConnect=false;
        telnet = new TelnetClient("VT100");// VT100 VT52 VT220 VTNT ANSI
        try {
            telnet.connect(host, port);
            telnetin = telnet.getInputStream();
            telnetout = new PrintStream(telnet.getOutputStream());
            StringBuffer sb;
            sb=readUntil("login: ");
            System.out.println(new String(sb.toString().getBytes("ISO8859-1"),TRANSLATE_CODEC)+"!!!!!");
            write("admin");
            write("\n");
            sb=readUntil("Password: ");
            System.out.println(new String(sb.toString().getBytes("ISO8859-1"),TRANSLATE_CODEC)+"!!!!!");
            write("admin");
            write("\n");
            readUntil(" ");

            ifConnect=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ifConnect;
    }


    public TelnetCommand sendCmd(String command, int delay) {
        try {
        StringBuffer sb;
        write(" ls");
        write("\n");
        sb=readUntil("ls");
        System.out.println(new String(sb.toString().getBytes("ISO8859-1"),TRANSLATE_CODEC)+"!!!!!");
        write("mkdir vi");
        write("\n");
        sb=readUntil(" ");
            System.out.println(new String(sb.toString().getBytes("ISO8859-1"),TRANSLATE_CODEC)+"!!!!!");
        write("cd vi");
        write("\n");
        sb=readUntil(" ");
            System.out.println(new String(sb.toString().getBytes("ISO8859-1"),TRANSLATE_CODEC)+"!!!!!");
        write("ls");
        write("\n");
        sb=readUntil(" ");
            System.out.println(new String(sb.toString().getBytes("ISO8859-1"),TRANSLATE_CODEC)+"!!!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write(String s) {
        try {
            telnetout.write(s.getBytes());
            telnetout.flush();
            System.out.println(s);
        } catch (Exception e) {
        }
    }


    public StringBuffer readUntil(String str) {
        boolean cheklogin=false;
        if(str.equals("login: ")|| str.equals("Password: ")){
            cheklogin=true;
        }
        char last = str.charAt(str.length() - 1);
        String[] ss;
        StringBuffer sb = new StringBuffer();
        try {

            char c;
            int code = -1;
            boolean ansiControl = false;
            boolean start = true;
            while ((code = (telnetin.read())) != -1) {
                c = (char) code;
                if (c == '\033') {//vt100控制码都是以\033开头的。
                    ansiControl = true;
                    int code2 = telnetin.read();
                    char cc = (char) code2;
                    if (cc == '[' || cc == '(') {
                    }
                }
                if (!ansiControl) {
                    if (c == '\r') {
                        //这里是命令行中的每一句反馈
                        String olds = new String(sb.toString().getBytes("ISO8859-1"),TRANSLATE_CODEC);
                        if(olds.equals(str)){
                            System.out.println(olds);
                        }else{
                            System.out.println(olds+"  ????");
                        }

                        if(cheklogin){
                            if (sb.lastIndexOf(str) != -1) {
                                break;
                            }
                        }
                        //sb.append('+');
                        sb.delete(0, sb.length());
                    } else if (c == '\n'){
                    } else{
                        sb.append(c);
                    }
                    if(cheklogin && sb.lastIndexOf(str) != -1){
                            break;
                    }
                    if (sb.lastIndexOf("$") != -1 || sb.lastIndexOf("#")!=-1) {
                        break;
                    }
                }

                if (ansiControl) {
                    if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z')
                            || c == '"') {
                        ansiControl = false;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }
}
