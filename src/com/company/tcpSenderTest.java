package com.company;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;


public class tcpSenderTest {

    public void send(String hostName,int port,String message) throws IOException {

        InetAddress addr = InetAddress.getByName(hostName);

        Socket sendSocket = new Socket(addr,port);

        DataOutputStream toServer = new DataOutputStream(sendSocket.getOutputStream());

        BufferedReader fromServer = new BufferedReader(new InputStreamReader(sendSocket.getInputStream()));

        toServer.writeBytes(message);

        message = fromServer.readLine();

        System.out.println(message);

        sendSocket.close();

    }

}
