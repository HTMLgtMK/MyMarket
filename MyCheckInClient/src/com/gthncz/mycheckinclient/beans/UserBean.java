package com.gthncz.mycheckinclient.beans;

import java.util.ArrayList;

/**
 * 会员数据结构
 * Created by GT on 2018/5/5.
 */

public class UserBean {
    private long id;
    private String name;
    private String mobile;
    private String user_pass;
    private int user_status;
    private String user_login;
    private String user_email;
    private String last_login_ip;
    private int last_login_time;
    private String user_activation_key;
    private int create_time;
    private int point;
    private int balance;
    private String user_nickname;
    private String avatar;
    private int sex;
    private int birthday;
    private long user_level ;
    private String more;
    
    private ArrayList<DiscountUseBean> discounts;
    
    public UserBean(){}
    
	public UserBean(long id, String name, String user_login, int point, int balance, String user_nickname,
			String avatar, long user_level) {
		super();
		this.id = id;
		this.name = name;
		this.user_login = user_login;
		this.point = point;
		this.balance = balance;
		this.user_nickname = user_nickname;
		this.avatar = avatar;
		this.user_level = user_level;
		this.discounts = new ArrayList<>();
	}
	public UserBean(long id, String name, String mobile, String user_pass, int user_status, String user_login,
			String user_email, String last_login_ip, int last_login_time, String user_activation_key, int create_time,
			int point, int balance, String user_nickname, String avatar, int sex, int birthday, long user_level,
			String more) {
		super();
		this.id = id;
		this.name = name;
		this.mobile = mobile;
		this.user_pass = user_pass;
		this.user_status = user_status;
		this.user_login = user_login;
		this.user_email = user_email;
		this.last_login_ip = last_login_ip;
		this.last_login_time = last_login_time;
		this.user_activation_key = user_activation_key;
		this.create_time = create_time;
		this.point = point;
		this.balance = balance;
		this.user_nickname = user_nickname;
		this.avatar = avatar;
		this.sex = sex;
		this.birthday = birthday;
		this.user_level = user_level;
		this.more = more;
		this.discounts = new ArrayList<>();
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public String getUser_pass() {
		return user_pass;
	}
	public void setUser_pass(String user_pass) {
		this.user_pass = user_pass;
	}
	public int getUser_status() {
		return user_status;
	}
	public void setUser_status(int user_status) {
		this.user_status = user_status;
	}
	public String getUser_login() {
		return user_login;
	}
	public void setUser_login(String user_login) {
		this.user_login = user_login;
	}
	public String getUser_email() {
		return user_email;
	}
	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}
	public String getLast_login_ip() {
		return last_login_ip;
	}
	public void setLast_login_ip(String last_login_ip) {
		this.last_login_ip = last_login_ip;
	}
	public int getLast_login_time() {
		return last_login_time;
	}
	public void setLast_login_time(int last_login_time) {
		this.last_login_time = last_login_time;
	}
	public String getUser_activation_key() {
		return user_activation_key;
	}
	public void setUser_activation_key(String user_activation_key) {
		this.user_activation_key = user_activation_key;
	}
	public int getCreate_time() {
		return create_time;
	}
	public void setCreate_time(int create_time) {
		this.create_time = create_time;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public String getUser_nickname() {
		return user_nickname;
	}
	public void setUser_nickname(String user_nickname) {
		this.user_nickname = user_nickname;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
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
	public long getUser_level() {
		return user_level;
	}
	public void setUser_level(long user_level) {
		this.user_level = user_level;
	}
	public String getMore() {
		return more;
	}
	public void setMore(String more) {
		this.more = more;
	}
	
	public ArrayList<DiscountUseBean> getDiscounts() {
		return discounts;
	}

	public void setDiscounts(ArrayList<DiscountUseBean> discounts) {
		this.discounts = discounts;
	}

	@Override
	public String toString() {
		return "UserBean [id=" + id + ", name=" + name + ", mobile=" + mobile + ", user_pass=" + user_pass
				+ ", user_status=" + user_status + ", user_login=" + user_login + ", user_email=" + user_email
				+ ", last_login_ip=" + last_login_ip + ", last_login_time=" + last_login_time + ", user_activation_key="
				+ user_activation_key + ", create_time=" + create_time + ", point=" + point + ", balance=" + balance
				+ ", user_nickname=" + user_nickname + ", avatar=" + avatar + ", sex=" + sex + ", birthday=" + birthday
				+ ", user_level=" + user_level + ", more=" + more + ", discounts=" + discounts + "]";
	}
    
}

