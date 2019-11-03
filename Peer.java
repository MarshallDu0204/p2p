

public class Peer {
    private String hostName;
    private int portNum;

    public Peer(String hostAddr, int port){
        hostName = hostAddr;
        portNum = port;
    }

    public String getHostName(){
        return hostName;
    }

    public int getPortNum(){
        return portNum;
    }
}
