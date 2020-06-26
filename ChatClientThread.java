package client;

import java.io.*;
import java.net.*;

public class ChatClientThread extends Thread{

    private BufferedReader reader;
    private Socket s;
    private ChatClient client;

    //Constructor
    public ChatClientThread(Socket s, ChatClient client){

        this.s = s;
        this.client = client;

        try {

            reader = new BufferedReader(new InputStreamReader(s.getInputStream()));

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    //Run thread
    public void run(){

        //Infinite loop, listen for messages from server to print
        while (true){

            try {

                String toDisplay = reader.readLine();

                if (!toDisplay.equals("!logout")){

                    client.display(toDisplay);

                } else {

                    //User is logging out
                    s.close();
                    client.logOff();

                    break;

                }

            } catch (IOException e){
                e.printStackTrace();
            }

        }

    }

}
