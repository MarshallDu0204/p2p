package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class udpSenderTest {

    public void send(String message,String hostName,int portNum) throws IOException {

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

        System.out.println(replyMsg);

        sendSocket.close();

    }
}
