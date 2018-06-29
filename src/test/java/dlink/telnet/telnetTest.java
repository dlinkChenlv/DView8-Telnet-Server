package dlink.telnet;


import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;

/**
 *
 * 使用java连接telnet进行操作的注意
 1.telnet有VT100 VT52 VT220 VTNT ANSI等协议。
 我用vt100。
 2.vt100控制码(ansi控制码)过滤的问题,可以过滤，也可以在服务设置不要。
 不过滤都是一些乱码。是以\033[***一个字母结尾的格式。
 3.中文乱码的问题。
 new String(old.getBytes("ISO8859-1"),"GBK")。
 4.如何判断读取到最后了。
 一有readUntil(),二有使用线程。
 5.选择telnet的java包问题，包有很多，比如appache(commons-net-3.1.jar), ganymed(ganymed-ssh2-build210.jar),javaexpect(smart-0.1-SNAPSHOT-jar-with-dependencies.jar)
 我使用appache。javaexpect有带的vt100控制码过滤，我没有仔细研究。
 6.write要flush()才发送。
 *
 *
 * telnet操作类。使用appache的net.Telnet包，对vt100控制代码（即ansi控制码）进行简单过滤。
 *
 * @author chruan
 * @version 1.0
 */
public class telnetTest {
    Object lock = new Object();
    TelnetClient telnet = null;
    String hostname;
    int hostport = 23;
    String user;
    String password;
    private InputStream in;
    private PrintStream out;
    private static final String ORIG_CODEC = "ISO8859-1";
    private static final String TRANSLATE_CODEC = "UTF-8";

    public telnetTest(String hostname, int hostport, String user,
                      String password) throws SocketException, IOException {
        super();
        this.hostname = hostname;
        this.hostport = hostport;
        this.user = user;
        this.password = password;

        telnet = new TelnetClient("VT100");// VT100 VT52 VT220 VTNT ANSI
        telnet.connect(hostname, hostport);
        in = telnet.getInputStream();
        out = new PrintStream(telnet.getOutputStream());

        readUntil("login: ");
        write(user);
        write("\n");
        readUntil("Password: ");
        write(password);
        write("\n");
        readUntil("ls");
        write("ls");
        write("\n");

        readUntil(" ");


        //readToEnd();
    }



    private void restartTerminal() {
        try {
            readUntil(">");
            write("ls\n");
            readUntil("login: ");
            write("dd\n", 500);
            readToEnd();

            write("dff\n", 200);
            readToEnd();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }



    public void readToEnd() {
        ReadThread readThread = new ReadThread();
        readThread.start();
        try {
            readThread.join();
        } catch (Exception e) {
        }
        //readThread = null;
    }

    public void readUntil(String str) {
        boolean cheklogin=false;
        if(str.equals("login: ")|| str.equals("Password: ")|| str.equals("$")){
            cheklogin=true;
        }
        char last = str.charAt(str.length() - 1);
        String[] ss;
        try {
            StringBuffer sb = new StringBuffer();
            char c;
            int code = -1;
            boolean ansiControl = false;
            boolean start = true;
            while ((code = (in.read())) != -1) {
                c = (char) code;
                if (c == '\033') {//vt100控制码都是以\033开头的。
                    ansiControl = true;
                    int code2 = in.read();
                    char cc = (char) code2;
                    if (cc == '[' || cc == '(') {
                    }
                }
                if (!ansiControl) {
                    if (c == '\r') {
                        //这里是命令行中的每一句反馈
                        String olds = new String(sb.toString().getBytes("ISO8859-1"),TRANSLATE_CODEC);
                        System.out.println(olds);
                        if(cheklogin){
                            if (sb.lastIndexOf(str) != -1) {
                                break;
                            }
                        }

                        sb.delete(0, sb.length());
                    } else if (c == '\n')
                        ;
                    else
                        sb.append(c);
                    if(cheklogin){
                        if (sb.lastIndexOf(str) != -1) {
                            break;
                        }
                    }
                    if (sb.lastIndexOf("$") != -1) {
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
            System.out.println(new String(sb.toString().getBytes("ISO8859-1"),"GBK"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(String s) {
        try {
            out.write(s.getBytes());
            out.flush();
            System.out.println(s);
        } catch (Exception e) {
        }
    }

    public void write(String s, int sleep) {
        write(s);
        try {
            Thread.sleep(sleep);
        } catch (Exception e) {
        }
    }


    /**
     * 完成之后必须关闭
     */
    public void close() {
        if (out != null)
            out.close();
        if (in != null)
            try {
                in.close();
            } catch (IOException e1) {
            }
        if (telnet != null)
            try {
                telnet.disconnect();
            } catch (IOException e) {
            }
    }

    public void doJob() {
        restartTerminal();
       counter();
    }
    private void counter() {
        //演示在一台机器上远程登录另一台计算机
        try {
            readUntil("bash-2.05b$ ");
            write("ssh loc@192.168.0.1\n");
            readUntil("loc@192.168.0.1's password: ");
            write("nodee\n");
            readUntil("[nodee@dz-05 ~]$ ");
            write("ll\n");
            readToEnd();



        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }


    public static void main(String[] args) {
        String hostname = "172.18.192.201";
        int hostport = 23;
        String user = "admin";
        String password = "admin";
        telnetTest helper = null;
        try {
            helper = new telnetTest(hostname, hostport, user, password);
            helper.doJob();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (helper != null)
                helper.close();
        }
    }

    /**
     * 读取主线程，负责管理子线程。防止读取时不动了，这时就抛弃读取子线程
     *
     */
    class ReadThread extends Thread {
        public void run() {
            synchronized (lock) {//只能一个读取
                SubReadThread sub = new SubReadThread();
                sub.start();
                int last = sub.count;
                while (true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                    if (last == sub.count) {
                        sub.stop();
                        break;
                    } else {
                        last = sub.count;
                    }
                }
                String s = sub.sb.toString();
                try {
                    System.out.println(new String(s.getBytes(ORIG_CODEC),
                            TRANSLATE_CODEC));
                } catch (UnsupportedEncodingException e) {
                    System.out.println(s);
                }
                sub = null;
            }

//          System.out.println("===========ReadThread end=============");
        }
    }

    /**
     * 读取子线程，完成实际读取
     *
     */
    class SubReadThread extends Thread {
        int count = 0;
        StringBuffer sb = new StringBuffer();

        public void read() {
            try {
                char c;
                int code = -1;
                boolean ansiControl = false;
                boolean start = true;
                while ((code = (in.read())) != -1) {
                    count++;
                    c = (char) code;
                    if (c == '\033') {
                        ansiControl = true;
                        int code2 = in.read();
                        char cc = (char) code2;
                        count++;
                        if (cc == '[' || cc == '(') {
                        }
                    }
                    if (!ansiControl) {
                        if (c == '\r') {
                            String olds = new String(sb.toString().getBytes(
                                    ORIG_CODEC), TRANSLATE_CODEC);
                            System.out.println(olds);
                            sb.delete(0, sb.length());
                        } else if (c == '\n')
                            ;
                        else
                            sb.append(c);
                    }

                    if (ansiControl) {
                        if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z')
                                || c == '"') {
                            ansiControl = false;
                        }
                    }
                }
            } catch (Exception e) {
            }
        }

        public void run() {
            read();
        }
    }
}