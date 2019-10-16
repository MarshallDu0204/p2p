package com.company;
import java.io.*;


public class p2pClient implements Runnable{

    private static int connectionPort;

    private static int transferPort;

    private static int discoverPort;

    private static String configPath = "C:/Users/24400/IdeaProjects/p2p/src/com/company/config_peer.txt";

    private void command(String command){

    }

    private void readConfig(String path) throws IOException {
        File file = new File(path);

        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        String[] config = new String[10];
        int i=0;
        while ((line = br.readLine())!=null){
            config[i] = line;
            i++;
        }

        for(i=0;i<3;i++){
            String temp = config[i];
            String[] splitTemp = temp.split(":");
            config[i] = splitTemp[1];
        }

        connectionPort =  Integer.parseInt(config[0]);

        transferPort = Integer.parseInt(config[1]);

        discoverPort = Integer.parseInt(config[2]);

    }

    @Override
    public void run() {

    }


    public static void main(String[] args) throws IOException {
        p2pClient p2p = new p2pClient();
        p2p.readConfig(configPath);

    }


}
