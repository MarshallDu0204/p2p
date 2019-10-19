package com.company;

public class Peer {
    private String ipAddr;
    private int portNum;

    public Peer(String addr, int port){
        ipAddr = addr;
        portNum = port;
    }

    public String getIp(){
        return ipAddr;
    }

    public int getPortNum(){
        return portNum;
    }
}
