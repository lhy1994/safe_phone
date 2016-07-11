package com.jlucs.safephone.bean;

public class PasswordInfo {

	private String user;
	private String password;
	private String description;
	
	@Override
	public String toString() {
		return "PasswordInfo [user=" + user + ", password=" + password
				+ ", description=" + description + "]";
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
