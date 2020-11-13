/*
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.ayakaji.msg;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;

/**
 * SMPP protocol encapsulation. You can use this program to directly access the
 * SMS center to send SMS.
 */
public class SMPPWrapper {
	private static final Logger LOGGER = Logger.getLogger(SMPPWrapper.class.getName());
	private static final TimeFormatter TIME_FORMATTER = new AbsoluteTimeFormatter();
	private static final String SMSC_HOST = "10.19.90.200"; // SMSC's address
	private static final int SMSC_PORT = 5018; // SMSC's port
	private static final String sysId = "bossll"; // Connected with which system id in SMSC
	private static final String passwd = "Aug_0929"; // Password
	private static final String sysTyp = "999999"; // System Type
	private static final String svcTyp = "CMT"; // Service Type
	private static final int MAX_LEN = 127; // The maximum string length of a short message
	
	/**
	 * Split string by fixed length
	 * @param s
	 * @param l
	 * @return
	 */
	public static String[] split(String s, int l) {
		int sl = s.length();
		int n = sl / l + 1;
		String[] arr = new String[n];
		for (int i = 0; i < n; i++) {
			if (i < n - 1) arr[i] = s.substring(i * l, (i + 1) * l);
			else arr[i] = s.substring(i * l);
		}
		return arr;
	}

	/**
	 * Usage: send("10086", "The message content", "15069071152");
	 * 
	 * @param sender
	 * @param msg
	 * @param receiver
	 * @throws PDUException
	 * @throws ResponseTimeoutException
	 * @throws InvalidResponseException
	 * @throws NegativeResponseException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void deliver(String sender, String msg, String receivers)
			throws PDUException, ResponseTimeoutException, InvalidResponseException, NegativeResponseException,
			IOException, InterruptedException {
		if (sender == null || sender.equals("") || msg == null || msg.equals("") || receivers == null
				|| receivers.equals(""))
			LOGGER.severe("Incomplete parameters!");
		SMPPSession smppSess = new SMPPSession();
		String systemId = smppSess.connectAndBind(SMSC_HOST, SMSC_PORT, new BindParameter(BindType.BIND_TX, sysId,
				passwd, sysTyp, TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
		LOGGER.info("Connected with SMSC with system id: " + systemId);
		DataCoding dataCoding = new GeneralDataCoding(Alphabet.ALPHA_UCS2, MessageClass.CLASS1, false);
		String[] arrMsg = split(msg, MAX_LEN); // support message block sending
		for (String sMsg : arrMsg) {
			byte[] data = sMsg.getBytes("UTF-16BE");
			String[] arrRecv = receivers.split(","); // support message batch sending
			for (String recv : arrRecv) {
				String msgId = smppSess.submitShortMessage(svcTyp, TypeOfNumber.INTERNATIONAL,
						NumberingPlanIndicator.UNKNOWN, sender, TypeOfNumber.NATIONAL, NumberingPlanIndicator.UNKNOWN, recv,
						new ESMClass(), (byte) 0, (byte) 1, TIME_FORMATTER.format(new Date()), null,
						new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT), (byte) 0, dataCoding, (byte) 0, data);
				LOGGER.info("Message delivered, message id is " + msgId);
				Thread.sleep(3000);
			}
		}
		smppSess.unbindAndClose();
		LOGGER.info("Disconnect with SMSC.");
	}
}
