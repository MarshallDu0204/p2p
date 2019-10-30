package com.company;
import java.io.*;
import java.net.InetAddress;
import java.util.Scanner;


public class p2pClient{

    private static int welcomePort;

    private static int transferPort;

    private static int discoverPort;

    public connectSender connSender;

    public  connectReceiver welcomeReceiver;

    public  connectReceiver[] connReceiverList;

    public udpSender uSend;

    public udpReceiver uRecv;

    public fileRequester fileReq;

    public fileServer fileServ;

    public static peerController controller;

    public Thread udpRecv;

    public Thread[] tcpConn;

    public Thread[] heartBeatThread;

    public Thread welcomeRecv;

    public Thread serveFile;


    private static p2pClient client;

    public void init() throws IOException {
        readConfig("config_peer.txt");

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

        tcpConn = new Thread[5];

        heartBeatThread = new Thread[5];

        connReceiverList = new connectReceiver[5];

        controller = peerController.init(tempPortList,welcomePort,discoverPort,transferPort);

        setWelcomeReceiver();

        setFileServer();
    }

    public static p2pClient getClient(){
        return client;
    }


    private void command() throws IOException {
         while (true){
             Scanner input = new Scanner(System.in);
             if (input.hasNext()){
                 String order = input.next();
                 String[] tempInfo = order.split("<");
                 if(tempInfo.length>1){
                     if(tempInfo[0].equals("Connect")){
                         discover(order);
                     }
                     else if(tempInfo[0].equals("Get")){
                         String fileName = tempInfo[1].substring(0,tempInfo[1].length()-1);
                         query(fileName);
                     }
                 }
                 else{
                     if(order.equals("Leave")){

                     }
                     else if(order.equals("Exit")){

                     }
                 }
             }
         }
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
        welcomeRecv = new Thread(){
          public void run(){
              try {
                  welcomeReceiver.serveConnect();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
        };
    }

    public void setFileServer(){
        serveFile = new Thread(){
          public void run(){
              try {
                  fileServ.serveFile();
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

        Thread tempSleep = new Thread(){
          public void run(){
              try {
                  Thread.sleep(1000);
                   Peer[] pong = peerController.getPongMsg();
                   if(pong[1].getPortNum()==0){
                       int localPort = peerController.getAvaPort();
                       String destIP = pong[0].getHostName();
                       int destPort = pong[0].getPortNum();
                       setConnection(destIP,destPort,localPort);
                   }
                   else{
                       int localPort = peerController.getAvaPort();
                       String destIP = pong[0].getHostName();
                       int destPort = pong[0].getPortNum();
                       setConnection(destIP,destPort,localPort);
                       int localPort1 = peerController.getAvaPort();
                       String destIP1 = pong[1].getHostName();
                       int destPort1 = pong[1].getPortNum();
                       setConnection(destIP1,destPort1,localPort1);
                   }
              } catch (InterruptedException | IOException e) {
                  e.printStackTrace();
              }
          }
        };
    }

    public void setConnection(String hostName,int portNum,int localPort) throws IOException {
        final int avaThread = peerController.getAvaThread();
        connReceiverList[avaThread] = new connectReceiver(localPort);
        tcpConn[avaThread] = new Thread(){
            public void run(){
                try {
                    connReceiverList[avaThread].serveConnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        tcpConn[avaThread].setPriority(Thread.MAX_PRIORITY);
        tcpConn[avaThread].start();
        peerController.addThread(localPort,avaThread);
        heartBeat(hostName,portNum,avaThread);
    }

    public void heartBeat(String hostName,int portNum,int tagNum){
        final String tempHostName = hostName;
        final int tempPortNum = portNum;
        heartBeatThread[tagNum] = new Thread(){
            public void run(){
                try {
                    while (true) {
                        Thread.sleep(20000);
                        boolean isConnect = connSender.checkAlive(tempHostName, tempPortNum);
                        if (!isConnect) {
                            int threadNum = peerController.getThreadNum(tempPortNum);
                            tcpConn[threadNum].interrupt();
                            connReceiverList[threadNum] = null;
                            peerController.removeConnect(tempPortNum);
                            peerController.removeThread(tempPortNum);
                            Thread.interrupted();
                        }
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
        heartBeatThread[tagNum].setPriority(Thread.MAX_PRIORITY);
        heartBeatThread[tagNum].start();
    }

    public void query(String fileName) throws IOException {
        connSender.queries("",fileName,0,1);
    }

    public static void main(String[] args) throws IOException {
        p2pClient p2p = new p2pClient();
        p2p.init();
        p2p.command();
    }


}
