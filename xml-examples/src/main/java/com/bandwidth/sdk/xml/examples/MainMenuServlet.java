package com.bandwidth.sdk.xml.examples;

import com.bandwidth.sdk.exception.XMLInvalidAttributeException;
import com.bandwidth.sdk.exception.XMLInvalidTagContentException;
import com.bandwidth.sdk.exception.XMLMarshallingException;
import com.bandwidth.sdk.xml.Response;
import com.bandwidth.sdk.xml.elements.PlayAudio;
import com.bandwidth.sdk.xml.elements.Redirect;
import com.bandwidth.sdk.xml.elements.SpeakSentence;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainMenuServlet extends HttpServlet {
    public static final Logger logger = Logger
            .getLogger(MainMenuServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        logger.info("get request /mainmenu");

        try {
            Response response = new Response();
            SpeakSentence speakSentence =
                    new SpeakSentence("Redirecting your call, please wait.", "paul", "male", "en");

            PlayAudio playAudio = new PlayAudio("http://www.mediacollege.com/audio/tone/files/100Hz_44100Hz_16bit_05sec.mp3");
            Redirect redirect = new Redirect("/transfer", 10000);

            response.add(speakSentence);
            response.add(playAudio);
            response.add(redirect);

            resp.setContentType("application/xml");
            resp.getWriter().print(response.toXml());
        } catch (XMLInvalidAttributeException | XMLInvalidTagContentException e) {
            logger.log(Level.SEVERE, "invalid attribute or value", e);
        } catch (XMLMarshallingException e) {
            logger.log(Level.SEVERE, "invalid xml", e);
        }

    }
}
