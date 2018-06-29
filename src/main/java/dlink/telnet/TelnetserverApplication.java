package dlink.telnet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import dlink.telnet.server.TelnetOperation;
import dlink.telnet.server.TelnetThread;

@SpringBootApplication
public class TelnetserverApplication implements ApplicationRunner {
	private static final Logger LOG = LoggerFactory.getLogger(TelnetserverApplication.class);
	@Override
	public void run(ApplicationArguments var1) throws Exception{
		LOG.info("ssh server is running now ++++++++++++++++++++++++++++++++++++++++");
		TelnetThread ts=new TelnetThread();
        ts.start();
	}
	public static void main(String[] args) {
		SpringApplication.run(TelnetserverApplication.class, args);
	}

}
