package com.gthncz.beans;

import net.sf.json.JSONObject;

/**
 * 员工信息
 * @author GT
 *
 */
public class AdminstratorInfo {
	private int id;
	private String user_login;//用户登录名
	private String name;//用户实名
	private String mobile;//用户手机号
	private int user_status;//员工状态,0:离职,1:正常,2:未验证 
	private long create_time;
	private int sex;//员工性别,1:男,2:女
	private int birthday;
	private int post_id;
	private String post_name;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUser_login() {
		return user_login;
	}
	public void setUser_login(String user_login) {
		this.user_login = user_login;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public int getUser_status() {
		return user_status;
	}
	public void setUser_status(int user_status) {
		this.user_status = user_status;
	}
	public long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getBirthday() {
		return birthday;
	}
	public void setBirthday(int birthday) {
		this.birthday = birthday;
	}
	public int getPost_id() {
		return post_id;
	}
	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}
	public String getPost_name() {
		return post_name;
	}
	public void setPost_name(String post_name) {
		this.post_name = post_name;
	}
	
	public static AdminstratorInfo newInstanceFromJSONObject(final JSONObject adminObj) {
		final AdminstratorInfo adminInfo = new AdminstratorInfo();
		adminInfo.setBirthday(adminObj.getInt("birthday"));
		adminInfo.setCreate_time(adminObj.getLong("create_time")*1000);
		adminInfo.setId(adminObj.getInt("id"));
		adminInfo.setMobile(adminObj.getString("mobile"));
		adminInfo.setName(adminObj.getString("name"));
		adminInfo.setPost_id(adminObj.getInt("post_id"));
		adminInfo.setPost_name(adminObj.getString("post_name"));
		adminInfo.setSex(adminObj.getInt("sex"));
		adminInfo.setUser_login(adminObj.getString("user_login"));
		adminInfo.setUser_status(adminObj.getInt("user_status"));
		return adminInfo;
	} 
	
	@Override
	public String toString() {
		return "AdminUserInfo [id=" + id + ", user_login=" + user_login + ", name=" + name + ", mobile=" + mobile
				+ ", user_status=" + user_status + ", create_time=" + create_time + ", sex=" + sex + ", birthday="
				+ birthday + ", post_id=" + post_id + ", post_name=" + post_name + "]";
	}
}
