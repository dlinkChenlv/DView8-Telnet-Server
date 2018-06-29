package dlink.telnet.common;

import java.util.UUID;



public class TelnetCommand {
    /**
     *  任务ID
     */
    private UUID taskId;
    private String command;
    private String restInfo;
    private String host;
    private int port;
    
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public UUID getTaskId() {
		return taskId;
	}
	public void setTaskId(UUID taskId) {
		this.taskId = taskId;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getRestInfo() {
		return restInfo;
	}
	public void setRestInfo(String restInfo) {
		this.restInfo = restInfo;
	}
    
}
