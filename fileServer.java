
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

            BufferedReader file = new BufferedReader(new FileReader(fileName));

            while ((line = file.readLine())!=null){
                message = file.readLine();
                toClient.writeBytes(message);
            }

            toClient.writeBytes(message);

            fromClient.close();
            toClient.close();
            recvSocket.close();
        }
    }



}
