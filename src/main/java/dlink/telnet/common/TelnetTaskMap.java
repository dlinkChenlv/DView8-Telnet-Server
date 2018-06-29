package dlink.telnet.common;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class TelnetTaskMap {
	static HashMap<UUID, BlockingQueue<TelnetCommand>> TelnetTaskMaps = new HashMap<>();

	private static class SingletonHolder {
		/**
		 * 静态初始化器，由JVM来保证线程安全
		 */
		private static TelnetTaskMap queue = new TelnetTaskMap();
	}

	// 单例队列
	public static TelnetTaskMap getTelnetTaskMap() {
		return SingletonHolder.queue;
	}

	public BlockingQueue<TelnetCommand> getTelnetCommandQueue(UUID taskid) {
		return TelnetTaskMaps.get(taskid);
	}
	
	public void addTelnetTaskMaps(UUID taskid, BlockingQueue<TelnetCommand> telnetCommandQueue) {
		TelnetTaskMaps.put(taskid, telnetCommandQueue);
	}
	public void removeTelnetTaskMaps(UUID taskid) {
		TelnetTaskMaps.remove(taskid);
	}
}
