package com.company;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class udpReceiver{
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

            String[] temp = message.split(":");

            if(temp[0].equals("PI")){
                peerController controller = peerController.getController();

                Peer[] connPeer = peerController.getConnectedPeer();

                int cont = peerController.getConnNum();

                String[] ipList = new String[cont];
                int[] portList = new int[cont];

                for(int i=0;i<cont;i++){
                    ipList[i] = connPeer[i].getIp();
                    portList[i] = peerController.getUdpPort();
                }

                udpSender uSend = new udpSender();

                for (int j = 0; j< cont; j++){
                    if(InetAddress.getByName(ipList[j])!=addr){
                        uSend.sendMessage(message,ipList[j],portList[j]);
                    }

                }
                InetAddress localAddr = InetAddress.getLocalHost();
                int welcomePort = peerController.getWPort();

                String pong = "PO:<"+localAddr+">:<"+welcomePort+">\n";
                String host = new String(String.valueOf(addr));
                uSend.sendMessage(pong,host,peerController.getUdpPort());

                //send pong message back
            }
            else{//recv pong message
                String ip = temp[1];
                String portNum = temp[2];
                ip = ip.substring(1,ip.length()-1);
                portNum = portNum.substring(1,portNum.length()-1);
                int portNumInt = Integer.parseInt(portNum);
                peerController controller = peerController.getController();
                peerController.addPong(ip,portNumInt);
            }

            sendData = message.getBytes();

            DatagramPacket sendPkt = new DatagramPacket(sendData,sendData.length,addr,port);

            recvSocket.send(sendPkt);

        }

    }

}
