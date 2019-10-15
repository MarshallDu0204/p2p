package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class connectReceiver {

    private static int portNum;

    public connectReceiver(int port){
        portNum = port;
    }

    public void serveConnect() throws IOException {
        ServerSocket welcomeSocket = new ServerSocket(portNum);

        String message = "connect";

        while (true){
            Socket recvSocket = welcomeSocket.accept();

            BufferedReader fromClient = new BufferedReader(new InputStreamReader(recvSocket.getInputStream()));

            DataOutputStream toClient = new DataOutputStream(recvSocket.getOutputStream());

            message = fromClient.readLine();

            if (message.equals("requestConnection:Sender")){
                //act as the client
            }
            else if (message.equals("requestConnection:Receiver")){
                //act as the server
            }
            else{
                //check alive packet
            }

            toClient.writeBytes(message);

            fromClient.close();
            toClient.close();
            recvSocket.close();
        }
    }
}
