package com.indev.client;

import javax.swing.JFrame;
public class ClientTwoMain {
    public static void main(String[] args) {
        ClientTwo clientTwoObject =new ClientTwo("127.0.0.1");
        clientTwoObject.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientTwoObject.startRunning();
    }
}