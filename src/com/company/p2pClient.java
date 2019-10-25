package com.company;
import java.io.*;
import java.net.InetAddress;


public class p2pClient{

    private static int welcomePort;

    private static int[] connectionPortList;

    private static int transferPort;

    private static int discoverPort;

    public connectSender welcomeSender;

    public connectSender connSender;

    public connectSender[] connSenderList;

    public  connectReceiver welcomeReceiver;

    public  connectReceiver[] connReceiverList;

    public udpSender uSend;

    public udpReceiver uRecv;

    public fileRequester fileReq;

    public fileServer fileServ;

    public peerController controller;

    public Thread udpRecv;

    public Thread[] tcpConn;

    public Thread welcomeRecv;

    public Thread fileRecv;

    private static p2pClient client;

    public void init() throws IOException {
        readConfig("config_peer.txt");

        welcomeSender = new connectSender();
        welcomeReceiver = new connectReceiver(welcomePort);

        uSend = new udpSender();
        uRecv = new udpReceiver(discoverPort);

        fileReq = new fileRequester();
        fileServ = new fileServer(transferPort);

        client = new p2pClient();

        int[] tempPortList = new int[5];

        for(int i=0;i<5;i++){
            tempPortList[i] = discoverPort+i+1;
        }

        controller = peerController.init(tempPortList,welcomePort,discoverPort,transferPort);
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

        for(i=0;i<3;i++){
            String temp = config[i];
            String[] splitTemp = temp.split(":");
            config[i] = splitTemp[1];
        }

        welcomePort =  Integer.parseInt(config[0]);

        transferPort = Integer.parseInt(config[1]);

        discoverPort = Integer.parseInt(config[2]);

    }

    public void setWelcomeReceiver(){
        connectReceiver connRecv = new connectReceiver(welcomePort);
        welcomeRecv = new Thread(){
          public void run(){
              try {
                  connRecv.serveConnect();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
        };
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

        String ping = "PI:<"+ownIp+">:<"+info[2]+">\n";

        uSend.sendMessage(ping, ipAddr,port);

        //wait about 2 second for the message
    }

    public void setConnection(String ipAddr,int portNum) throws IOException {
        int connPortNum = peerController.getConnNum();
        connReceiverList[connPortNum] = new connectReceiver(connPortNum+discoverPort+1);
        tcpConn[connPortNum] = new Thread(){
            public void run(){
                try {
                    connReceiverList[connPortNum].serveConnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        tcpConn[connPortNum].setPriority(Thread.MAX_PRIORITY);
        tcpConn[connPortNum].start();
        connSender.requestConnection(ipAddr,portNum,connPortNum+discoverPort+1);

        peerController.addConnect(ipAddr,portNum,connPortNum+discoverPort+1);
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
