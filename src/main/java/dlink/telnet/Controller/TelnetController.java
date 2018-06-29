package dlink.telnet.Controller;

import dlink.telnet.common.TelnetCommand;
import dlink.telnet.common.TelnetResultMap;
import dlink.telnet.common.TelnetTaskMap;
import dlink.telnet.common.TelnetTaskQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by 91680 on 2018.6.12.
 *
 * SSH服务接口
 */
@RestController
public class TelnetController {
	private static final int waitFrequency = 300;// 等待时间
	/**
	 * 日志
	 */
	private static final Logger LOG = LoggerFactory.getLogger(TelnetController.class);

	@RequestMapping(value = "/TelnetCommand", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> sendTelnetCommand(@RequestBody TelnetCommand input) {
		int tice = 0;
		Map<String, String> map = new HashMap<String, String>();
		try {
			TelnetTaskQueue.getTelnetCommandQueue().produce(input);
			// 等待结果，等待30秒，
			while (TelnetResultMap.getTelnetTaskMap().getResultString(input.getTaskId()) == null) {
				Thread.sleep(100);
				tice++;
				if (tice >= waitFrequency) {
					break;
				}
			}

			map.put("res", TelnetResultMap.getTelnetTaskMap().getResultString(input.getTaskId()));
			TelnetResultMap.getTelnetTaskMap().removeResult(input.getTaskId());
		} catch (InterruptedException e) {
			map.put("code", "200");
			e.printStackTrace();
		}
		if (TelnetResultMap.getTelnetTaskMap().getResultString(input.getTaskId()) == null) {
			map.put("code", "100");
		} else {
			map.put("code", "200");
		}

		return map;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(HttpServletResponse response, HttpServletRequest request) throws MalformedURLException {
		return new ModelAndView("index");

	}
}