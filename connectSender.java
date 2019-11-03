
import java.net.*;
import java.io.*;

public class connectSender {

    public int requestConnection(String hostName,int portNum,int localPortNum) throws IOException{

        InetAddress ownIP = InetAddress.getLocalHost();

        String tempHost = String.valueOf(ownIP);

        String[] destHost = tempHost.split("/");

        String acHost = peerController.getPeerAddr();

        String message = "{{{requestConnection}}}:("+acHost+","+localPortNum+")\n";

        InetAddress addr = InetAddress.getByName(hostName);

        Socket sendSocket = new Socket(addr,portNum);

        DataOutputStream toServer = new DataOutputStream(sendSocket.getOutputStream());

        BufferedReader fromServer = new BufferedReader(new InputStreamReader(sendSocket.getInputStream()));

        toServer.writeBytes(message);

        message = fromServer.readLine();

        String[] replyMsg = message.split(":");

        int resPort = Integer.parseInt(replyMsg[1]);

        peerController.addConnect(hostName,resPort,localPortNum);

        sendSocket.close();

        return resPort;

    }

    public String queries(String hostName, String fileName,int portNum,int type) throws IOException {//optional enter the hostName if hostName is null then broadcast msg

        int queryID = peerController.getQueryID();

        peerController.addQueryNum(queryID);

        String query = "Q:"+queryID+";"+fileName+"\n";

        if (type!=0){
            Peer[] connPeer = peerController.getConnectedPeer();

            int cont = peerController.getConnNum();

            String[] hostList = new String[cont];
            int[] portList = new int[cont];

            for(int i=0;i<cont;i++){
                hostList[i] = connPeer[i].getHostName();
                portList[i] = connPeer[i].getPortNum();
            }

            for (int j = 0; j< cont; j++){
                String tempResult = queries(hostList[j],fileName,portList[j],0);
                if(tempResult!=null){//do the depth first search, if the first client return the message, break the loop
                    String[] info = tempResult.split(":");
                    String[] tempAddr = info[1].split(";");
                    String[] tempPort = info[2].split(";");

                    String destAddr = tempAddr[1];
                    String strPort = tempPort[0];
                    int destPort = Integer.parseInt(strPort);

                    fileRequester fileReq = new fileRequester();

                    fileReq.requestFile(fileName,destAddr,destPort);

                    break;
                }
            }
            return null;
        }

        InetAddress addr = InetAddress.getByName(hostName);

        Socket sendSocket = new Socket(addr,portNum);

        DataOutputStream toServer = new DataOutputStream(sendSocket.getOutputStream());

        BufferedReader fromServer = new BufferedReader(new InputStreamReader(sendSocket.getInputStream()));

        toServer.writeBytes(query);

        String result = fromServer.readLine();

        sendSocket.close();

        return result;
    }

    public boolean checkAlive(String hostName,int portNum) throws IOException {

        InetAddress addr = InetAddress.getByName(hostName);

        String message = "{{{check}}}:"+hostName+"\n";

        System.out.println("Send Heart Beat to "+hostName);

        Socket sendSocket = new Socket(addr,portNum);

        DataOutputStream toServer = new DataOutputStream(sendSocket.getOutputStream());

        BufferedReader fromServer = new BufferedReader(new InputStreamReader(sendSocket.getInputStream()));

        toServer.writeBytes(message);

        message = fromServer.readLine();

        return true;

    }

}
