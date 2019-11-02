

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class fileServer {
    private static int portNum;

    public fileServer(int port){
        portNum = port;
    }

    public void serveFile() throws IOException {
        ServerSocket welcomeSocket = new ServerSocket(portNum);

        String message = "";

        String line = "";

        while (true){
            Socket recvSocket = welcomeSocket.accept();

            BufferedReader fromClient = new BufferedReader(new InputStreamReader(recvSocket.getInputStream()));

            DataOutputStream toClient = new DataOutputStream(recvSocket.getOutputStream());

            message = fromClient.readLine();

            String[] fileMsg = message.split(":");

            String fileName = fileMsg[1];

            fileName = "shared/"+fileName;

            File file = new File(fileName);

            byte[] bytes = new byte[1024];

            int length = 0;

            FileInputStream fileInputStream = new FileInputStream(file);

            while ((length = fileInputStream.read(bytes,0,bytes.length))!=-1){
                toClient.write(bytes,0,length);
                toClient.flush();
            }


            fromClient.close();
            toClient.close();
            recvSocket.close();
        }
    }



}
