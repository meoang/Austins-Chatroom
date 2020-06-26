package server;

import java.io.*;
import java.net.*;

public class ChatServerThread extends Thread {

    private Socket s;
    private ChatServer server;
    private BufferedWriter writer;
    private String username;

    public  ChatServerThread(Socket s, ChatServer server){

        this.s = s;
        this.server = server;

    }

    public void run(){

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

            //Save username sent by Client
            username = reader.readLine();

            //Notify server and previously connected clients of new user
            server.broadcast("<Connected: " + username + ">", this);
            System.out.println("Client connected: " + username);

            //Display all past chats broadcast on the server
            server.updateChat(this);

            //String to save incoming messages
            String message;

            //Listen for incoming chats to broadcast until logout
            do {

                message = reader.readLine();

                if (message != null && !message.equals("!logout")) {
                    System.out.println("Received from " + username + ": " + message);
                    server.broadcast("[" + username + "]: " + message);
                }

            } while (message == null || !message.equals("!logout"));

            //Loop ended, user is logging out
            server.broadcast("<Disconnected: " + username + ">", this);
            System.out.println("Client disconnected: " + username);
            server.removeUser(this);
            s.close();

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public void sendMessage(String message){

        try {

            writer.write(message);
            writer.write("\r\n");
            writer.flush();

        } catch (IOException e){
            e.printStackTrace();
        }

    }

}
