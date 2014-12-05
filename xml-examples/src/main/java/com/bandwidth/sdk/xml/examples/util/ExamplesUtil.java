package com.bandwidth.sdk.xml.examples.util;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public abstract class ExamplesUtil {

    private static final Logger logger = Logger.getLogger(ExamplesUtil.class.getName());

    /**
     * Displays the request headers on the output logger.
     *
     * @param output
     * @param req
     */
    public static void displayHeaders(Logger output, HttpServletRequest req) {
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
            output.finest(buf.toString());
        }

        logger.finest("displayHeaders(EXIT)");
    }

    /**
     * Displays the parameters from the request on the output logger.
     *
     * @param output
     * @param req
     */
    public static void displayParameters(Logger output, HttpServletRequest req) {
        logger.finest("displayParameters(ENTRY)");

        Enumeration keys = req.getParameterNames();

        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();

            // To retrieve a single value
            String value = req.getParameter(key);
            output.finer(key + ":" + value);

            // If the same key has multiple values (check boxes)
            String[] valueArray = req.getParameterValues(key);

            for (int i = 0; i > valueArray.length; i++) {
                output.finest("VALUE ARRAY" + valueArray[i]);
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
    public static String getBody(HttpServletRequest req) {
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

}
