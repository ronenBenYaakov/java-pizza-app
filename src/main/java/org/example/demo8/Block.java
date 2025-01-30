package org.example.demo8;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class Block {
    private JSONObject dst;
    private Block next;
    private String data;
    private int port;
    private DatagramSocket socket;

    public Block(JSONObject dst, Block next, int port) throws IOException {
        this.socket = new DatagramSocket(port);
        this.port = port;
        this.dst = dst;
        this.next = next;
    }

    public void flow() throws Exception {
        byte[] receivedBuffer = new byte[1024];
        DatagramPacket receivedPacket = new DatagramPacket(receivedBuffer, receivedBuffer.length);

        try {
            // Set a timeout to avoid indefinite blocking
            socket.setSoTimeout(5000); // Timeout in milliseconds
            System.out.println("Waiting for data on port: " + port);
            socket.receive(receivedPacket);

            String receivedData = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
            System.out.println("Received data: " + receivedData);

            if (next == null) {
                write(dst, receivedData);
                return;
            }

            // Forward data to the next block
            next.socket.send(new DatagramPacket(
                    receivedData.getBytes(),
                    receivedData.getBytes().length,
                    InetAddress.getLocalHost(),
                    next.port
            ));

            next.flow();
        } catch (SocketTimeoutException e) {
            System.err.println("Timeout: No data received on port " + port);
        }
    }


    private void write(JSONObject dst, String data) {
        try {
            String[] rawData = data.split(" ");
            if (rawData.length == 2) {
                dst.put(rawData[0], rawData[1]);
            } else {
                System.err.println("Invalid data format: " + data);
            }
        } catch (Exception e) {
            System.err.println("Error writing data: " + e.getMessage());
        }
    }

    public void closeSocket() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
