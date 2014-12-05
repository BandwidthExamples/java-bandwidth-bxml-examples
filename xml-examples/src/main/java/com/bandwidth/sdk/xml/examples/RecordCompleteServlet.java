package com.bandwidth.sdk.xml.examples;

import com.bandwidth.sdk.exception.XMLInvalidAttributeException;
import com.bandwidth.sdk.exception.XMLMarshallingException;
import com.bandwidth.sdk.xml.Response;
import com.bandwidth.sdk.xml.elements.Hangup;
import com.bandwidth.sdk.xml.elements.SpeakSentence;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecordCompleteServlet extends HttpServlet {
    public static final Logger logger = Logger
            .getLogger(RecordCompleteServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        logger.info("get request /recordComplete");

        try {
            Response response = new Response();

            String recordingUri = req.getParameter("recordingUri");
            String terminatingDigits = req.getParameter("terminatingDigits");
            if (terminatingDigits != null) {
                terminatingDigits = terminatingDigits.replace("", " ").trim();
            }
            String startTime = req.getParameter("startTime");
            String endTime = req.getParameter("endTime");

            SpeakSentence speak = new SpeakSentence("You just received the following parameters from BaML", "paul", "male", "en_US");
            SpeakSentence speakSeq1 = new SpeakSentence("recordingUri: " + recordingUri, "paul", "male", "en_US");
            SpeakSentence speakSeq2 = new SpeakSentence("terminatingDigits: " + terminatingDigits, "paul", "male", "en_US");
            SpeakSentence speakSeq3 = new SpeakSentence("startTime: " + startTime, "paul", "male", "en_US");
            SpeakSentence speakSeq4 = new SpeakSentence("endTime: " + endTime, "paul", "male", "en_US");
            SpeakSentence goodbye = new SpeakSentence("Goodbye!", "paul", "male", "en_US");

            Hangup hangup = new Hangup();

            response.add(speak);
            response.add(speakSeq1);
            response.add(speakSeq2);
            response.add(speakSeq3);
            response.add(speakSeq4);
            response.add(goodbye);
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
