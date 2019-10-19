package com.company;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Base64;

public class testThread {
    public void func1(){
        Thread recvThread = new Thread(){
            public void run(){
                int x = 0;
                while (x!=20){
                    System.out.println("A");
                    x++;
                }
            }
        };
        recvThread.setPriority(Thread.MAX_PRIORITY);
        recvThread.start();
        int i=0;
        while (i!=5){
            System.out.println("B");
            i++;
        }
    }
    public void func2(){
        int j=0;
        while (j!=5){
            System.out.println("C");
            j++;
        }

    }

    public static void main(String[] args) throws UnknownHostException {
        testThread a = new testThread();
        //a.func1();
        //a.func2();
        InetAddress addr = InetAddress.getByName("www.google.com");

        //String host = new String(String.valueOf(addr));

        //String s = Base64.getEncoder().encodeToString(addr.getAddress());
        System.out.println(addr);
    }
}
