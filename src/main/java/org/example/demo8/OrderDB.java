package org.example.demo8;

import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;

public class OrderDB {
    private final JSONObject orders = new JSONObject();
    private final JSONObject orderTimes = new JSONObject();
    private final Block[] orderChain = new Block[5];

    public OrderDB() throws Exception {
        // Set up the chain of blocks
        orderChain[4] = new Block(orders, null, 1234);
        for (int i = 3; i >= 0; i--) {
            orderChain[i] = new Block(orders, orderChain[i + 1], 1235 + i);
        }

        // Start the server
        try (ServerSocket server = new ServerSocket(8081)) {
            System.out.println("OrderDB server running on port 8081...");

            while (true) {
                Socket client = server.accept();
                System.out.println("Client connected: " + client.getInetAddress());
                new ClientHandler(client).start();
            }
        }
    }

    class ClientHandler extends Thread {
        private final Socket client;
        private final BufferedReader in;
        private final PrintWriter out;

        public ClientHandler(Socket client) throws IOException {
            this.client = client;
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("order")) {
                        handleOrder();
                    }
                    else if(message.equalsIgnoreCase("get")){
                        out.println(orders);
                        out.println(orderTimes);
                    }
                    else if(message.equalsIgnoreCase("remove")){
                        String order = in.readLine();
                        orders.remove(order);
                        orderTimes.remove(order);
                    }
                    else {
                        System.out.println("Unknown command: " + message);
                        out.println("fail");
                    }
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
                e.printStackTrace();
            } finally {
                closeResources();
            }
        }

        private void handleOrder() {
            try {
                // Read client inputs
                String entitledName = in.readLine();
                String order = in.readLine();

                System.out.println("Received entitledName: " + entitledName);
                System.out.println("Received order: " + order);

                // Validate inputs
                if (entitledName == null || entitledName.isEmpty() || order == null || order.isEmpty()) {
                    System.out.println("Validation failed: Missing inputs");
                    out.println("fail");
                    return;
                }

                // Store order details
                synchronized (orders) {
                    // Capture current time when the order is placed
                    LocalDateTime orderTime = LocalDateTime.now();
                    String orderTimeStr = orderTime.toString(); // ISO format (e.g., 2025-01-22T15:30:45.123)

                    // Add order and order time to JSON objects
                    orders.put(order, entitledName); // Store the order details
                    orderTimes.put(order, orderTimeStr); // Store the time of the order

                    // Forward order data to the chain
                    try (DatagramSocket socket = new DatagramSocket(9999)) {
                        String data = order + " " + entitledName;
                        DatagramPacket packet = new DatagramPacket(
                                data.getBytes(), data.getBytes().length, InetAddress.getLocalHost(), 1235
                        );
                        socket.send(packet);
                        System.out.println("Order forwarded to chain: " + data);

                        // Simulate processing in the chain
                        orderChain[0].flow();
                    }
                }

                out.println("success");
            } catch (Exception e) {
                System.err.println("Error in handleOrder: " + e.getMessage());
                out.println("fail");
                e.printStackTrace();
            }
        }


        private void closeResources() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (client != null && !client.isClosed()) client.close();
                System.out.println("Client connection closed.");
            } catch (IOException e) {
                System.err.println("Error closing client resources: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            new OrderDB();
        } catch (Exception e) {
            System.err.println("Error starting OrderDB: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
