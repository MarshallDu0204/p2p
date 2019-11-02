

import java.io.IOException;
import java.net.*;

public class udpSender{

    public void sendMessage(String message,String ipAddr,int portNum) throws IOException {

        DatagramSocket sendSocket = new DatagramSocket();

        InetAddress addr = InetAddress.getByName(ipAddr);

        byte[] sendData = new byte[1024];
        byte[] recvData = new byte[1024];

        sendData = message.getBytes();

        DatagramPacket sendPkt = new DatagramPacket(sendData,sendData.length,addr,portNum);

        sendSocket.send(sendPkt);

        sendSocket.close();
    }

}
