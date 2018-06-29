package dlink.telnet.common;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;



public class TelnetResultMap {
	static HashMap<UUID, String> TelnetResultMaps = new HashMap<>();
	private static class SingletonHolder {
		/**
		 * 静态初始化器，由JVM来保证线程安全
		 */
		private static TelnetResultMap queue = new TelnetResultMap();
	}

	// 单例队列
	public static TelnetResultMap getTelnetTaskMap() {
		return SingletonHolder.queue;
	}
	public void removeResult(UUID taskid) {
		TelnetResultMaps.remove(taskid);
	}
	public String getResultString(UUID taskid) {
		return TelnetResultMaps.get(taskid);
	}

	public void addTelnetResultMaps(UUID taskid, String telnetCommandQueue) {
		TelnetResultMaps.put(taskid, telnetCommandQueue);
	}
}
