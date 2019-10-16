package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class fileRequester {

    private static int portNum;

    public fileRequester(int port){
        portNum = port;
    }

    public void requestFile(String fileName,String hostName) throws IOException {

        String line;

        InetAddress addr = InetAddress.getByName(hostName);

        Socket sendSocket = new Socket(addr,portNum);

        DataOutputStream toServer = new DataOutputStream(sendSocket.getOutputStream());

        BufferedReader fromServer = new BufferedReader(new InputStreamReader(sendSocket.getInputStream()));

        toServer.writeBytes(fileName);

        String[] message = new String[5000];

        int i = 0;

        while ((line = fromServer.readLine())!=null){
            message[i] = fromServer.readLine();
            i++;
        }

        sendSocket.close();
    }

}
