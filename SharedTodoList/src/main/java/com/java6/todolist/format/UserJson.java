package com.java6.todolist.format;
public class UserJson {
	private String id;          //유저 ID(고유값)
	private String password;	//유저 PW
	private String name;		//유저 이름
    
    public UserJson() {};
    
    public UserJson(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
    }
    
    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
    
    public void setPassword(String password) {
    	this.password = password;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
}