package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class fileSender {

    private static int portNum;


    public fileSender(int port){
        portNum = port;
    }

    public void requestFile(String fileName,String hostName) throws IOException {
        InetAddress localIp = InetAddress.getLocalHost();

        String addr = localIp.getHostAddress();

        String message = "{{{request}}}+++:"+fileName+":"+addr;

        Socket sendSocket = new Socket(hostName,portNum);

        DataOutputStream toServer = new DataOutputStream(sendSocket.getOutputStream());

        BufferedReader fromServer = new BufferedReader(new InputStreamReader(sendSocket.getInputStream()));

        toServer.writeBytes(message);

        message = fromServer.readLine();

        sendSocket.close();
    }

    public void sendFile(String filePath,String hostName) throws IOException {

        String message;
        String line;

        BufferedReader file = new BufferedReader(new FileReader(filePath));

        Socket sendSocket = new Socket(hostName,portNum);

        DataOutputStream toServer = new DataOutputStream(sendSocket.getOutputStream());

        BufferedReader fromServer = new BufferedReader(new InputStreamReader(sendSocket.getInputStream()));

        while ((line = file.readLine())!=null){
            message = file.readLine();
            toServer.writeBytes(message);
        }

        message = "{{{end}}}+++:"+"end";

        toServer.writeBytes(message);

        sendSocket.close();
    }

}
