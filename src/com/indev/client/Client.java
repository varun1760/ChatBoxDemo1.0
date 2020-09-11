package com.indev.client;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
// to use anonymous class instead lambda expression import these
/* import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;   */
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends JFrame {
    private final JTextField userText;
    private final JTextArea chatBox;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket connection;
    private String message ="";
    private final String serverIP;

    // constructor
    public Client(String host){
        super("Client ChatBox");
        serverIP = host;

        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                event -> {
                    sendMessage(event.getActionCommand());
                    userText.setText("");
                }
                // old school: anonymous class
                /* new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendMessage(e.getActionCommand());
                        userText.setText("");
                    }
                } */
        );
        add(userText, BorderLayout.NORTH);

        chatBox = new JTextArea();
        chatBox.setEditable(false);
        add(new JScrollPane(chatBox), BorderLayout.CENTER);
        setSize(300,150);
        setVisible(true);
    }

    // connect to server
    public void startRunning(){
        try{
            connectToServer();
            setIOStream();
            duringChat();
        }catch (EOFException eofException){
            showMessage("\n Client Ended the connection");
        }catch (IOException ioException){
            ioException.printStackTrace();
        }finally {
            closeConversation();
        }
    }

    // connecting to server
    private void connectToServer() throws IOException{
        showMessage("\n Attempting connection...");
        connection = new Socket(InetAddress.getByName(serverIP),5555);
        showMessage("\n You are connected to: "+ connection.getInetAddress().getHostName());
    }

    // setup I/O streams
    private void setIOStream() throws IOException{
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Streams are active");
    }

    // while chatting
    private void duringChat() throws IOException{
        typePermission(true);
        do{
            try{
                message = (String) inputStream.readObject();
                showMessage("\n" + message);
            }catch (ClassNotFoundException cnf){
                showMessage("\n Unreadable message");
            }
        }while (!message.equals("SERVER - END"));
    }


    private void closeConversation() {
        showMessage("\n Closing Connection");
        typePermission(false);
        try{
            outputStream.close();
            inputStream.close();
            connection.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    // send message
    private void sendMessage(String message){
        try{
            outputStream.writeObject("CLIENT - "+message);
            outputStream.flush();
            showMessage("\n CLIENT - "+message);
        }catch (IOException e){
            chatBox.append("\n Unable to send Message!");
        }
    }
    private void showMessage(final String message){
        SwingUtilities.invokeLater(
                ()-> chatBox.append(message)
                // old school: Anonymous class
                /*  new Runnable() {
                    @Override
                    public void run() {
                        chatBox.append(message);
                    }
                }   */
        );
    }
    private void typePermission(final boolean tof) {
        SwingUtilities.invokeLater(
                ()-> userText.setEditable(tof)
                // old school: Anonymous class
                /* new Runnable() {
                    @Override
                    public void run() {
                        userText.setEditable(tof);
                    }
                }   */
        );
    }
}