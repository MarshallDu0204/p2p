

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

            InetAddress getAddr = recvPacket.getAddress();
            int port = recvPacket.getPort();

            String[] temp = message.split(":");

            if(temp[0].equals("PI")){
                peerController controller = peerController.getController();

                udpSender uSend = new udpSender();

                Peer[] connPeer = peerController.getConnectedPeer();

                int cont = peerController.getConnNum();

                if(cont>0){//if the message is ping, flood it to the neighbour with out the sender of the ping
                    String[] hostList = new String[cont];
                    int[] portList = new int[cont];

                    for(int i=0;i<cont;i++){
                        hostList[i] = connPeer[i].getHostName();
                        portList[i] = peerController.getUdpPort();
                    }

                    for (int j = 0; j< cont; j++){
                        if(!InetAddress.getByName(hostList[j]).equals(getAddr)){//prevent message back
                            uSend.sendMessage(message,hostList[j],portList[j]);
                        }
                    }
                }

                //InetAddress localAddr = InetAddress.getLocalHost();

                String strAddr = peerController.getPeerAddr();

                int welcomePort = peerController.getWelcomePort();

                String pong = "PO:<"+strAddr+">:<"+welcomePort+">\n";

                strAddr = temp[1].substring(1,temp[1].length()-1);

                uSend.sendMessage(pong,strAddr,portNum);//send pong message back

                //send pong message back
            }
            else{//recv pong message

                String ip = temp[1];

                String portNumRecv = temp[2];

                ip = ip.substring(1,ip.length()-1);

                portNumRecv = portNumRecv.substring(1,6);

                int portNumInt = Integer.parseInt(portNumRecv);

                peerController controller = peerController.getController();

                peerController.addPong(ip,portNumInt);//add pong message to the list
            }
        }

    }

}
