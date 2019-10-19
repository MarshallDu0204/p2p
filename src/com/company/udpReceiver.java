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

                Peer[] connPeer = controller.getConnectedPeer();
                int cont = controller.getConnNum();
                String[] ipList = new String[2];
                int[] portList = new int[2];

                for(int i=0;i<cont;i++){
                    ipList[i] = connPeer[i].getIp();
                    portList[i] = connPeer[i].getPortNum();
                }

                // if connection is full then only flood the query
                String finalMessage = message;
                Thread sendThread = new Thread(){
                  public void run(){
                      udpSender uSend = new udpSender();
                      try {
                          for (int j = 0; j< cont; j++){
                              uSend.sendMessage(finalMessage,ipList[j],portList[j]);
                          }
                          if (cont ==1){
                              InetAddress localAddr = InetAddress.getLocalHost();
                              int idlePort = controller.getIdlePort();
                              if(idlePort!=0){
                                  String pong = "PO:<"+localAddr+">:<"+idlePort+">";
                                  String host = new String(String.valueOf(addr));
                                  uSend.sendMessage(pong,host,port);
                              }
                              //send pong message back
                          }
                      } catch (IOException e) {
                          e.printStackTrace();
                      }
                  }
                };

            }
            else{//recv pong message
                String ip = temp[1];
                String portNum = temp[2];
                ip = ip.substring(1,ip.length()-1);
                portNum = portNum.substring(1,portNum.length()-1);
                int portNumInt = Integer.parseInt(portNum);
                peerController controller = peerController.getController();
                controller.addPong(ip,portNumInt);
            }

            sendData = message.getBytes();

            DatagramPacket sendPkt = new DatagramPacket(sendData,sendData.length,addr,port);

            recvSocket.send(sendPkt);

        }

    }

}
