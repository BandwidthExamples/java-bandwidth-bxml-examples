package com.bandwidth.sdk.examples;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;

import com.bandwidth.sdk.model.*;
import com.bandwidth.sdk.model.events.*;

/**
 * This app is an event server for the Bandwidth App Platform SDK It processes
 * events within a jetty web app using the SDK
 * 
 * 
 * 
 */
public class ChaosCorpServlet extends HttpServlet {
	public static final Logger logger = Logger
			.getLogger(com.bandwidth.sdk.examples.ChaosCorpServlet.class.getName());

	// Setup logging programmatically to make initial deployment easier. Replace
	// this with logging config
	static {
		ConsoleHandler consoleHandler = new ConsoleHandler();

		// create a single line log output
		consoleHandler.setFormatter(new SimpleFormatter() {

			@Override
			public String format(LogRecord record) {
				StringBuilder sb = new StringBuilder();

				sb.append(record.getLevel().getLocalizedName()).append(": ")
						.append(formatMessage(record)).append("\n");

				return sb.toString();
			}
		});
		// change the log level here
		consoleHandler.setLevel(Level.FINEST);
		// and here
		logger.setLevel(Level.FINEST);

		logger.addHandler(consoleHandler);
	}
	
	public static String MAIN_MENU_TAG = "main_menu";
	public static String MENU_ONE_TAG = "menu_one";
	public static String MENU_TWO_TAG = "menu_two";
	public static String MENU_THREE_TAG = "menu_three";
	public static String MENU_FOUR_TAG = "menu_four";
	public static String ONE = "1";
	public static String TWO = "2";
	public static String THREE = "3";
	public static String FOUR = "4";

	private ChaosCorpEventHandler eventHandler;

	// The number of worker threads
	private static int NUMWORKERS = 1;
	private ArrayList<Thread> workerThreads;
	
	// edit this to your phone number. 
	// //This would be replaced by a db to lookup give a from number
	// Alternatively you can set this in the environment variable
	// BANDWIDTH_APPPLATFORM_OUTGOING_NUMBER
	private String outgoingNumber; 
	

	// The concurrent blocking queue allows worker threads to respond to the
	// requests
	// without blocking or delaying new events
	private final BlockingQueue<Event> queue = new LinkedBlockingQueue<Event>();
	

	/**
	 * Initialize the servlet.
	 * 
	 * This method starts the worker threads
	 */
	@Override
	public void init() {
		logger.finer("init(ENTRY)");
		
		workerThreads = new ArrayList<Thread>();

		// start the worker threads
		for (int i = 0; i < NUMWORKERS; i++) {
			
			Thread thread = new Thread(new EventWorker(queue));
			thread.start();
			
			workerThreads.add(thread);
		}

		eventHandler = new ChaosCorpEventHandler();
				
		logger.finer("init(EXIT)");
	}
	
	/**
	 * Clean up the threads
	 */
	public void destroy() {
		
		// clean up nicely
		for (Thread thread : workerThreads) {
			thread.interrupted();
		}
	}

	/**
	 * This method is the main event handler when events are posted. It creates
	 * an event object from the body of the post and puts it on the queue for
	 * the workers to process
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		logger.finer("doPost(ENTRY)");

		// displayHeaders(req);
		// displayParameters(req);

		try {
			String body = getBody(req);
			logger.finest(body);
			Event event = (Event) EventBase.createEventFromString(body);

			String requestUrl = req.getRequestURL().toString();
			String requestUri = req.getRequestURI();
			String contextPath = req.getContextPath();
			String baseUrl = requestUrl.substring(0, requestUrl.length()
				- requestUri.length()) + contextPath;

			logger.finer("requestUrl:" + requestUrl);
			logger.finer("requestUri:" + requestUri);
			logger.finer("contextPath:" + contextPath);
			logger.finer("baseUrl:" + baseUrl);

			String fromNumber = req.getParameter("fromNumber");
			event.setProperty("fromNumber", fromNumber);
			event.setProperty("baseUrl", baseUrl);

			logger.fine("adding event to queue");
			queue.add(event);

			resp.setStatus(HttpServletResponse.SC_OK);

		} catch (Exception e) {
			logger.severe(e.toString());
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();

		}

		logger.finer("doPost(EXIT)");
	}

	/**
     * 
     */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.finer("doGet(ENTRY)");
		
		String requestUrl = req.getRequestURL().toString();
		String requestUri = req.getRequestURI();
		String contextPath = req.getContextPath();

		logger.finer("requestUrl:" + requestUrl);
		logger.finer("requestUri:" + requestUri);
		logger.finer("contextPath:" + contextPath);
		
		String baseUrl = requestUrl.substring(0, requestUrl.length()
				- requestUri.length());
		
