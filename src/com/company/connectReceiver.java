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

            if (message.equals("{{{requestConnection}}}")){

                toClient.writeBytes(message);
            }

            else if (message.equals("{{{check:alive}}}")){
                message = "alive";
                toClient.writeBytes(message);
            }

            else{
                String[] query = message.split(":");
                String fileName = query[1];
                //if fileName exist, send the query back
                // else keep flood the query
                toClient.writeBytes(message);

            }

            fromClient.close();
            toClient.close();
            recvSocket.close();
        }
    }

    public boolean hasFile(String file){
        return true;
    }
}
