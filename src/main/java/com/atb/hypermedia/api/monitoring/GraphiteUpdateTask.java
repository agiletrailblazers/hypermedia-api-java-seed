package com.atb.hypermedia.api.monitoring;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphiteUpdateTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(GraphiteUpdateTask.class);
    private final byte[] messageBuffer;
    private final GraphiteMonitor graphiteMonitor;

    public GraphiteUpdateTask(byte[] buffer, GraphiteMonitor graphiteMonitor) {
        this.messageBuffer = buffer;
        this.graphiteMonitor = graphiteMonitor;
    }

    public void run() {
        try {
            if(!this.graphiteMonitor.isDisabled()) {
                int e = this.graphiteMonitor.getGraphitePort();
                InetAddress address = this.graphiteMonitor.getGraphiteAddress();
                DatagramSocket socket = this.graphiteMonitor.getSocket();
                DatagramPacket packet = new DatagramPacket(this.messageBuffer, this.messageBuffer.length, address, e);
                socket.send(packet);
            }
        } catch (Exception var5) {
            logger.warn("Failed to send updated metrics to Graphite.", var5);
        }

    }

    public byte[] getMessageBuffer() {
        return this.messageBuffer;
    }
}
