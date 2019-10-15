package com.company;
import java.net.*;
import java.io.*;


public class connectSender {
    private static int portNum;
    private String host;

    public connectSender(int port){
        portNum = port;
    }

    public void requestConnection(int type,String hostName) throws IOException{
        String message;

        if (type==1){
            message = "requestConnection:Sender";
        }
        else{
            message = "requestConnection:Receiver";
        }

        Socket sendSocket = new Socket(hostName,portNum);

        DataOutputStream toServer = new DataOutputStream(sendSocket.getOutputStream());

        BufferedReader fromServer = new BufferedReader(new InputStreamReader(sendSocket.getInputStream()));

        toServer.writeBytes(message);

        message = fromServer.readLine();

        if (message.equals("ok")){
            //set connection
        }

        sendSocket.close();
    }

    public void execCheck() throws IOException {
        //timeSlice check the alive
        checkAlive(host);
        //if success, do nothing
        //else do change
    }

    private void checkAlive(String hostName) throws IOException {

        String message = "check:alive";

        Socket sendSocket = new Socket(hostName,portNum);

        DataOutputStream toServer = new DataOutputStream(sendSocket.getOutputStream());

        BufferedReader fromServer = new BufferedReader(new InputStreamReader(sendSocket.getInputStream()));

        toServer.writeBytes(message);

        message = fromServer.readLine();

    }


}
