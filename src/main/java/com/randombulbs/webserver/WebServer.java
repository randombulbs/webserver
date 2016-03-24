package com.randombulbs.webserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.Config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by nbhatti on 3/23/16.
 */
/**
 * This is a simple configurable http server. There are following major areas of functionality namely
 * <ul>
 *     <li>Providing robust implementation of HTTP protocol</li>
 *     <li>Performance considerations</li>
 *     <li>An interface for handling requests with rick features like session handling</li>
 * </ul>
 * The current implementation is very basic with a very limited handling of http protocol.
 * See <a href="https://www.w3.org/Protocols/rfc2616/rfc2616.html">Protocol</a>
 * Only basic request line and header parsing has been implemented.
 * @todo to qualify as a rudimentary http server need to complete body parsing including multipart.
 * @todo the ClientConnectionHandler class should be made more pluggable by providing functionality for Session management.
 * @todo ClientConnectionHandler tries to implement the keep-alive feature but more through testing is required.
 * @todo IRequestHandler interface needs to be improved to ensure all http protocol features can be
 * <ul>
 *     <li>handled</li>
 *     <li>handled effciently</li>
 *     <li>through a richer interface</li>
 * </ul>
 */
public class WebServer extends Thread {
    static Logger log = LoggerFactory.getLogger(WebServer.class);

    /**
     * The main function here demonstrate the use of WebServer by using a request handler that simply returns 200 status
     * @param args
     */
    public static void main(String args[]) {
        // 1. Read configs
        Config conf = ConfigFactory.load();
        int port = conf.getInt("port");
        int poolSize = conf.getInt("pool");
        int timeout = conf.getInt("timeout");

        WebServer ws = null;
        // 2. Start the server
        try {
            log.info("Starting server at port {}", port);
            ws = new WebServer(port, poolSize, timeout);
            // @todo This does not work.
            //ws.setRequestHandler(new StatusOkRequestHandler());
            ws.startServer();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("The server could not be started");
        }
    }

    /**
     * The webserver can be configured with following parameters
     * @param port     : The port at which the server should be started
     * @param poolSize : The number of thread to use for fixed size pool
     * @param requestTimeout : A global request timeout
     */
    WebServer(int port, int poolSize, int requestTimeout) {
        this.port = port;
        this.poolSize = poolSize;
        timeout = requestTimeout;
    }

    ServerSocket serverSocket;
    ExecutorService executors;
    int port;
    int poolSize;
    int timeout;

    protected boolean alive = true;
    protected IRequestHandler requestHandler = null;

    void startServer() throws IOException {
        // 1. Initialize resources
        ExecutorService ex = Executors.newFixedThreadPool(poolSize);

        // 2. Start the server
        Thread serverMainThread = new Thread(this);
        serverMainThread.start();

        // 3. Clean up on exit
        Runtime.getRuntime().addShutdownHook(new Cleanup(this));

        try {
            serverMainThread.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // 1. Create the Server Socket
            serverSocket = new ServerSocket(port);
            // 2. The thread pool for serving connections
            executors = Executors.newFixedThreadPool(poolSize);
            // 3. Listen for new connection and handle them using ClientConnectionHandler
            while (alive) {
                Socket conn = serverSocket.accept();
                if (timeout > 0) {
                    conn.setSoTimeout(timeout);
                }
                executors.execute(new ClientConnectionHandler(conn, requestHandler));
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void shutdown() {
        alive = false;
        if (null != executors) {
            executors.shutdown();
        }
    }

    static class Cleanup extends Thread {
        WebServer ws;
        Cleanup(WebServer ws) {
            this.ws = ws;
        }
        @Override
        public void run() {
            if (null != ws) {
                ws.shutdown();
            }
        }
    }

    void setRequestHandler(IRequestHandler rh) {
        this.requestHandler = rh;
    }
}
