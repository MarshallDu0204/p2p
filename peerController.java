

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class peerController {
    private static int pongNum = 0;
    private static int connectNum = 0;
    private static Peer[] pongPeer = new Peer[5];
    private static Peer[] connectedPeer = new Peer[5];
    private static int[] queryList = new int[1000];
    private static int queryNum = 0;
    private static int[] localPort = new int[5];
    private static int[] actualPort = new int[5];
    private static String[] fileList = new String[50];
    private static int fileNum = 0;
    private static int[] accordance = new int[5];
    private static int fPort;
    private static int uPort;
    private static int wPort;
    public static int idNum;
    public static int queryID;
    private static peerController controller;

    public static boolean pongExist(String hostName, int portNum){
        for(int i=0;i<pongNum;i++){
            if (pongPeer[i].getHostName().equals(hostName) && pongPeer[i].getPortNum() == portNum) {
                return true;
            }
        }
        return false;
    }

    public static void addPong(String hostName, int portNum){
        if (!pongExist(hostName,portNum)) {
            if (pongNum < 5) {
                pongPeer[pongNum] = new Peer(hostName, portNum);
                pongNum++;
            }
        }
    }

    public static Peer[] getPongMsg(){
        Peer[] peers = new Peer[2];
        if(pongPeer[0]==null){
            return null;
        }
        else if(pongPeer[1]==null){
            peers[0] = pongPeer[0];
            return peers;
        }
        else{
            peers[0] = pongPeer[0];
            peers[1] = pongPeer[1];
            return peers;
        }
    }

    public static int getWelcomePort(){
        return wPort;
    }

    public static int getUdpPort(){
        return uPort;
    }

    public static int getFilePort(){
        return fPort;
    }

    public static void addConnect(String hostName,int portNum,int selfPort){
        if(connectNum<5){
            connectedPeer[connectNum] = new Peer(hostName,portNum);
            localPort[connectNum] = selfPort;
            connectNum++;
        }
    }

    public static void removeConnect(int portNum){
        Peer[] temp = new Peer[5];
        int[] newLocalPort = new int[5];
        int k = 0;
        for(int i=0;i<connectNum;i++){
            if (connectedPeer[i].getPortNum()!=portNum) {
                temp[k] = connectedPeer[i];
                newLocalPort[k] = localPort[i];
                k++;
            }
        }
        connectNum--;
        connectedPeer = temp;
    }

    public static void clearPong(){
        Peer[] pong = new Peer[5];
        pongPeer = pong;
        pongNum = 0;
    }

    public static int getConnNum(){
        return connectNum;
    }

    public static Peer[] selectPeer() {
        Peer[] getPeer = new Peer[2];
        for(int i=0;i<2;i++){
            getPeer[i] = pongPeer[i];
        }
        return getPeer;
    }

    public static Peer[] getConnectedPeer(){
        return connectedPeer;
    }


    public static int getAvaPort(){
        return actualPort[connectNum];
    }

    public static peerController init(int[] acPort,int welcomePort,int udpPort,int filePort) throws UnknownHostException {
        controller = new peerController();
        actualPort = acPort;
        wPort = welcomePort;
        uPort = udpPort;
        fPort = filePort;

        InetAddress addr = InetAddress.getLocalHost();
        String strAddr = addr.getHostName();
        String[] tempAddr = strAddr.split("-");
        String tempHash = tempAddr[1];

        idNum = Integer.parseInt(tempHash);

        queryID = idNum*100;

        readDir("shared/");

        return controller;
    }

    public static int getQueryID() {
        queryID++;
        return queryID-1;
    }

    public static void addQueryNum(int ID){
        boolean add = true;
        for (int i=0;i<queryNum;i++){
            if (queryList[i]==ID) {
                add = !add;
            }
        }
        if (add){
            queryNum++;
            queryList[queryNum] = ID;
        }
    }

    public static boolean inQueryList(int ID){
        boolean add = false;
        for (int i=0;i<queryNum;i++){
            if (queryList[i]==ID) {
                add = !add;
            }
        }
        return add;
    }

    public static void readDir(String dir){
        File file = new File(dir);
        File[] tempList = file.listFiles();

        for (int i = 0; i<tempList.length; i++){
            if(tempList[i].isFile()){
                String tempName = tempList[i].getName();
                fileList[i] = tempName;
            }
        }

        fileNum = tempList.length;
    }

    public static int getAvaThread(){
        int k = -1;
        for(int i=0;i<5;i++){
            if(accordance[i]==0){
                k = i;
            }
        }
        return k;
    }

    public static void addThread(int portNum,int pos){
        accordance[pos] = portNum;
    }

    public static void removeThread(int portNum){
        for(int i=0;i<5;i++){
            if(accordance[i]==portNum){
                accordance[i]=0;
            }
        }
    }

    public static int getThreadNum(int portNum){
        int k = -1;
        for(int i=0;i<5;i++){
            if(accordance[i]==portNum){
               k = i;
            }
        }
        return k;
    }

    public static boolean existFile(String fileName){
        boolean exist = false;
        for(int i=0;i<fileNum;i++){
            if (fileList[i].equals(fileName)){
                exist = true;
            }
        }
        return exist;
    }

    public static peerController getController(){
        return controller;
    }
}
