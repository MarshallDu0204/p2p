package com.company;

import java.io.IOException;
import java.net.*;

public class udpSender {

    private static int portNum;

    public udpSender(int port){
        portNum = port;
    }

    public void sendMessage(String message,String hostName) throws IOException {

        DatagramSocket sendSocket = new DatagramSocket();

        InetAddress addr = InetAddress.getByName(hostName);

        byte[] sendData = new byte[1024];
        byte[] recvData = new byte[1024];

        sendData = message.getBytes();

        DatagramPacket sendPkt = new DatagramPacket(sendData,sendData.length,addr,portNum);

        sendSocket.send(sendPkt);

        DatagramPacket recvPkt = new DatagramPacket(recvData,recvData.length);

        sendSocket.receive(recvPkt);

        String replyMsg =  new String(recvPkt.getData());

        sendSocket.close();
    }
}
