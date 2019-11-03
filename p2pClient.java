
import java.io.*;
import java.net.InetAddress;
import java.util.Scanner;

public class p2pClient{

    private static int welcomePort;

    private static int transferPort;

    private static int discoverPort;

    public  connectSender connSender;

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

    public void init(p2pClient thisp2p) throws IOException {//init the client and set the variable
        readConfig("config_peer.txt");

        welcomeReceiver = new connectReceiver(welcomePort);

        uSend = new udpSender();
        uRecv = new udpReceiver(discoverPort);

        connSender = new connectSender();

        fileReq = new fileRequester();

        fileServ = new fileServer(transferPort);



        int[] tempPortList = new int[5];//set the tcpConnection available port list

        for(int i=0;i<5;i++){
            tempPortList[i] = discoverPort+i+1;
        }

        tcpConn = new Thread[5];//init the thread list for tcp connection

        heartBeatThread = new Thread[5];//init the heart beat thread

        connReceiverList = new connectReceiver[5];

        controller = peerController.init(tempPortList,welcomePort,discoverPort,transferPort);//init the peer controller and get it's instance

        setWelcomeReceiver();

        setFileServer();

        setUdpReceiver();

        client = thisp2p;//get the p2pclient instance
    }

    public static p2pClient getClient(){
        return client;
    }


    private void command() throws IOException {//wait for the user type command into the function
         System.out.println("You can start input now!\n");
         while (true){
             Scanner input = new Scanner(System.in);
             if (input.hasNext()){
                 String order = input.next();
                 String[] tempInfo = order.split("<");
                 if(tempInfo.length>1){
                     if(tempInfo[0].equals("Connect")){
                         try{
                             discover(order);//start to discover the file
                         }catch (IOException e){
                             System.out.println("discover fail");
                         }
                     }
                     else if(tempInfo[0].equals("Get")){
                         try{
                             String fileName = tempInfo[1].substring(0,tempInfo[1].length()-1);
                             query(fileName);//query with the path
                         }catch (IOException e){
                             System.out.println("query fail");
                         }
                     }
                 }
                 else{
                     if(order.equals("Leave")){
                            int connNum = peerController.getConnNum();
                            connReceiverList = new connectReceiver[5];//clear all the connection
                            tcpConn = new Thread[5];//close all the thread
                            heartBeatThread = new Thread[5];
                            peerController.clearPong();
                            peerController.cleanConnection();//clear all the connection information
                            System.out.println("All connection closed");
                     }
                     else if(order.equals("Exit")){
                            System.out.println("Bye");
                            System.exit(0);//quit
                     }
                 }
             }
         }
    }

    private void readConfig(String path) throws IOException {//read the config file and set the port
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

    public void setWelcomeReceiver(){//init the welcome tcp connection thread
        welcomeRecv = new Thread(){
          public void run(){
              try {
                  welcomeReceiver.serveConnect();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
        };
        welcomeRecv.setPriority(Thread.MAX_PRIORITY);
        welcomeRecv.start();
    }

    public void setFileServer(){//init the fileServer thread
        serveFile = new Thread(){
          public void run(){
              try {
                  fileServ.serveFile();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
        };
        serveFile.setPriority(Thread.MAX_PRIORITY);
        serveFile.start();
    }

    public void setUdpReceiver(){//init the udp receiver thread
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
    }



    public void discover(String command) throws IOException {
        String[] info = command.split("<");
        String ipAddr = info[1];
        String portNum = info[2];

        ipAddr = ipAddr.substring(0,ipAddr.length()-1);
        portNum = portNum.substring(0,portNum.length()-1);

        int port = Integer.parseInt(portNum);

        InetAddress ownIp = InetAddress.getLocalHost();

        String acHost = peerController.getPeerAddr();

        String ping = "PI:<"+acHost+">:<"+info[2]+">\n";

        uSend.sendMessage(ping, ipAddr,port);//send the ping message

        String finalPortNum = portNum;
        Thread tempSleep = new Thread(){
          public void run(){//wait 1 sec for the pong message back then read it.
              try {
                  Thread.sleep(1000);
                   Peer[] pong = peerController.getPongMsg();
                   if(pong!=null){
                       if(pong[1]==null){
                           int localPort = peerController.getAvaPort();
                           String tempIp = pong[0].getHostName();
                           int destPort = pong[0].getPortNum();
                           System.out.println("Start Connect IP:"+tempIp+" Port:"+destPort);
                           connSender.requestConnection(tempIp, destPort,localPort);//send the connection request
                           setConnection(tempIp,destPort,localPort);//set the connection
                           System.out.println("Connect Complete Ip:"+tempIp+" Port:"+destPort);
                       }
                       else{
                           int localPort = peerController.getAvaPort();
                           String tempIp = pong[0].getHostName();
                           int destPort = pong[0].getPortNum();
                           System.out.println("Start Connect IP:"+tempIp+" Port:"+destPort);
                           int resport = connSender.requestConnection(tempIp, destPort,localPort);
                           setConnection(tempIp,resport,localPort);
                           System.out.println("Connect Complete Ip:"+tempIp+" Port:"+destPort);
                           int localPort1 = peerController.getAvaPort();
                           String tempIp1  = pong[1].getHostName();
                           int destPort1 = pong[1].getPortNum();
                           System.out.println("Start Connect IP:"+tempIp1+" Port:"+destPort1);
                           connSender.requestConnection(tempIp1, destPort1,localPort1);
                           setConnection(tempIp1,destPort1,localPort1);
                           System.out.println("Connect Complete Ip:"+tempIp1+" Port:"+destPort1);
                       }
                       peerController.clearPong();
                   }

              } catch (InterruptedException | IOException e) {
                  e.printStackTrace();
              }
          }
        };
        tempSleep.setPriority(Thread.MAX_PRIORITY);
        tempSleep.start();
    }

    public void setConnection(String hostName,int portNum,int localPort) throws IOException {//get the Thread list and set the tcp connection into proper position
        final int avaThread = peerController.getAvaThread();//get the accordance thread num
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
        heartBeat(hostName,portNum,avaThread);//start to heart beat the rest client
    }

    public void heartBeat(String hostName,int portNum,int tagNum){
        final String tempHostName = hostName;
        final int tempPortNum = portNum;
        heartBeatThread[tagNum] = new Thread(){
            public void run(){
                try {
                    while (true) {
                        Thread.sleep(20000);//heart beat in evert 20 sec
                        boolean isConnect = true;
                        try{
                            isConnect = connSender.checkAlive(tempHostName, tempPortNum);
                        }catch (IOException e){
                            isConnect=false;
                            System.out.println("Connect false to "+tempHostName);
                        }
                        if (!isConnect) {
                            int threadNum = peerController.getThreadNum(tempPortNum);
                            tcpConn[threadNum].interrupt();
                            connReceiverList[threadNum] = null;
                            peerController.removeConnect(tempPortNum);
                            peerController.removeThread(tempPortNum);
                            Thread.interrupted();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        heartBeatThread[tagNum].setPriority(Thread.MAX_PRIORITY);
        heartBeatThread[tagNum].start();
    }

    public void query(String fileName) throws IOException {
        connSender.queries("abc",fileName,0,1);
    }

    public static void main(String[] args) throws IOException {
        p2pClient p2p = new p2pClient();
        p2p.init(p2p);//send the instance back ot the p2pClient
        System.out.println("Initiate complete!\n");
        p2p.command();
    }


}
