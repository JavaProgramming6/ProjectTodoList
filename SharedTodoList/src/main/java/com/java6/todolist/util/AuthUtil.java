package com.java6.todolist.util;
import org.mindrot.jbcrypt.BCrypt;

public class AuthUtil {
	
	// 비밀번호 해시함수
	public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
	}
	
	// 비밀번호 일치 여부
	public static boolean isMatch(String password, String hashed) {
		return BCrypt.checkpw(password, hashed);
	}
}
