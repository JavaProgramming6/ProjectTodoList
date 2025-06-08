package com.java6.todolist;
import java.io.*;
import java.net.*;

import com.java6.todolist.manager.MenuManager;

public class ClientMain {
	
	
	
    public static void main(String[] args) {
        String serverIP = "172.30.1.10"; // 서버 IP
        int port = 8800; //서버 포트

        try (
            Socket socket = new Socket(serverIP, port); //클라이언트 소켓 생성
            BufferedReader in = new BufferedReader(   // 소켓 input(서버->클라) 생성
                new InputStreamReader(socket.getInputStream(), "UTF-8"));
            PrintWriter out = new PrintWriter( // 소켓 output(클라->서버) 생성
                new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            BufferedReader userInput = new BufferedReader( // ~=스캐너
                new InputStreamReader(System.in))
        ) {
            System.out.println("서버에 연결되었습니다.");
            Client client = new Client();

            try{
            	MenuManager.loginMenu(client, in, out, userInput);
            }catch(Exception e){
            	System.out.println(e.getMessage());
            }
            
        } catch (IOException e) {
            System.err.println("클라이언트 오류: " + e.getMessage());
        }
    }
}
