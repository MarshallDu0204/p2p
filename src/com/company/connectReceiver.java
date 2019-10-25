package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class connectReceiver {

    private static int portNum;

    public connectReceiver(int port){
        portNum = port;
    }

    public int getPortNum(){
        return portNum;
    }

    public void serveConnect() throws IOException {
        ServerSocket welcomeSocket = new ServerSocket(portNum);

        String message = "connect";

        while (true){
            Socket recvSocket = welcomeSocket.accept();

            BufferedReader fromClient = new BufferedReader(new InputStreamReader(recvSocket.getInputStream()));

            DataOutputStream toClient = new DataOutputStream(recvSocket.getOutputStream());

            message = fromClient.readLine();

            peerController controller = peerController.getController();

            String[] tempMessage = message.split(":");

            if (tempMessage[0].equals("{{{requestConnection}}}")){
                String[] connInfo = tempMessage[1].split(",");
                String reqHost = connInfo[0];
                reqHost = reqHost.substring(1,reqHost.length());
                String reqPort = connInfo[1];
                reqPort = reqPort.substring(0,reqPort.length()-1);
                int connPort = Integer.parseInt(reqPort);
                p2pClient client = p2pClient.getClient();
                //call the client to set the connection with set of parameters
                message = "ok\n";
                toClient.writeBytes(message);
            }

            else if (tempMessage[0].equals("{{{check}}}")){
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
