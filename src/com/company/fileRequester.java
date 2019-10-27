package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class fileRequester {

    public void requestFile(String fileName,String hostName,int portNum) throws IOException {

        String line;

        InetAddress addr = InetAddress.getByName(hostName);

        Socket sendSocket = new Socket(addr,portNum);

        DataOutputStream toServer = new DataOutputStream(sendSocket.getOutputStream());

        BufferedReader fromServer = new BufferedReader(new InputStreamReader(sendSocket.getInputStream()));

        String queryFile = "T:"+fileName;

        toServer.writeBytes(queryFile);

        String[] message = new String[5000];

        int i = 0;

        while ((line = fromServer.readLine())!=null){//receive the file and store it
            message[i] = fromServer.readLine();
            i++;
        }

        storeFile(fileName,message);

        sendSocket.close();
    }

    public void storeFile(String fileName,String[] message) throws FileNotFoundException, UnsupportedEncodingException {
        String path = "obtained/"+fileName;
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        for (int i=0;i<message.length;i++){
            writer.println(message[i]);
        }
        writer.close();
    }

}
