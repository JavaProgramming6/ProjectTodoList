package com.java6.todolist;
import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.function.BiConsumer;
import com.java6.todolist.util.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ClientHandler implements Runnable {
        private final Socket socket;
        private final Map<String, BiConsumer<JsonObject, PrintWriter>> dispatcher;
        
        //소켓 서버로부터 접속 성공하면 작동
        public ClientHandler(Socket socket, Map<String, BiConsumer<JsonObject, PrintWriter>> dispatcher) {
            this.socket = socket;
            this.dispatcher = dispatcher;
        }

        
        //쓰레드
        public void run() {
            String clientAddress = socket.getInetAddress().toString();
            System.out.println("클라이언트 연결됨: " + clientAddress);
            JsonUtil.logWrite("INFO", "main", clientAddress , "클라이언트 접속");
            
            try (
                BufferedReader in = new BufferedReader( //클라->서버 인풋
                    new InputStreamReader(socket.getInputStream(), "UTF-8"));
                PrintWriter out = new PrintWriter( //서버->클라 아웃풋
                    new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true)
            ) {
	            String line;
	            while ((line = in.readLine()) != null) { //클라로부터 명령어 받음
	                JsonObject request = JsonParser.parseString(line).getAsJsonObject(); //파싱
	                String command = request.get("command").getAsString();
	                JsonObject data = request.getAsJsonObject("data");
	                
	                BiConsumer<JsonObject, PrintWriter> handler = dispatcher.get(command);
	                if (handler != null) {//존재하는 명령어면 실행
	                    handler.accept(data, out);
	                } else {
	                	out.println(JsonUtil.success(false, "알 수 없는 명령어입니다."));
	                }
	            }
            } catch (IOException e) {
                System.err.println("[" + clientAddress + "] 연결 중 오류: " + e.getMessage());
                JsonUtil.logWrite("ERROR", "main", clientAddress , "클라이언트 연결 오류 : " + e.getMessage());
            } finally { //통신 끝
                try {
                    socket.close();
                    System.out.println("[" + clientAddress + "] 연결 종료");
                    JsonUtil.logWrite("INFO", "main", clientAddress , "클라이언트 종료");
                } catch (IOException e) {
                    System.err.println("소켓 종료 실패: " + e.getMessage());
                    JsonUtil.logWrite("ERROR", "main", clientAddress , "클라이언트 종료 오류 : " + e.getMessage());
                }
            }
        }
    }
