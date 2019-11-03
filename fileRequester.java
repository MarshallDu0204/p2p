

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class fileRequester {

    public void requestFile(String fileName,String hostName,int portNum) throws IOException {

        String line;

        System.out.println("Request file to "+hostName);

        InetAddress addr = InetAddress.getByName(hostName);

        Socket sendSocket = new Socket(addr,portNum);

        DataOutputStream toServer = new DataOutputStream(sendSocket.getOutputStream());

        DataInputStream fromServer = new DataInputStream(sendSocket.getInputStream());


        String queryFile = "T:"+fileName+"\n";

        toServer.writeBytes(queryFile);

        fileName = "obtained/"+fileName;

        File file = new File(fileName);

        if(file.exists()){//create the file
            file.delete();
        }
        else {
            file.createNewFile();
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);

        byte[] bytes = new byte[1024];
        int num = 0;
        while ((num = fromServer.read(bytes,0,bytes.length))!=-1){//receive the file and store it
            fileOutputStream.write(bytes,0,num);
            fileOutputStream.flush();
        }

        System.out.println("Transmission Complete!");

        sendSocket.close();
    }

}
