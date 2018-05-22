package com.gthncz.mycheckinclient.beans;

/**
 * 用户授权请求数据结构
 * @author GT
 *
 */
public class UserGrantReqBean {
	
	private int id;
	private int user_id;
	private String token;
	private String action;
	private int status;
	private int creat_time;
	private int expire_time;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getCreat_time() {
		return creat_time;
	}
	public void setCreat_time(int creat_time) {
		this.creat_time = creat_time;
	}
	public int getExpire_time() {
		return expire_time;
	}
	public void setExpire_time(int expire_time) {
		this.expire_time = expire_time;
	}
	@Override
	public String toString() {
		return "UserGrantReqBean [id=" + id + ", user_id=" + user_id + ", token=" + token + ", action=" + action
				+ ", status=" + status + ", creat_time=" + creat_time + ", expire_time=" + expire_time + "]";
	}
	
}
