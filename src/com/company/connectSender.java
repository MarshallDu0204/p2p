package com.company;
import java.net.*;
import java.io.*;


public class connectSender {

    public void requestConnection(String hostName,int portNum) throws IOException{

        String message = "{{{requestConnection}}}";

        InetAddress addr = InetAddress.getByName(hostName);

        Socket sendSocket = new Socket(addr,portNum);

        DataOutputStream toServer = new DataOutputStream(sendSocket.getOutputStream());

        BufferedReader fromServer = new BufferedReader(new InputStreamReader(sendSocket.getInputStream()));

        toServer.writeBytes(message);

        //add timeout

        message = fromServer.readLine();

        sendSocket.close();

    }

    public String queries(String hostName,String file,int portNum) throws IOException {

        String query = "query:"+file;

        InetAddress addr = InetAddress.getByName(hostName);

        Socket sendSocket = new Socket(addr, portNum);

        DataOutputStream toServer = new DataOutputStream(sendSocket.getOutputStream());

        BufferedReader fromServer = new BufferedReader(new InputStreamReader(sendSocket.getInputStream()));

        toServer.writeBytes(query);

        //add timeout mechanism to prevent message explode

        String result = fromServer.readLine();

        sendSocket.close();

        return result;

    }

    public void execCheck() throws IOException {
        //timeSlice check the alive

        //if success, do nothing
        //else do change
    }

    private boolean checkAlive(String hostName,int portNum) throws IOException {

        InetAddress addr = InetAddress.getByName(hostName);

        String message = "{{{check:alive}}}";

        Socket sendSocket = new Socket(addr,portNum);

        DataOutputStream toServer = new DataOutputStream(sendSocket.getOutputStream());

        BufferedReader fromServer = new BufferedReader(new InputStreamReader(sendSocket.getInputStream()));

        toServer.writeBytes(message);

        message = fromServer.readLine();

        return true;

    }

}
