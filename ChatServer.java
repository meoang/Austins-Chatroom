package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    //Instance variables
    private int port;
    private ArrayList<ChatServerThread> clients;
    private ArrayList<String> chatLog;

    //Constructor
    public ChatServer(int port){

        this.port = port;
        clients = new ArrayList<>();

        System.out.println("Austin's Chat Server started.");

        //Apply greeting message
        chatLog = new ArrayList<>();
        chatLog.add("<Welcome to Austin's Chat!>");
        chatLog.add("<Type \"!logout\" or close the window to leave.>");

    }

    //Loop for clients
    public void startUp(){

        try {

            ServerSocket ss = new ServerSocket(port);

            System.out.println("Listening to port: " + port + ".");

            while (true){

                Socket s = ss.accept();

                ChatServerThread newClient = new ChatServerThread(s, this);
                clients.add(newClient);
                newClient.start();

            }

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    //Sends out passed message to all connected clients
    public void broadcast(String message){

        for (ChatServerThread client : clients)
            client.sendMessage(message);

        chatLog.add(message);

    }

    //broadcast while leaving out one client
    public void broadcast(String message, ChatServerThread exception){

        for (ChatServerThread client : clients){

            if (client != exception)
                client.sendMessage(message);

        }

        chatLog.add(message);

    }

    //sends out passed message to ONLY passed client
    public void personalMessage(String message, ChatServerThread receiver){

        receiver.sendMessage(message);

    }

    //sends out the full chat log
    public void updateChat(ChatServerThread receiver){

        for (int i = 0; i < chatLog.size(); i++)
            personalMessage(chatLog.get(i), receiver);


    }

    public void removeUser (ChatServerThread toRemove){

        clients.remove(toRemove);

        toRemove.sendMessage("!logout");

    }

    //Main
    public static void main (String args[]){

        System.out.print("Enter port: ");

        Scanner input = new Scanner(System.in);
        int port =  input.nextInt();
        input.nextLine();

        ChatServer server = new ChatServer(port);
        server.startUp();

    }

}
