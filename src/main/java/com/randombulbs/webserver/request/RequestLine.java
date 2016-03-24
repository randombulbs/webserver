package com.randombulbs.webserver.request;

/**
 * Created by nbhatti on 3/23/16.
 */

import com.randombulbs.webserver.HttpProtocolException;

/**
 * The RequestLine class represent an HTTP request line having a valid method, target and version.
 */
public class RequestLine {
    /**
     * @param requestLine The raw line that represents the HTTP request line.
     *                    Parses according to http://tools.ietf.org/html/rfc7230#section-3.1
     * @return The URI
     * @throws HttpProtocolException If the line is not as per specification an exception is thrown.
     */
    public static RequestLine parseRequestLine(String requestLine) throws HttpProtocolException  {
        String splits[] = requestLine.split(" ");
        if (splits.length != 3) {
            throw new HttpProtocolException("HTTP message does not have proper request type");
        } else {
            //@Todo validate these fields
            String method = splits[0].toLowerCase();
            String target = splits[1];
            String version = splits[2];
            return new RequestLine(method, target, version);
        }
    }

    public final String method;
    public final String target;
    public final String version;

    private RequestLine(String method, String target, String version) {
        this.method = method;
        this.target = target;
        this.version = version;
    }
}
