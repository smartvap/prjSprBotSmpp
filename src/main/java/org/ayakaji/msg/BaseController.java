package org.ayakaji.msg;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@SuppressWarnings("unused")
@RestController
public class BaseController {

	private static Logger LOGGER = Logger.getLogger(BaseController.class.getName());
	private static DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	private static Pattern pattern = Pattern.compile("in environment (.*)\\n");

	/**
	 * Parse Parameter with key
	 * 
	 * @param param
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String parseParam(String param, String key) throws UnsupportedEncodingException {
		String decoded = URLDecoder.decode(param, "UTF-8");
		Pattern p = Pattern.compile("(" + key + "=)([^&]*)(&|$)");
		Matcher m = p.matcher(decoded);
		if (m.find() && m.groupCount() > 2) {
			String v = m.group(2);
			if (!v.equals("")) {
				return v;
			}
		}
		return null;
	}

	/**
	 * Convert double to percentage string
	 * 
	 * @param d
	 * @param intRsv
	 * @param decRsv
	 * @return
	 */
	private static String getPercVal(double d, int intRsv, int decRsv) {
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMaximumIntegerDigits(intRsv);
		nf.setMinimumFractionDigits(decRsv);
		return nf.format(d);
	}

	/**
	 * The maximum width of the string buffer should reserve 4 bytes as an ellipsis,
	 * that is, MAX_LEN + 4 <= the desired limit of the number of bytes. Here is set
	 * to accommodate two SMS, for each one is 127.
	 * 
	 * @author Hugh
	 *
	 */
	private static final class StringBufferEx {
		private static Logger LOGGER = Logger.getLogger(StringBufferEx.class.getName());
		private final static int MAX_SIZE = 250; // the actual maximum width is MAX_SIZE + 4
		private StringBuffer buff = new StringBuffer();
		private boolean isFull = false;

		private StringBufferEx() {
			buff.append("");
		}

		private StringBufferEx(String str) {
			buff.append(str);
		}

		private void append(String str) {
			if (isFull)
				return;
			if (isFull || buff.length() >= MAX_SIZE || buff.length() + str.length() > MAX_SIZE) {
				LOGGER.severe("String length overflow!");
				isFull = true;
				buff.append(" ...");
				return;
			}
			buff.append(str);
		}

		public String toString() {
			return buff.toString();
		}
	}

	private static String formDateTime(Long l) {
		DateTime dt = new DateTime(l);
		return fmt.print(dt);
	}

	/**
	 * This message delivery handler supports both JSON format and request parameter
	 * format. The parameters must include sender, msg and receiver, which
	 * correspond to the SMS sender, SMS content and SMS receiver respectively. The
	 * receiver contains at least one number, multiple numbers should be separated
	 * by commas to achieve batch sending.
	 * 
	 * @param param
	 * @return
	 * @throws InterruptedException
	 * @throws PDUException
	 * @throws ResponseTimeoutException
	 * @throws InvalidResponseException
	 * @throws NegativeResponseException
	 * @throws IOException
	 */
	@RequestMapping("/deliver.do")
	public String deliver(@RequestBody String param) throws InterruptedException, PDUException,
			ResponseTimeoutException, InvalidResponseException, NegativeResponseException, IOException {
		// Parse Request Body
		LOGGER.info(param);
		Map<String, String> map = parse(param);
		SMPPWrapper.deliver(map.get("sender"), map.get("msg"), map.get("receivers"));
		return "Success";
	}

