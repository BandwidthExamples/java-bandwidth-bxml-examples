package com.bandwidth.sdk.xml.examples;

import com.bandwidth.sdk.exception.XMLInvalidAttributeException;
import com.bandwidth.sdk.exception.XMLInvalidTagContentException;
import com.bandwidth.sdk.exception.XMLMarshallingException;
import com.bandwidth.sdk.xml.Response;
import com.bandwidth.sdk.xml.elements.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class tTransferServlet extends HttpServlet {
	public static final Logger logger = Logger
			.getLogger(TransferServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        logger.info("get request /transfer");

        try {
            Response response = new Response();

            SpeakSentence speakSentence = new SpeakSentence("Transferring your call, please wait.", "paul", "male", "en_US");
            Transfer transfer = new Transfer("+15302987472", "+12134711336");
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
