package com.bandwidth.sdk.xml.examples;

import com.bandwidth.sdk.exception.XMLInvalidAttributeException;
import com.bandwidth.sdk.exception.XMLMarshallingException;
import com.bandwidth.sdk.model.events.Event;
import com.bandwidth.sdk.model.events.EventBase;
import com.bandwidth.sdk.xml.Response;
import com.bandwidth.sdk.xml.elements.Hangup;
import com.bandwidth.sdk.xml.elements.SendMessage;
import com.bandwidth.sdk.xml.elements.SpeakSentence;
import com.bandwidth.sdk.xml.examples.util.ExamplesUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * This BaML app is a callback event server for the Bandwidth App Platform SDK.
 * It processes events within a jetty web app using the BaML SDK.
 * <p/>
 * The CallMeBack BaML App answer your calls while you're away or busy and
 * send you a message with details of calls you weren't able to pick up.
 */
public class CallMeBackServlet extends HttpServlet {
    public static final Logger logger = Logger
            .getLogger(CallMeBackServlet.class.getName());

    private static final Pattern PATTERN_FROM = Pattern.compile("\"from\":\"([+0-9])\"");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");

    // This would be replaced by a db to lookup give a from number
    // Alternatively you can set this in the environment variable
    // BANDWIDTH_APPPLATFORM_OUTGOING_NUMBER
    private static final String outgoingNumber = System.getenv("BANDWIDTH_OUTGOING_NUMBER");

    private static final String messageRecipientNumber = System.getenv("BANDWIDTH_MESSAGE_RECIPIENT_NUMBER");

    private final Map<String, RouteHandler> getRoutes = new HashMap<>();
    private final Map<String, RouteHandler> postRoutes = new HashMap<>();

    @Override
    public void init() throws ServletException {
        getRoutes.put("/", new RootRouteHandler());
        getRoutes.put("/status", new StatusRouteHandler());
        postRoutes.put("/message", new MessageCallbackRouteHandler());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ExamplesUtil.displayParameters(logger, req);

        String path = getRoute(req);
        logger.fine(String.format("Routing path: [%s]", path));

        RouteHandler handler = postRoutes.get(path);
        if(handler == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Cannot do GET on " + path);
        } else {
            handler.process(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ExamplesUtil.displayParameters(logger, req);

        String path = getRoute(req);
        logger.fine(String.format("Routing path: [%s]", path));

        RouteHandler handler = getRoutes.get(path);
        if(handler == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Cannot do GET on " + path);
        } else {
            handler.process(req, resp);
        }
    }

    private static String getRoute(HttpServletRequest req) {
        return req.getRequestURI().replace(req.getServletPath(), "");
    }

    private static class RootRouteHandler implements RouteHandler {
        @Override
        public void process(HttpServletRequest req, HttpServletResponse resp) {
            Map<String, String> paramsMap = req.getParameterMap();

            logger.finest(paramsMap.toString());
            try {
                Response response = new Response();

                String from = paramsMap.get("from");
                String message = new StringBuilder("Someone called from ").append(from).append(" at ")
                        .append(DATE_FORMAT.format(new Date())).append(".").toString();

                SpeakSentence absentSpeech = new SpeakSentence("I'm currently on vacation, I'll be back soon.",
                        "paul", "male", "en");

                SendMessage sendMessage = new SendMessage(outgoingNumber, messageRecipientNumber, message);
                sendMessage.setStatusCallbackUrl("/callback");

                SpeakSentence thankYouSpeech = new SpeakSentence("Thank you for your call.",
                        "paul", "male", "en");

                response.add(absentSpeech);
                response.add(sendMessage);
                response.add(thankYouSpeech);
                response.add(new Hangup());

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(response.toXml());

            } catch (XMLInvalidAttributeException | XMLMarshallingException | IOException e) {
                logger.severe(e.toString());
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        }
    }

    private static class MessageCallbackRouteHandler implements RouteHandler {
        @Override
        public void process(HttpServletRequest req, HttpServletResponse resp) {
            String body = ExamplesUtil.getBody(req);

            logger.finest(body);
            try {
                Event event = (Event) EventBase.createEventFromString(body);

            } catch (Exception e) {
                logger.severe(e.toString());
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        }
    }

    private static class StatusRouteHandler implements RouteHandler {
        @Override
        public void process(HttpServletRequest req, HttpServletResponse resp) {
        }
    }

    /**
     * Worker thread
     */
    private interface RouteHandler {
        void process(HttpServletRequest req, HttpServletResponse resp);
    }

}
