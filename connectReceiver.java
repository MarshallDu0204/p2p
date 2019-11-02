

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class connectReceiver {

    private static int portNum;

    public connectReceiver(int port){
        portNum = port;
    }

    public int getPortNum(){
        return portNum;
    }

    public void serveConnect() throws IOException {
        ServerSocket welcomeSocket = new ServerSocket(portNum);

        String message = "connect";

        while (true){
            Socket recvSocket = welcomeSocket.accept();

            BufferedReader fromClient = new BufferedReader(new InputStreamReader(recvSocket.getInputStream()));

            DataOutputStream toClient = new DataOutputStream(recvSocket.getOutputStream());

            message = fromClient.readLine();

            peerController controller = peerController.getController();

            String[] tempMessage = message.split(":");

            if (tempMessage[0].equals("{{{requestConnection}}}")){

                String[] connInfo = tempMessage[1].split(",");

                String reqHost = connInfo[0];

                reqHost = reqHost.substring(1,reqHost.length());

                String reqPort = connInfo[1];

                reqPort = reqPort.substring(0,reqPort.length()-1);

                int connPort = Integer.parseInt(reqPort);

                p2pClient client = p2pClient.getClient();

                //call the client to set the connection with set of parameters
                int resPort = peerController.getAvaPort();

                message = "ok:"+resPort+"\n";

                toClient.writeBytes(message);

                peerController.addConnect(reqHost,connPort,resPort);

                client.setConnection(reqHost,connPort,resPort);
            }

            else if (tempMessage[0].equals("{{{check}}}")){
                message = "alive";
                toClient.writeBytes(message);
            }

            else{
                String[] query = tempMessage[1].split(";");

                InetAddress fileAddr = InetAddress.getLocalHost();

                String tempAddr = new String(String.valueOf(fileAddr));

                String[] actuAddr = tempAddr.split("/");

                String acHost = peerController.getPeerAddr();

                int filePort = peerController.getFilePort();

                String strID = query[0];
                String fileName = query[1];

                int queryID = Integer.parseInt(strID);

                if(peerController.inQueryList(queryID)){
                    message = "";

                    toClient.writeBytes(message);
                }
                else{
                    peerController.addQueryNum(queryID);

                    if(peerController.existFile(fileName)){
                        message = "R:"+queryID+";"+acHost+":"+filePort+";"+fileName+"\n";

                        toClient.writeBytes(message);
                    }
                    else{
                        message = "";

                        toClient.writeBytes(message);

                        Peer[] connPeer = peerController.getConnectedPeer();

                        int cont = peerController.getConnNum();

                        String[] hostList = new String[cont];
                        int[] portList = new int[cont];

                        for(int i=0;i<cont;i++){
                            hostList[i] = connPeer[i].getHostName();
                            portList[i] = connPeer[i].getPortNum();
                        }

                        connectSender sender = new connectSender();

                        for (int j = 0; j< cont; j++){
                            String tempResult = sender.queries(hostList[j],fileName,portList[j],0);
                            if(tempResult!=null){
                                message = tempResult;
                                break;
                            }
                        }
                    }
                }

            }

            fromClient.close();
            toClient.close();
            recvSocket.close();
        }
    }

}
