package com.randombulbs.webserver;

import com.randombulbs.webserver.body.HttpMessageBody;
import com.randombulbs.webserver.header.HttpHeader;
import com.randombulbs.webserver.request.RequestLine;
import com.randombulbs.webserver.response.Response;
import org.apache.commons.httpclient.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import org.apache.commons.httpclient.HttpParser;

/**
 * Created by nbhatti on 3/23/16.
 */

/**
 * This class manages the connection life cycle.
 *
 * @todo extensions
 * <ul>
 *     <li>Do session management here, start storing cookies etc.
 * </ul>
 */
public class ClientConnectionHandler extends Thread {
    static Logger LOG = LoggerFactory.getLogger(ClientConnectionHandler.class);

    Socket soc = null;
    static final int SIZE = 8124;
    byte buffer[];
    int offset = 0;
    IRequestHandler requestHandler = null;

    ClientConnectionHandler(Socket s, IRequestHandler rh) {
        buffer = new byte[SIZE];
        soc = s;
        requestHandler = rh;
    }
    @Override
    public void run() {
        try {
            boolean responseSent = false;

            // This thread will keep processing requests on the socket until a request indicates that it wants the connection
            // closed or a response from the response handler expects the connection to be closed
            while (!soc.isClosed()) {
                try {
                    // 1. Get the stream and read the request type
                    InputStream is = soc.getInputStream();
                    responseSent = false;
                    byte rawLine[] = HttpParser.readRawLine(is); // @todo remove this dependency
                    if (rawLine != null) {
                        String requestLineString = new String(rawLine, HttpHeader.httpHeaderCharset);
                        RequestLine requestLine = RequestLine.parseRequestLine(requestLineString.trim());
                        // 2. read request headers
                        HttpHeader header = HttpHeader.parseHeaders(is);
                        boolean keepAlive = keepAlive(requestLine, header.map());
                        LOG.info("Message received from " + soc.getInetAddress());
                        LOG.info("Request headers " + header.map());
                        if (this.requestHandler == null) {
                            soc.getOutputStream().write(new String("200: Success - The action was successfully received\r\n").getBytes());
                            soc.getOutputStream().flush();
                        } else {
                            //@todo considerable work needed here
                            //                        String filePath = null;
                            //                        if (requestLine.method.contentEquals("post") || requestLine.method.contentEquals("put")) {
                            //                            filePath = cacheDataToFile(is);
                            //                        }
                            Response response = requestHandler.handleRequest(
                                    requestLine.target,
                                    requestLine.method,
                                    header.map(),
                                    new HttpMessageBody(is)
                            );
                            if (keepAlive(response.header.map())) {
                                soc.close();
                            }
                            // @todo implement response handling and remove this
                            sendInternalError(soc.getOutputStream(), "Http Request has no valid request line");
                        }
                        if (!keepAlive) soc.close();
                        responseSent = true;  // no exception occured response was successfully sent
                    } else {
                        // bad request line
                        sendBadRequest(soc.getOutputStream(), "Http Request has no valid request line");
                    }
                } catch (HttpException parseException) {
                    parseException.printStackTrace();
                    sendBadRequest(soc.getOutputStream(), "Http Request has no valid request line");
                } catch (HttpProtocolException badProto) {
                    badProto.printStackTrace();
                    sendBadRequest(soc.getOutputStream(), "Http Request has no valid request line");
                } catch (IOException io) {
                    io.printStackTrace();
                } finally {
                    try {
                        if (!responseSent && !soc.isClosed()) {
                            soc.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOG.error("Exception while closing connection for HTTP request");
                    }
                }
            }
        } catch (IOException io) {
            io.printStackTrace();
            LOG.error("IOException while processing connection for HTTP request");
        }
    }

    // Determine if connect is to be kept alive or not
    boolean keepAlive(RequestLine req, Map<String, String> headerMap) {
        boolean keepAlive = req.version.toLowerCase().contentEquals("http1.1")
                || req.version.toLowerCase().contentEquals("http2.0");
        if (headerMap.containsKey("connection") && headerMap.get("connection").contentEquals("close")){
            keepAlive = false;
        } else if (headerMap.containsKey("connection") && headerMap.get("connection").contentEquals("keep-alive")){
            keepAlive = true;
        }
        return keepAlive;
    }

    // Determine if connect is to be kept alive or not
    boolean keepAlive(Map<String, String> headerMap) {
        boolean keepAlive = true;
        if (headerMap.containsKey("connection") && headerMap.get("connection").contentEquals("close")){
            keepAlive = false;
        } else if (headerMap.containsKey("connection") && headerMap.get("connection").contentEquals("keep-alive")){
            keepAlive = true;
        }
        return keepAlive;
    }

    void sendBadRequest(OutputStream out, String message) throws IOException {
        out.write(new String("400: Bad request\r\n").getBytes());
        out.flush();
    }
    void sendInternalError(OutputStream out, String message) throws IOException {
        out.write(new String("500: Internal Server Error\r\n").getBytes());
        out.flush();
    }

// @todo implement a cache of request data
//    String cacheDataToFile(InputStream is) throws IOException {
//        Path filePath = Files.createTempFile("Webserver", "dat");
//        Files.copy(is, filePath);
//        return filePath.toString();
//    }

}
