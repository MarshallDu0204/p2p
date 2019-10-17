package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class udpReceiverTest {
    private static int portNum;

    public udpReceiverTest(int port){
        portNum = port;
    }

    public void receive() throws IOException {
        DatagramSocket recvSocket = new DatagramSocket(portNum);

        String message = "";

        byte[] recvData = new byte[1024];
        byte[] sendData = new byte[1024];

        while (true) {
            DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);

            recvSocket.receive(recvPacket);

            message = new String(recvPacket.getData());

            InetAddress addr = recvPacket.getAddress();
            int port = recvPacket.getPort();

            System.out.println(message);

            message = "response";

            //String[] ping = message.split(":");

            // if connection is full then only flood the query
            // else return the pong message and flood the query

            sendData = message.getBytes();

            DatagramPacket sendPkt = new DatagramPacket(sendData, sendData.length, addr, port);

            recvSocket.send(sendPkt);
        }

    }

}
