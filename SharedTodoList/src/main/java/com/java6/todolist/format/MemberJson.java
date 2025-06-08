package com.java6.todolist.format;

public class MemberJson {
	private String id;			//멤버의 ID
	private String role;		//멤버의 역할
	private String permission;  //멤버의 권한
    
    public MemberJson() {};
    
    public MemberJson(String id, String role, String permission) {
        this.id = id;
        this.role = role;
        this.permission = permission;
    }
    
    public void setRole(String role) {
    	 this.role = role;
    }
    
    public void setPermission(String permission) {
   	 this.permission = permission;
   }
    
    public String getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getPermission() {
        return permission;
    }
}
