package com.bandwidth.sdk.xml.examples;

import com.bandwidth.sdk.exception.XMLInvalidAttributeException;
import com.bandwidth.sdk.exception.XMLMarshallingException;
import com.bandwidth.sdk.xml.Response;
import com.bandwidth.sdk.xml.elements.Gather;
import com.bandwidth.sdk.xml.elements.Hangup;
import com.bandwidth.sdk.xml.elements.Record;
import com.bandwidth.sdk.xml.elements.SpeakSentence;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecordServlet extends HttpServlet {
    public static final Logger logger = Logger
            .getLogger(RecordServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        logger.info("get request /record");

        try {
            Response response = new Response();

            SpeakSentence speak =
                    new SpeakSentence("Recording is about to start, press 1 2 3 4 to stop recording.", "paul", "male", "en_US");


            Record record = new Record();
            record.setRequestUrl("/recordComplete");
            record.setMaxDuration(60);
            record.setTerminatingDigits("1234");

            response.add(speak);
            response.add(record);

            resp.setContentType("application/xml");
            resp.getWriter().print(response.toXml());
        } catch (XMLInvalidAttributeException e) {
            logger.log(Level.SEVERE, "invalid attribute or value", e);
        } catch (XMLMarshallingException e) {
            logger.log(Level.SEVERE, "invalid xml", e);
        }

    }
}
