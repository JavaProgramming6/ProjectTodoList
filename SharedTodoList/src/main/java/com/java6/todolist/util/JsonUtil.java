package com.java6.todolist.util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.java6.todolist.format.ResultData;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class JsonUtil {

	//저장할 json 포맷(보기 좋게 설정)
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    //경로별 락 저장소
    private static final Map<String, Object> fileLocks = new ConcurrentHashMap<>();

    //경로별 락 객체 반환 (없으면 새로 생성)
    private static Object getLock(String path) {
        return fileLocks.computeIfAbsent(path, k -> new Object());
    }

    //파일 저장
    public static <T> boolean saveToJson(String path, T data) {
        synchronized (getLock(path)) {
            File file = new File(path);

            // 상위 디렉토리 생성
            if (!file.getParentFile().exists()) {
                boolean dirsCreated = file.getParentFile().mkdirs();
                if (!dirsCreated) {
                	JsonUtil.logWrite("ERROR" , "saveToJson", "system", "디렉토리 생성 실패: " + file.getParent());
                    System.err.println("디렉토리 생성 실패: " + file.getParent());
                    return false;
                }
            }

            //파일 생성
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(data, writer);
                return true;
            } catch (IOException e) {
            	JsonUtil.logWrite("ERROR" , "saveToJson", "system", "파일 저장 실패: " + e.getMessage());
                System.err.println("파일 저장 실패: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
    }

    //파일 읽기
    public static <T> T loadFromJson(String path, Type type) {
        synchronized (getLock(path)) {
            File file = new File(path);
            if (!file.exists()) {
            	JsonUtil.logWrite("ERROR" , "loadFromJson", "system", "파일이 존재하지 않습니다: " + path);
                System.err.println("파일이 존재하지 않습니다: " + path);
                return null;
            }

            try (FileReader reader = new FileReader(file)) {
                return gson.fromJson(reader, type);
            } catch (IOException e) {
            	JsonUtil.logWrite("ERROR" , "loadFromJson", "system", "파일 읽기 실패: " + e.getMessage());
                System.err.println("파일 읽기 실패: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
    }
    
    
    //파일 생성
    public static boolean createFile(String path) {
    	synchronized (getLock(path)) {
    		File file = new File(path);

            // 상위 디렉토리 생성
    		if (!file.getParentFile().exists()) {
                boolean dirsCreated = file.getParentFile().mkdirs();
                if (!dirsCreated) {
                	JsonUtil.logWrite("ERROR" , "createFile", "system", "디렉토리 생성 실패: " + file.getParent());
                    System.err.println("디렉토리 생성 실패: " + file.getParent());
                    return false;
                }
            }
            
            try {
            	//파일 존재 여부
                if (file.exists()) {
                    return  file.isFile();
                }

                //파일 생성
                boolean created = file.createNewFile();
                if (!created) {
                	JsonUtil.logWrite("ERROR" , "createFile", "system", "파일 생성 실패: " + file.getAbsolutePath());
                    System.err.println("파일 생성 실패: " + file.getAbsolutePath());
                }
                return created;
            } catch (IOException e) {
            	JsonUtil.logWrite("ERROR" , "createFile", "system", "파일 생성 중 오류: " + e.getMessage());
                System.err.println("파일 생성 중 오류: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
    }
    
    //폴더 생성
    public static boolean createDirectory(String path) {
        synchronized (getLock(path)) {
            File directory = new File(path);
            
            //폴더 존재 여부
            if (directory.exists()) {
                return directory.isDirectory();
            }

            boolean created = directory.mkdirs();
            if (!created) {
            	JsonUtil.logWrite("ERROR" , "createDirectory", "system", "디렉토리 생성 실패: " + directory.getAbsolutePath());
                System.err.println("디렉토리 생성 실패: " + directory.getAbsolutePath());
            }
            return created;
        }
    }
    
    
    
    //폴더 삭제(내부데이터 포함)
    public static boolean deleteDirectory(String path) {
        synchronized (getLock(path)) {
            File directory = new File(path);
            if (!directory.exists()) {
            	JsonUtil.logWrite("ERROR" , "deleteDirectory", "system", "삭제할 디렉토리가 존재하지 않습니다: " + path);
                System.err.println("삭제할 디렉토리가 존재하지 않습니다: " + path);
                return false;
            }

            return deleteRecursively(directory);
        }
    }

    //하위 파일/폴더도 같이 삭제
    private static boolean deleteRecursively(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                boolean success = deleteRecursively(f);
                if (!success) return false;
            }
        }
        return file.delete();
    }
    
    public static void logWrite(String level, String event, String id, String message) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatted = LocalDateTime.now().format(formatter);
    	String log = "[" + formatted + "]" + " [" + level + "] " + id + " - " + event + " - " + message + "\n";  
    	try (FileWriter writer = new FileWriter(FilePathsUtil.SERVERLOG_PATH, true)) { // true -> append 모드
            writer.write(log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //JSON 파싱
    public static <T> T parseToDto(JsonObject data, Class<T> format){
    	return gson.fromJson(data, format);
    }

    //명령 성공여부 JSON 생성
    public static <T> String success(boolean result, T data) {
    	Gson gson = new Gson();
        ResultData<T> response = new ResultData<>(result, data);
        return gson.toJson(response);
    }
}
