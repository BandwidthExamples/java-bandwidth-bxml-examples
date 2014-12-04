package com.bandwidth.sdk.xml.examples;

import com.bandwidth.sdk.exception.XMLInvalidAttributeException;
import com.bandwidth.sdk.exception.XMLMarshallingException;
import com.bandwidth.sdk.xml.Response;
import com.bandwidth.sdk.xml.elements.Gather;
import com.bandwidth.sdk.xml.elements.Hangup;
import com.bandwidth.sdk.xml.elements.SpeakSentence;
import com.bandwidth.sdk.xml.elements.Transfer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GatherServlet extends HttpServlet {
    public static final Logger logger = Logger
            .getLogger(GatherServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        logger.info("get request /gather");

        try {
            Response response = new Response();

            SpeakSentence speakSentence = new SpeakSentence("Hi, we will handle your call.", "paul", "male", "en_US");

            SpeakSentence speakSentenceWithinGather =
                    new SpeakSentence("Please press a number.", "paul", "male", "en_US");
            Gather gather = new Gather("/gatherComplete", 1000, "#", 1, 5000, "true", speakSentenceWithinGather);

            // The verbs after gather will only be handled if no BaML is returned on the gather requestUrl
            // or if no digits are pressed.
            Hangup hangup = new Hangup();

            response.add(speakSentence);
            response.add(gather);
            response.add(new SpeakSentence("Good bye!", "paul", "male", "en_US"));
            response.add(hangup);

            resp.setContentType("application/xml");
            resp.getWriter().print(response.toXml());
        } catch (XMLInvalidAttributeException e) {
            logger.log(Level.SEVERE, "invalid attribute or value", e);
        } catch (XMLMarshallingException e) {
            logger.log(Level.SEVERE, "invalid xml", e);
        }

    }
}
