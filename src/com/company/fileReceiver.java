package com.company;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class fileReceiver {
    private static int portNum;

    public fileReceiver(int port){
        portNum = port;
    }

    public void serveFile() throws IOException {
        ServerSocket welcomeSocket = new ServerSocket(portNum);

        String message = "";

        while (true){
            Socket recvSocket = welcomeSocket.accept();

            BufferedReader fromClient = new BufferedReader(new InputStreamReader(recvSocket.getInputStream()));

            DataOutputStream toClient = new DataOutputStream(recvSocket.getOutputStream());

            message = fromClient.readLine();

           try {
               String[] temp = message.split(":");
               if (temp.length==3 && temp[0].equals("{{{request}}}+++")) {
                   //request file name
               }
               else if(temp.length==2 && temp[0].equals("{{{end}}}+++")){
                   //file end
               }
               else {
                   //append the message
               }
           }finally {
               //append the message
           }

            toClient.writeBytes(message);

            fromClient.close();
            toClient.close();
            recvSocket.close();
        }
    }



}
