package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class udpReceiver {
    private static int portNum;

    public udpReceiver(int port){
        portNum = port;
    }

    public void recvMessage() throws IOException {
        DatagramSocket recvSocket = new DatagramSocket(portNum);

        String message = "";

        byte[] recvData = new byte[1024];
        byte[] sendData = new byte[1024];

        while (true){
            DatagramPacket recvPacket = new DatagramPacket(recvData,recvData.length);

            recvSocket.receive(recvPacket);

            message = new String(recvPacket.getData());

            InetAddress addr = recvPacket.getAddress();
            int port = recvPacket.getPort();



        }

    }


}
