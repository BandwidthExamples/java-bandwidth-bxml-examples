package com.bandwidth.sdk.xml.examples;

import com.bandwidth.sdk.exception.XMLInvalidAttributeException;
import com.bandwidth.sdk.exception.XMLMarshallingException;
import com.bandwidth.sdk.xml.Response;
import com.bandwidth.sdk.xml.elements.Hangup;
import com.bandwidth.sdk.xml.elements.SpeakSentence;
import com.bandwidth.sdk.xml.elements.Transfer;
import com.bandwidth.sdk.xml.examples.util.ExamplesUtil;
import com.bandwidth.sdk.xml.examples.util.PropertiesReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.bandwidth.sdk.xml.examples.util.PropertiesReader.CatapultProperties.CallIncomingNumber;
import static com.bandwidth.sdk.xml.examples.util.PropertiesReader.CatapultProperties.CallOutgoingNumber;

public class TransferServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(TransferServlet.class.getName());
    private static final PropertiesReader properties = new PropertiesReader();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        logger.info("get request /transfer");

        try {
            Response response = new Response();

            String from = properties.getCatapultProperty(CallOutgoingNumber);
            String to = properties.getCatapultProperty(CallIncomingNumber);

            SpeakSentence speakSentence = new SpeakSentence("Transferring your call, please wait.", "paul", "male", "en_US");
            Transfer transfer = new Transfer(from, to);
            SpeakSentence speakSentenceWithinTransfer =
                    new SpeakSentence("Inner speak sentence.", "paul", "male", "en_US");
            transfer.setSpeakSentence(speakSentenceWithinTransfer);
            Hangup hangup = new Hangup();

            response.add(speakSentence);
            response.add(transfer);
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