	private static Map<String, String> parse(String param) {
		Map<String, String> map = new HashMap<String, String>();
		StringBufferEx msgBuf = new StringBufferEx();
		JSONObject json = JSONObject.parseObject(param);
		if (json == null) {
			LOGGER.severe("Illegal JSON Format!");
			return null;
		}
		String sender = json.getString("sender");
		if (sender == null || sender.equals("")) {
			LOGGER.severe("Illegal sender!");
			return null;
		}
		map.put("sender", sender);
		String receivers = json.getString("receivers");
		if (receivers == null || receivers.equals("")) {
			LOGGER.severe("Illegal receivers!");
			return null;
		}
		map.put("receivers", receivers);
		// Problem state: OPEN or RESOLVED
		String state = json.getString("state");
		if (state == null || state.equals("")) {
			LOGGER.severe("Illegal problem state!");
			return null;
		} else {
			msgBuf.append(state + " problem ");
		}
		// Problem ID
		String problemId = json.getString("problemId");
		if (problemId == null || problemId.equals("")) {
			LOGGER.severe("Illegal problem ID!");
			return null;
		} else {
			msgBuf.append(problemId + ":");
			if (problemId.equals("999")) {
				msgBuf.append(" this is a test message, please ignore.");
				map.put("msg", msgBuf.toString());
				return map;
			}
		}
		// Problem Title
		String problemTitle = json.getString("problemTitle");
		if (problemTitle == null || problemTitle.equals("")) {
			LOGGER.severe("Illegal problem title!");
			return null;
		}
		problemTitle = problemTitle.toLowerCase();
		// Problem Details JSON
		JSONObject problemDetails = json.getJSONObject("problemDetails");
		if (problemDetails == null) {
			LOGGER.severe("Illegal problem details!");
			return null;
		}
		// Problem Details Text
		String problemDetailsTxt = json.getString("problemDetailsText");
		if (problemDetailsTxt == null || problemDetailsTxt.equals("")) {
			LOGGER.severe("Illegal problem details text");
		} else {
			Matcher m = pattern.matcher(problemDetailsTxt);
			if (m.find() && m.groupCount() >= 1) {
				msgBuf.append(" " + m.group(1));
			}
		}
		// Ranked events
		JSONArray rankedEvents = null;
		rankedEvents = problemDetails.getJSONArray("rankedEvents");
		if (rankedEvents == null || rankedEvents.size() == 0) {
			LOGGER.severe("No ranked events!");
			map.put("msg", msgBuf.toString());
			return map;
		}
		LOGGER.info("Ranked Events Count: " + rankedEvents.size());
		for (int i = 0; i < rankedEvents.size(); i++) {
			msgBuf.append(" [" + (i + 1) + "]");
			JSONObject rankedEvent = (JSONObject) rankedEvents.get(i);
			String entityName = rankedEvent.getString("entityName");
			String eventType = rankedEvent.getString("eventType");
			Long startTime = rankedEvent.getLong("startTime");
			Long endTime = rankedEvent.getLong("endTime");
			if (entityName == null || entityName.equals("") || eventType == null || eventType.equals("")
					|| startTime == null) {
				LOGGER.severe("Illegal ranked events!");
				continue;
			}
			if (eventType.equals("FAILURE_RATE_INCREASED")) {
				eventType = eventType.toLowerCase().replace("_", " ");
				msgBuf.append(" " + entityName + "'s " + eventType);
				JSONArray severities = rankedEvent.getJSONArray("severities");
				if (severities == null || severities.size() == 0) {
					LOGGER.severe("No severities!");
					continue;
				}
				for (int j = 0; j < severities.size(); j++) {
					JSONObject severity = (JSONObject) severities.get(j);
					Double val = severity.getDouble("value");
					if (val == null) {
						LOGGER.severe("No value!");
					}
					msgBuf.append(" to " + getPercVal(val.doubleValue(), 2, 2));
				}
			} else if (eventType.equals("SLOW_DISK")) {
				eventType = eventType.toLowerCase().replace("_", " ");
				String resName = rankedEvent.getString("resourceName");
				if (resName == null || resName.equals("")) {
					LOGGER.severe("Illegal resource name!");
				}
				msgBuf.append(" " + entityName + " has " + eventType + " " + resName);
			} else if (eventType.equals("HIGH_CONNECTIVITY_FAILURES")) {
				eventType = eventType.toLowerCase().replace("_", " ");
				msgBuf.append(" " + entityName + " encountered " + eventType);
			} else {
				eventType = eventType.toLowerCase().replace("_", " ");
				msgBuf.append(" " + entityName + " " + eventType);
			}
			if (endTime == null || endTime == -1)
				msgBuf.append(" since " + formDateTime(startTime) + ".");
			else
				msgBuf.append(" from " + formDateTime(startTime) + " to " + formDateTime(endTime) + ".");
		}
		map.put("msg", msgBuf.toString());
		return map;
	}
}