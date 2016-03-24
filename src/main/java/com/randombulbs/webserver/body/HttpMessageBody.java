package com.randombulbs.webserver.body;

/**
 * Created by nbhatti on 3/23/16.
 */

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class models an HTTP message body.
 * @Todo Need better modeling. Take into consideration the lifecycle of a multipart message and design this better.
 */
public class HttpMessageBody {
    /**
     * Create a message body from input stream
     * @param is
     */
    public HttpMessageBody(InputStream is) {

    }

    /**
     * Create a message body from output stream
     * @param out
     */
    public HttpMessageBody(OutputStream out) {

    }

    /**
     * Create message body from string
     * @param bodyString
     */
    public HttpMessageBody(String bodyString) {

    }

}
