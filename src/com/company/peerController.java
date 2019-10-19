package com.company;

public class peerController {
    private static int pongNum = 0;
    private static int connectNum = 0;
    private static Peer[] pongPeer = new Peer[30];
    private static Peer[] connectedPeer = new Peer[2];
    private static int[] localPort = new int[2];
    private static peerController controller;

    public void addPong(String ip, int portNum){
        pongPeer[pongNum] = new Peer(ip,portNum);
        pongNum++;
    }

    public void addConnect(String ip,int portNum,int selfPort){
        if(connectNum<2){
            connectedPeer[connectNum] = new Peer(ip,portNum);
            localPort[connectNum] = selfPort;
            connectNum++;
        }
    }

    public void removeConnect(int portNum){
        Peer[] temp = new Peer[2];
        for(int i=0;i<2;i++){
            if (connectedPeer[i].getPortNum()!=portNum){
                temp[0] = connectedPeer[i];
            }
        }
        connectedPeer = temp;
    }

    public void clearPong(){
        Peer[] pong = new Peer[30];
        pongPeer = pong;
    }

    public int getConnNum(){
        return connectNum;
    }

    public int getIdlePort(){
        if (connectNum == 1){
            return localPort[1];
        }
        else{
            return 0;
        }
    }

    public Peer[] getPeer() {
        Peer[] result = new Peer[2];
        if (pongNum == 0) {
            result[0] = pongPeer[0];
            return result;
        } else {
            result[0] = pongPeer[0];
            result[1] = pongPeer[1];
            return result;
        }
    }

    public Peer[] getConnectedPeer(){
        return connectedPeer;
    }

    public static peerController init(){
        controller = new peerController();
        return controller;
    }

    public static peerController getController(){
        return controller;
    }
}
