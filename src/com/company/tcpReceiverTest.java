package com.company;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class tcpReceiverTest {
    private static int port;

    public tcpReceiverTest(int portNum){
        port = portNum;
    }

    public void receive() throws IOException {

        ServerSocket welcomeSocket = new ServerSocket(port);

        String message = "";

        while (true){
            Socket recvSocket = welcomeSocket.accept();

            BufferedReader fromClient = new BufferedReader(new InputStreamReader(recvSocket.getInputStream()));

            DataOutputStream toClient = new DataOutputStream(recvSocket.getOutputStream());

            message = fromClient.readLine();

            message = "ok\n";

            System.out.println("receive");

            toClient.writeBytes(message);

            fromClient.close();
            toClient.close();
            recvSocket.close();
        }

    }

}
