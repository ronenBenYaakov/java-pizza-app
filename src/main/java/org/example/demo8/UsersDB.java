package org.example.demo8;

import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class UsersDB {
    JSONObject users;

    public UsersDB() throws Exception {
        ServerSocket server = new ServerSocket(8080);
        users = new JSONObject();

        while(true){
            Socket client = server.accept();

            new ClientHandler(client).start();
        }
    }

    class ClientHandler extends Thread{
        Socket client;
        BufferedReader in;
        PrintWriter out;

        public ClientHandler(Socket client) throws Exception{
            this.client = client;
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        }

        @Override
        public void run() {
            String message;
            try{
                while((message = in.readLine()) != null){
                    if(message.equalsIgnoreCase("login") && login())
                        break;

                    else if(message.equalsIgnoreCase("register") && register())
                        break;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        private boolean register() throws Exception{
            String username = in.readLine();
            String password = in.readLine();

            if(!users.has(username)) {
                users.put(username, password);
                out.println("success");
                return true;
            }

            out.println("fail");
            return false;
        }

        private boolean login() throws Exception{
            String username = in.readLine();
            String password = in.readLine();

            if(users.has(username) && users.get(username).equals(password)){
                out.println("success");
                return true;
            }

            out.println("fail");
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        new UsersDB();
    }
}
