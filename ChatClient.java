package client;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient implements ActionListener{

    //Instance Variables
    //Main GUI
    private JTextField chatInput;
    private JTextArea textDisplay;

    //Writer
    private BufferedWriter writer;

    //Login information
    private String hostname;
    private int port;
    private String username;

    //Constructor - Builds GUI, takes in username, connects to server
    public ChatClient(){

        //Prompt for Login, followed by connecting to server and building GUI
        loginGUI();

    }

    //Builds the GUI and assigns functionality
    public void buildGUI(){

        JFrame mainFrame = new JFrame("Austin's Chat Client: " + username);
        mainFrame.setSize(350,350);

        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());

        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());

        chatInput = new JTextField();
        p1.add(chatInput, BorderLayout.CENTER);

        JButton btnSend = new JButton("Send");
        p1.add(btnSend, BorderLayout.EAST);

        textDisplay = new JTextArea();
        textDisplay.setLineWrap(true);
        textDisplay.setEditable(false);

        DefaultCaret caret = (DefaultCaret)textDisplay.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scroll = new JScrollPane(textDisplay);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        p2.add(scroll, BorderLayout.CENTER);
        p2.add(p1, BorderLayout.SOUTH);

        mainFrame.setContentPane(p2);

        //Assign listeners
        btnSend.addActionListener(this);
        chatInput.addActionListener(this);
        mainFrame.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent we){

                //User is logging out
                sendLogOff();

            }

        });

        //Display GUI
        mainFrame.setVisible(true);

    }

    //Prompts user for login information
    public void loginGUI(){

        //Frame
        JFrame loginFrame = new JFrame("Austin's Chat Login");
        loginFrame.setSize(300,120);

        //Panels
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new FlowLayout());

        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new BorderLayout());

        JPanel hostnamePanel = new JPanel();
        hostnamePanel.setLayout(new BorderLayout());

        JPanel portPanel = new JPanel();
        portPanel.setLayout(new BorderLayout());

        //Text Fields
        JTextField usernameField = new JTextField();
        usernameField.setColumns(15);
        usernamePanel.add(usernameField, BorderLayout.CENTER);

        JTextField hostnameField = new JTextField();
        hostnameField.setColumns(10);
        hostnamePanel.add(hostnameField, BorderLayout.CENTER);

        JTextField portField = new JTextField();
        portField.setColumns(5);
        portPanel.add(portField, BorderLayout.CENTER);

        //Labels
        JLabel usernameLabel = new JLabel();
        usernameLabel.setText("Username:  ");
        usernamePanel.add(usernameLabel, BorderLayout.WEST);

        JLabel hostnameLabel = new JLabel();
        hostnameLabel.setText("Hostname:  ");
        hostnamePanel.add(hostnameLabel, BorderLayout.WEST);

        JLabel portLabel = new JLabel();
        portLabel.setText("Port:  ");
        portPanel.add(portLabel, BorderLayout.WEST);

        //Assemble panels
        fieldPanel.add(usernamePanel);
        fieldPanel.add(hostnamePanel);
        fieldPanel.add(portPanel);

        mainPanel.add(fieldPanel, BorderLayout.CENTER);

        //button
        JButton btnLogin = new JButton("Log In");
        mainPanel.add(btnLogin, BorderLayout.SOUTH);

        //Close functionality
        loginFrame.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent we){

                System.exit(0);

            }

        });

        //Button functionality
        btnLogin.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent ae2){

                //Validate entered information, close if invalid
                username = usernameField.getText().trim();
                hostname = hostnameField.getText();

                try {

                    port = Integer.parseInt(portField.getText());

                } catch (NumberFormatException e){
                    e.printStackTrace();
                    System.exit(0);
                }

                //Close login window
                loginFrame.setVisible(false);

                //Build main GUI
                buildGUI();

                //Connect to server
                connectToServer();

            }

        });

        //Finish, set visible
        loginFrame.setContentPane(mainPanel);
        loginFrame.setVisible(true);

    }

    //Connect to server
    public void connectToServer(){

        //Connect to server
        try {

            Socket s = new Socket(hostname, port);
            writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

            new ChatClientThread(s, this).start();

            //Send username to server
            writer.write(username);
            writer.write("\r\n");
            writer.flush();

        } catch (IOException e){
            e.printStackTrace();
            System.exit(0);
        }

    }

    //Prints passed message to text display area
    public void display(String message){

        textDisplay.append(message + "\n");

    }

    //Send button clicked
    public void actionPerformed(ActionEvent ae) {

        String toSend = chatInput.getText();

        //Don't allow chats that are too long
        if (toSend.length() > 1000){

            display("<Chat is too long to send.>");

        } else {

            if (toSend.equals("!logout")){

                //User is logging out
                sendLogOff();

            } else if (!toSend.equals("")){

                toSend = toSend.trim();
                chatInput.setText("");

                //write out message
                try {

                    writer.write(toSend);
                    writer.write("\r\n");
                    writer.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    public void sendLogOff(){

        try {

            writer.write("!logout");
            writer.write("\r\n");
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Finish closing client when !logout message sent from server
    public void logOff(){

        System.exit(0);

    }

    //main
    public static void main(String args[]){

        try {

            ChatClient theClient = new ChatClient();

        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