		logger.finer("baseUrl:" + baseUrl);


		logger.finer("doGet(EXIT)");
	}

	/**
	 * Displays the request headers
	 * 
	 * @param req
	 */
	protected void displayHeaders(HttpServletRequest req) {
		logger.finest("displayHeaders(ENTRY)");

		Enumeration names = req.getHeaderNames();

		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			StringBuffer buf = new StringBuffer(name + ":");

			Enumeration headers = req.getHeaders(name);

			while (headers.hasMoreElements()) {
				String header = (String) headers.nextElement();

				buf.append(header + ",");
			}
			logger.finest(buf.toString());
		}

		logger.finest("displayHeaders(EXIT)");
	}

	/**
	 * Displays the parameters from the request
	 * 
	 * @param req
	 */
	protected void displayParameters(HttpServletRequest req) {
		logger.finest("displayParameters(ENTRY)");

		Enumeration keys = req.getParameterNames();

		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();

			// To retrieve a single value
			String value = req.getParameter(key);
			logger.finer(key + ":" + value);

			// If the same key has multiple values (check boxes)
			String[] valueArray = req.getParameterValues(key);

			for (int i = 0; i > valueArray.length; i++) {
				logger.finest("VALUE ARRAY" + valueArray[i]);
			}

		}

		logger.finest("displayParameters(EXIT)");
	}

	/**
	 * Extracts the JSON body from the request
	 * 
	 * @param req
	 * @return
	 */
	protected String getBody(HttpServletRequest req) {
		logger.finest("getBody(ENTRY)");

		StringBuilder sb = new StringBuilder();
		try {
			InputStream in = req.getInputStream();

			InputStreamReader is = new InputStreamReader(in);

			BufferedReader br = new BufferedReader(is);
			String read = br.readLine();

			while (read != null) {

				// System.out.println(read);
				sb.append(read);
				read = br.readLine();
			}
		} catch (Exception e) {
			logger.severe(e.toString());
			e.printStackTrace();
		}

		logger.finest("getBody(EXIT)");
		return sb.toString();
	}

	/**
	 * Worker thread
	 * 
	 * @author smitchell
	 * 
	 */
	public class EventWorker implements Runnable {
		protected BlockingQueue<Event> queue = null;

		public EventWorker(BlockingQueue<Event> queue) {
			this.queue = queue;
		}

		/**
		 * Processes each event
		 */
		public void run() {
			logger.finer("run(ENTRY)");
			while (true) {
				try {
					logger.fine("taking event from queue");
					Event event = (Event) queue.take();

					EventType eventType = event.getEventType();
					logger.finer("got event from queue, eventType:"
							+ eventType.toString());

					event.execute(eventHandler);

				} // try
				catch (InterruptedException e) {
					logger.severe(e.getMessage());
					e.printStackTrace();
					Thread.currentThread().interrupt();
					break;
				}

			} // while

		} // run

	} // end EventConsumer

	/**
	 * Events Dispatched here
	 * 
	 * @author smitchell
	 * 
	 */
	public class ChaosCorpEventHandler implements Visitor {
		
		/**
		 * Handles the incoming call event
		 * 
		 * This is necessary for when auto-answer is off. Otherwise process the
		 * new call in the answer event
		 * 
		 */
		public void processEvent(IncomingCallEvent event) {
			logger.finer("processIncomingCallEvent(ENTRY)");

			// Processes event when autoanswer is off

			logger.finer("processIncomingCallEvent(EXIT)");
		}

		/**
		 * Handles answer event. 
		 * 
		 */
		public void processEvent(AnswerEvent event) {
			logger.finer("processAnswerEvent(ENTRY)");

			try {
				logger.finer("creating call");
				
				String callId = event.getProperty("callId");
				Call call = Call.get(callId);

				//HashMap <String, Object>gatherParams = new HashMap<String, Object>();
				//HashMap <String, Object>promptParams = new HashMap<String, Object>();
				
				// fix this, no external json!!!
				JSONObject gatherParams = new JSONObject();
				JSONObject promptParams = new JSONObject();

				
				gatherParams.put("maxDigits", "1"); //
				gatherParams.put("interDigitTimeout", "8"); //
				gatherParams.put("tag", MAIN_MENU_TAG); //

				promptParams.put("sentence", "Press 1 for a song, press 2 to record, press 3 to make a call, press 4 to send a text"); //
				promptParams.put("gender", "female"); //
				promptParams.put("voice", "kate");
				promptParams.put("locale", "en_US"); //
				//gatherParams.put("fileUrl", ""); //
				promptParams.put("bargeable", "true"); //
				
				logger.finer("create gather, play top menu 1");
				call.createGather(gatherParams, promptParams);
				
			} catch (Exception e) {
				logger.severe(e.toString());
				e.printStackTrace();
			}
			 
			logger.finer("processAnswerEvent(EXIT)");
		}

		/**
        * 
       */

		public void processEvent(GatherEvent event) {
			logger.finer("processGatherEvent(ENTRY)");

			String tag = event.getProperty("tag");
			logger.finer("gather tag:" + tag);
			
			
			if (MAIN_MENU_TAG.equalsIgnoreCase(tag)) {
				onMainMenu(event);
			}
			else if (MENU_ONE_TAG.equalsIgnoreCase(tag)) {
				onMenuOne(event);
			}
			else if (MENU_TWO_TAG.equalsIgnoreCase(tag)) {
				onMenuTwo(event);
			}
			else if (MENU_THREE_TAG.equalsIgnoreCase(tag)) {
				onMenuThree(event);
			}
			else if (MENU_FOUR_TAG.equalsIgnoreCase(tag)) {
				onMenuFour(event);
			}
			
			

			logger.finer("processGatherEvent(EXIT)");
		}
		
		public void onMainMenu(GatherEvent event) {
			logger.finer("onMainMenu(ENTRY)");
			
			try {
				
				String digits = event.getProperty("digits");
				String callId = event.getProperty("callId");
				String reason = event.getProperty("reason");
				logger.finer("digits:" + digits);
				logger.finer("callId:" + callId);

				Call call = Call.get(callId);

				if ("inter-digit-timeout".equalsIgnoreCase(reason)) {

					// todo create gather again
					call.createGather("I'm sorry, I didn't get your response.");
				} else {
					if (ONE.equals(digits)) {
						// play recording
						//call.playAudio();
						
					} 
					else if (TWO.equals(digits)) {
						// create recording
					}
					else if (THREE.equalsIgnoreCase(digits)) {
						// bridge call
					}
					else if (FOUR.equalsIgnoreCase(digits)) {
						// send text
					}
						
				}
			} catch (Exception e) {
				logger.severe(e.toString());
				e.printStackTrace();
			}
			

			logger.finer("onMainMenu(EXIT)");
			
		}
		
		public void onMenuOne(GatherEvent e) {
			logger.finer("onMenuOne(ENTRY)");

			logger.finer("onMenuOne(EXIT)");
			
		}
		
		public void onMenuTwo(GatherEvent e) {
			logger.finer("onMenuTwo(ENTRY)");

			logger.finer("onMenuTwo(EXIT)");
			
		}
		
		public void onMenuThree(GatherEvent e) {
			logger.finer("onMenuThree(ENTRY)");

			logger.finer("onMenuThree(EXIT)");
			
		}
		
		public void onMenuFour(GatherEvent e) {
			logger.finer("onMenuFour(ENTRY)");

			logger.finer("onMenuFour(EXIT)");
			
		}

		/**
		 * Handles speak event
		 */
		public void processEvent(SpeakEvent event) {
			logger.finer("processSpeakEvent(ENTRY)");

			logger.finer("processSpeakEvent(EXIT)");
		}

		public void processEvent(HangupEvent event) {
			logger.finer("processHangupEvent(ENTRY)");

			logger.finer("processHangupEvent(EXIT)");
		}

		public void processEvent(RejectEvent event) {
			logger.finer("processRejectEvent(ENTRY)");

			logger.finer("processRejectEvent(EXIT)");
		}

		/**
		 * Handles playback event
		 */
		public void processEvent(PlaybackEvent event) {
			logger.finer("processPlaybackEvent(ENTRY)");

			logger.finer("processPlaybackEvent(EXIT)");
		}

		/**
		 * Handles both stop and start of the recording
		 */
		public void processEvent(RecordingEvent event) {
			logger.finer("processRecordingEvent(ENTRY)");

			logger.finer("processRecordingEvent(EXIT)");
		}

		/**
		 * Handles dtmf events
		 */
		public void processEvent(DtmfEvent event) {
			logger.finer("processDtmfEvent(ENTRY)");

			logger.finer("processDtmfEvent(EXIT)");
		}

		/**
		 * Handles default events
		 */
		public void processEvent(Event event) {
			logger.finer("processEvent(ENTRY)");

			logger.finer("processEvent(EXIT)");

		}

		/**
		 * Handles sms events
		 */
		public void processEvent(SmsEvent event) {
			logger.finer("processSmsEvent(ENTRY)");

			logger.finer("processSmsEvent(EXIT)");

		}

		public void processEvent(TimeoutEvent event) {
			logger.finer("processTimeoutEvent(ENTRY)");

			logger.finer("processTimeoutEvent(EXIT)");

		}

	}

}
