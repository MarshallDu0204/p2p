package com.company;

import java.io.IOException;
import java.net.*;

public class udpSender {

    public void sendMessage(String discovery,String hostName,int portNum) throws IOException {

        DatagramSocket sendSocket = new DatagramSocket();

        InetAddress addr = InetAddress.getByName(hostName);

        byte[] sendData = new byte[1024];
        byte[] recvData = new byte[1024];

        sendData = discovery.getBytes();

        DatagramPacket sendPkt = new DatagramPacket(sendData,sendData.length,addr,portNum);

        sendSocket.send(sendPkt);

        DatagramPacket recvPkt = new DatagramPacket(recvData,recvData.length);

        sendSocket.receive(recvPkt);

        String replyMsg =  new String(recvPkt.getData());

        sendSocket.close();
    }
}
