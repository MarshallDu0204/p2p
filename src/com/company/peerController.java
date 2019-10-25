package com.company;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class peerController {
    private static int pongNum = 0;
    private static int connectNum = 0;
    private static Peer[] pongPeer = new Peer[5];
    private static Peer[] connectedPeer = new Peer[5];
    private static int[] localPort = new int[5];
    private static int[] actualPort = new int[5];
    private static int fPort;
    private static int uPort;
    private static int wPort;
    public static int idNum;
    public static int queryID;
    private static peerController controller;

    public static boolean pongExist(String ip, int portNum){
        for(int i=0;i<connectNum;i++){
            if (pongPeer[i].getIp().equals(ip) && pongPeer[i].getPortNum() == portNum) {
                return true;
            }
        }
        return false;
    }

    public static void addPong(String ip, int portNum){
        if (!pongExist(ip,portNum)) {
            if (portNum < 5) {
                pongPeer[pongNum] = new Peer(ip, portNum);
                pongNum++;
            }
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

    public static void addConnect(String ip,int portNum,int selfPort){
        if(connectNum<5){
            connectedPeer[connectNum] = new Peer(ip,portNum);
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
        Peer[] pong = new Peer[30];
        pongPeer = pong;
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

    public static int getWPort(){
        return wPort;
    }

    public static peerController init(int[] acPort,int welcomePort,int udpPort,int filePort) throws UnknownHostException {
        controller = new peerController();
        actualPort = acPort;
        welcomePort = wPort;
        udpPort = uPort;
        filePort = fPort;

        InetAddress addr = InetAddress.getLocalHost();
        String strAddr = addr.getHostName();
        String[] tempAddr = strAddr.split("-");
        String tempHash = tempAddr[1];

        idNum = Integer.parseInt(tempHash);

        queryID = idNum*100;

        return controller;
    }

    public static int getQueryID() {
        queryID++;
        return queryID-1;
    }

    public static peerController getController(){
        return controller;
    }
}
