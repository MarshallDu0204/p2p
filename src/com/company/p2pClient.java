package com.company;
import java.io.*;
import java.net.InetAddress;


public class p2pClient{

    private static int connectionPort1;

    private static int connectionPort2;

    private static boolean idlePort1;

    private static boolean idlePort2;

    private static int transferPort;

    private static int discoverPort;

    public connectSender connSender1;

    public  connectReceiver connReceiver1;

    public connectSender connSender2;

    public connectReceiver connReceiver2;

    public udpSender uSend;

    public udpReceiver uRecv;

    public fileRequester fileReq;

    public fileServer fileServ;

    public peerController controller;

    public Thread udpRecv;

    public Thread tcpConn1;

    public Thread tcpConn2;

    public Thread fileRecv;

    private static p2pClient client;

    public void init() throws IOException {
        readConfig("config_peer.txt");

        connSender1 = new connectSender();
        connReceiver1 = new connectReceiver(connectionPort1);
        connSender2 = new connectSender();
        connReceiver2 = new connectReceiver(connectionPort2);

        uSend = new udpSender();
        uRecv = new udpReceiver(discoverPort);

        fileReq = new fileRequester();
        fileServ = new fileServer(transferPort);

        client = new p2pClient();

        controller = peerController.init();

        idlePort1 = true;
        idlePort2 = true;
    }

    public static p2pClient getClient(){
        return client;
    }


    private void command(String command){

    }


    private void readConfig(String path) throws IOException {
        File file = new File(path);

        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        String[] config = new String[10];
        int i=0;
        while ((line = br.readLine())!=null){
            config[i] = line;
            i++;
        }

        for(i=0;i<4;i++){
            String temp = config[i];
            String[] splitTemp = temp.split(":");
            config[i] = splitTemp[1];
        }

        connectionPort1 =  Integer.parseInt(config[0]);

        connectionPort2 = Integer.parseInt(config[1]);

        transferPort = Integer.parseInt(config[2]);

        discoverPort = Integer.parseInt(config[3]);

    }

    public void discover(String command) throws IOException {
        String[] info = command.split("<");
        String ipAddr = info[1];
        String portNum = info[2];

        ipAddr = ipAddr.substring(0,ipAddr.length()-1);
        portNum = portNum.substring(0,portNum.length()-1);

        int port = Integer.parseInt(portNum);

        udpRecv = new Thread(){
            public void run(){
                try {
                    uRecv.recvMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        udpRecv.setPriority(Thread.MAX_PRIORITY);
        udpRecv.start();
        //create a thread to open the receiver to receive the message

        InetAddress ownIp = InetAddress.getLocalHost();

        String ping = "PI:<"+ownIp+">:<"+info[2]+">";

        uSend.sendMessage(ping, ipAddr,port);

        //wait about 2 second for the message

        peerController controller = peerController.getController();
    }

    public void setConnection(String ipAddr,int portNum) throws IOException {
        if (idlePort1){
            idlePort1 = !idlePort1;
            tcpConn1 = new Thread(){
              public void run(){
                  try {
                      connReceiver1.serveConnect();
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }
            };
            tcpConn1.setPriority(Thread.MAX_PRIORITY);
            tcpConn1.start();
            connSender1.requestConnection(ipAddr,portNum);

            controller.addConnect(ipAddr,portNum,connectionPort1);

        }
        else{
            if(idlePort2){
                idlePort2 = !idlePort2;
                tcpConn2 = new Thread(){
                  public void run(){
                      try {
                          connReceiver2.serveConnect();
                      } catch (IOException e) {
                          e.printStackTrace();
                      }
                  }
                };
                tcpConn2.setPriority(Thread.MAX_PRIORITY);
                tcpConn2.start();
                connSender2.requestConnection(ipAddr,portNum);

                controller.addConnect(ipAddr,portNum,connectionPort2);
            }
        }
    }

    public void heartBeat(){

    }

    public void query(){

    }

    public static void main(String[] args) throws IOException {
        p2pClient p2p = new p2pClient();
        p2p.init();
    }


}
