package com.company;

import java.io.IOException;
import java.net.InetAddress;

public class testClient {

    public void init() throws IOException {
        /*
        tcpSenderTest sender = new tcpSenderTest();
        tcpReceiverTest receiver = new tcpReceiverTest(52060);
        sender.send("eecslab-11.case.edu",52060,"Hello\n");//MUST ADD \n AT THE END OF THE MESSAGE
        receiver.receive();*/

        udpSenderTest sender = new udpSenderTest();
        udpReceiverTest receiver = new udpReceiverTest(52060);

        sender.send("Hello\n","eecslab-13.case.edu",52060);
        receiver.receive();
    }

    public static void main(String[] args) throws IOException {
        testClient client = new testClient();
        client.init();
    }
}
