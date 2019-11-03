

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

            if (tempMessage[0].equals("{{{requestConnection}}}")){//message type is request connection return the information and add the connect to the peerController

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

                System.out.println("Accept Peer Connect "+reqHost);
            }

            else if (tempMessage[0].equals("{{{check}}}")){//message type is heart beat
                System.out.println("Receive Heart Beat from "+tempMessage[1]);
                message = "alive";
                toClient.writeBytes(message);
            }

            else{//message type is query
                System.out.println("Receive Query:"+message);

                String[] query = tempMessage[1].split(";");

                InetAddress fileAddr = InetAddress.getLocalHost();

                String tempAddr = new String(String.valueOf(fileAddr));

                String[] actuAddr = tempAddr.split("/");

                String acHost = peerController.getPeerAddr();

                int filePort = peerController.getFilePort();

                String strID = query[0];
                String fileName = query[1];

                int queryID = Integer.parseInt(strID);

                if(peerController.inQueryList(queryID)){//if the query id is in the list, do not flood it forward
                    message = "";

                    toClient.writeBytes(message);
                }
                else{
                    peerController.addQueryNum(queryID);

                    if(peerController.existFile(fileName)){
                        message = "R:"+queryID+";"+acHost+":"+filePort+";"+fileName+"\n";//send the query back to it's previous holder

                        toClient.writeBytes(message);

                        System.out.println("file Exist");
                    }
                    else{//continue forward the message and wait response
                        System.out.println("file Not Exist");

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
