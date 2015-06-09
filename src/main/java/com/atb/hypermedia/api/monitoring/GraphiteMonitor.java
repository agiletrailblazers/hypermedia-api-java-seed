package com.atb.hypermedia.api.monitoring;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphiteMonitor {
    public static final int DEFAULT_MAX_TASKS_IN_THREADPOOL_QUEUE = 100000;
    private static final String NEWLINE = "\n";
    private static final String SPACE = " ";
    private static final String DOT = ".";
    private static final Logger logger = LoggerFactory.getLogger(GraphiteMonitor.class);
    private static final String GRAPHITE_DEFAULT_HOST = "graphite";
    private static final int GRAPHITE_DEFAULT_PORT = 2003;
    private final String appName;
    private final String graphiteHostname;
    private final int graphitePort;
    private final int maxNumberOfTasksAllowed;
    private final LazyHostResolver hostResolver;
    private InetAddress graphiteAddress;
    private String localHostname;
    private boolean isDisabled;
    private DatagramSocket socket;
    private ExecutorService executorService;

    public GraphiteMonitor(String appName) {
        this(appName, "graphite", 2003, 100000);
    }

    public GraphiteMonitor(String appName, String graphiteHostName, int graphitePort, int maxNumberOfTasksAllowed) {
        this.isDisabled = false;
        this.appName = appName;
        this.graphiteHostname = graphiteHostName;
        this.graphitePort = graphitePort;
        this.hostResolver = new LazyHostResolver();
        this.maxNumberOfTasksAllowed = maxNumberOfTasksAllowed;
        this.executorService = this.createExecutor();
    }

    public void sendData(String metricPath, double value) {
        this.sendData(metricPath, value, System.currentTimeMillis() / 1000L);
    }

    public void sendData(String metricPath, double value, long timestamp) {
        if(!this.isDisabled) {
            byte[] buffer = this.buildMessage(this.getLocalhost(), metricPath, value, timestamp);
            this.executorService.submit(new GraphiteUpdateTask(buffer, this));
        }

    }

    public DatagramSocket getSocket() throws SocketException {
        if(this.socket == null || this.socket.isClosed()) {
            this.socket = new DatagramSocket();
        }

        return this.socket;
    }

    public void disable() {
        this.isDisabled = true;
        this.executorService.shutdown();
    }

    public void enable() {
        this.isDisabled = false;
        if(this.executorService.isShutdown()) {
            this.executorService = this.createExecutor();
        }

    }

    public boolean isDisabled() {
        return this.isDisabled;
    }

    public InetAddress getGraphiteAddress() {
        if(this.graphiteAddress == null) {
            this.graphiteAddress = this.resolveHostname(this.graphiteHostname);
        }

        return this.graphiteAddress;
    }

    public int getGraphitePort() {
        return this.graphitePort;
    }

    private byte[] buildMessage(String localHostName, String metricPath, double value, long timestamp) {
        StringBuilder builder = new StringBuilder();
        builder.append(this.appName).append(".").append(localHostName).append(".").append(metricPath).append(" ").append(value).append(" ").append(timestamp).append("\n");
        String message = builder.toString();
        logger.trace(message);
        return message.getBytes();
    }

    private String getLocalhost() {
        if(this.localHostname == null) {
            this.localHostname = this.resolveLocalHostname();
        }

        return this.localHostname;
    }

    private String resolveLocalHostname() {
        String localHostname = null;

        try {
            localHostname = this.hostResolver.resolveLocalHost();
            localHostname = localHostname.replace('.', '_');
        } catch (UnknownHostException var3) {
            logger.error("Unable to resolve localhost so disabling GraphiteMonitor.", var3);
            this.disable();
        }

        return localHostname;
    }

    private InetAddress resolveHostname(String host) {
        InetAddress hostname = null;

        try {
            hostname = this.hostResolver.resolveHost(host);
        } catch (UnknownHostException var4) {
            logger.error("Unable to resolve hostname so disabling GraphiteMonitor.", var4);
            this.disable();
        }

        return hostname;
    }

    private ThreadPoolExecutor createExecutor() {
        return new ThreadPoolExecutor(1, 1, 1L, TimeUnit.SECONDS, new ArrayBlockingQueue(this.maxNumberOfTasksAllowed), new DiscardOldestPolicy());
    }
}
