package com.indev.server;

import javax.swing.*;

public class ServerMain {
    public static void main(String[] args) {
        Server serverObject = new Server();
        serverObject.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        serverObject.startServer();
    }
}
