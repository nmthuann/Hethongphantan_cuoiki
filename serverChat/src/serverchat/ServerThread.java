/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package com.mycompany.multichat;
package serverchat;
// xu li join nhom chat option 1

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Formatter;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yabok
 */
public class ServerThread extends Thread{
    private Socket socket;
    private DataOutputStream dout;
    private DataInputStream din;
    private User user;
    public String coderoom = new String();
    FileInputStream inFile = null;
    FileOutputStream outFile = null;
    
    String[] welcomeNote = {" đang ở đây.", "đã tham gia!"};
    
    public ServerThread() {
        
    }

    public ServerThread(Socket socket, DataOutputStream dout, DataInputStream din) {
        this.socket = socket;
        this.dout = dout;
        this.din = din;
    }        

    public ServerThread(Socket socket, User user) {
        this.socket = socket;
        this.user = user;
    }

    public ServerThread(Socket socket) {
        this. socket = socket;
    }
    
    
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public DataOutputStream getDout() {
        return dout;
    }

    public void setDout(DataOutputStream dout) {
        this.dout = dout;
    }
    
    @Override
    public void run()
    {
        String content = "";
        File x = new File("log.dat");
        try {
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());

            user = new User();
            
           // user.setMssv(new String(Base64.getDecoder().decode(din.readUTF().trim())));
            //Thread.sleep(100);
            user.setName(new String(Base64.getDecoder().decode(din.readUTF().trim())));
            //Thread.sleep(100);
            //kiem tra chuc nang
            String feature = new String(Base64.getDecoder().decode(din.readUTF().trim()));
            Thread.sleep(100);
            System.out.println(feature);
            // chuc nang join nhom
            if(feature.equals("join")){
            if (Server.getInstance().getGroups().size() < 1)
            { 
                
                String pass = getRandomString();
                Group group = new Group("group1",pass);
                group.getThreads().add(this);
                Server.getInstance().getGroups().add(group);
                dout.writeUTF(Base64.getEncoder().encodeToString("0".getBytes()));
                Thread.sleep(100);
                dout.writeUTF(Base64.getEncoder().encodeToString(group.getPassword().getBytes()));
                coderoom=group.getPassword();
            }
            else {// da co nhom ton tai 
                dout.writeUTF(Base64.getEncoder().encodeToString("1".getBytes()));
                Thread.sleep(1500);
                String code = new String();
                do{
                  code= new String(Base64.getDecoder().decode(din.readUTF().trim()));
                 dout.writeUTF(Base64.getEncoder().encodeToString(findgroupbycode(code).getBytes()));
                 Thread.sleep(100);}
                while(findgroupbycode(code).equals("false"));
                for (Group group : Server.getInstance().getGroups())
                    if(group.getPassword().equals(code)){
                        group.getThreads().add(this);
                        coderoom = group.getPassword();
                        System.out.println(coderoom);
                    }
            
                
                
               
            }}
            if(feature.equals("create")){
           
            String pass = getRandomString();
            Group group = new Group("group1",pass);
            group.getThreads().add(this);
            coderoom = group.getPassword();
            Server.getInstance().getGroups().add(group);
            dout.writeUTF(Base64.getEncoder().encodeToString(group.getPassword().getBytes()));
            }
            
            x = getGroupByCode(coderoom).getFile();
            
            if (x.exists())
            {
                Scanner scan = new Scanner(x);

                while(scan.hasNextLine()) {
                    content = scan.nextLine()+"\r\n";
                    dout.writeUTF(content);
                }                    
                scan.close();
            }
        
        //    dout.writeUTF(content);
        
            Random rand = new Random();
            int n = rand.nextInt();
            if (n < 0 ) n *= -1;            
            n %= welcomeNote.length;
            
//            sendToAllClients("\n ------------> Chào mừng "+ user.getName() + " (MSSV: " + user.getMssv() + ")"+ welcomeNote[n] + "\n");
//            writeLogs("\n ------------> Chào mừng "+ user.getName() + " (MSSV: " + user.getMssv() + ")"+ welcomeNote[n] + "\n");
            sendToAllClients("\n **> " + user.getName() + welcomeNote[n] + "\n");
            writeLogs("\n **> " + user.getName()+ welcomeNote[n] + "\n",coderoom);
            while (true)
            {
                Thread.sleep(200);
                String msg = din.readUTF().trim();
                sendToAllClients(user.getName()+ ": " + msg);
                writeLogs(user.getName()+ ": " + msg + "\n",coderoom);
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            Server.getInstance().getThreads().remove(this);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendToAllClients(String msg) throws IOException
    { 
        Group group = getGroupByCode(coderoom);
        for (ServerThread serverThread : group.getThreads())
                if (!serverThread.equals(this))
                    serverThread.dout.writeUTF(msg);
    }
    
    private void writeLogs(String msg,String fileName) throws IOException
    {
        try {
            BufferedWriter writer = new BufferedWriter
                                        (new OutputStreamWriter(new FileOutputStream(fileName+".dat", true), StandardCharsets.UTF_8));
            writer.write(msg);
            writer.close();
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        
    }
    public static String getRandomString()
    {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String passwd = new String();
        Random rand = new Random();
        int n = rand.nextInt();
        if (n < 0)
                n *= -1;
        n %= 12;
        if (n < 7)
                n += 7;
        while(n >= 0)
        {
                passwd += alphabet.charAt(rand.nextInt(alphabet.length()));
                n--;
        }
        return passwd;
    }

    private String findgroupbycode(String coderoom) {
        for (Group group : Server.getInstance().getGroups())
            if(group.getPassword().equals(coderoom))
                return "true";
            
        return "false";
    }
    private Group getGroupByCode(String code)
    {
        for (Group group : Server.getInstance().getGroups())
            if(group.getPassword().equals(coderoom))
                return group;
        return null;
    }
}
