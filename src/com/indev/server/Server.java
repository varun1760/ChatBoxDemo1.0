package com.indev.server;

// import necessary
// extends JFrame
// constructor
// create chat Field
// add action listener with lambda expression.. Hint = event -> send message
// run server.. Hint = port and backlog... wait connection, set streams, during chat
// wait for connection.. Hint = throw, show waiting connection, connect, show connected
// get stream setup to send and receive data..
                    // Hint = throw, I/O streams connection, show stream set successful
// during chat conv...
                    // Hint = throw, string,send message, set chat true,
                    // handle exception if message readable, END conv
// close stream and socket... Hint = handle exception if connection closed or not
// send message to client... Hint = String arg, stream and show message
// update chat window... Hint = final String arg, create thread to append chat text
// let user type... Hint = final Boolean arg, create thread to set trueOrFalse

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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame {
    private final JTextField userText;
    private final JTextArea chatBox;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket connection;
    private ServerSocket serverSocket;

    // Constructor
    public Server(){
        super("Server ChatBox");

        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                event -> {
                    sendMessage(event.getActionCommand());
                    userText.setText("");
                }
        );
        add(userText, BorderLayout.NORTH);

        chatBox = new JTextArea();
        chatBox.setEditable(false);
        add(new JScrollPane(chatBox), BorderLayout.CENTER);
        setSize(300,150);
        setVisible(true);
    }

    // run server
    public void startServer(){
        try{
            serverSocket = new ServerSocket(5555,200);
            //noinspection InfiniteLoopStatement
            while (true){
                try{
                    waitForConnection();
                    setIOStream();
                    duringChat();
                }catch (EOFException eofException){
                    showMessage("\n Server Ended The Connection \n");
                }finally {
                    closeConversation();
                }
            }
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    // wait for connection
    private void waitForConnection() throws IOException {
        showMessage("\n Waiting for someone to connect... \n");
        connection = serverSocket.accept();
        showMessage("\n You are connected to " + connection.getInetAddress().getHostName());
    }

    // get stream setup to send and receive data
    private void setIOStream() throws IOException{
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Streams are active \n");
    }

    // during chat conv
    private void duringChat() throws IOException{
        String message = "You are connected";
        sendMessage(message);
        typePermission(true);
        do{
            try{
                message = (String) inputStream.readObject();
                showMessage("\n" + message);
            }catch (ClassNotFoundException cnf){
                showMessage("\n Unreadable message \n");
            }
        }while(!message.equals("CLIENT - END"));
    }

    // close stream and socket
    private void closeConversation(){
        showMessage("\n Closing Connection... \n");
        typePermission(false);
        try{
            outputStream.close();
            inputStream.close();
            connection.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // send message to client
    private void sendMessage(String message){
        try{
            outputStream.writeObject("SERVER - " + message);
            outputStream.flush();
            showMessage("\n SERVER - "+message);
        }catch (IOException e){
            chatBox.append("\n Error: Unable send the message");
        }
    }

    // update chat window
    private void showMessage(final String text){
        SwingUtilities.invokeLater(
                () -> chatBox.append(text)
        );
    }

    // let user type
    private void typePermission(final Boolean tof){
        SwingUtilities.invokeLater(
                () -> userText.setEditable(tof)
        );
    }
}