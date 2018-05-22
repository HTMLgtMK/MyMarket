package com.gthncz.mycheckinclient.beans;

public class CartBean {
	public static final int STATUS_SALING = 0X01;
	public static final int STATUS_SALED = 0X02;
	public static final int STATUS_LOCKED = 0X03;
	public static final int STATUS_UNKONW = 0X04;
	
	private int index;//序号
	private String name;//商品名称
	private int nums;//商品数量
	private float price;//单价
	private String status;//状态
	
	private int statusCode; // 当前状态码
	
	public int getStatusCode() {
		return statusCode;
	}


	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
		switch (statusCode) {
		case STATUS_SALING: {
			setStatus("待售");
			break;
		}
		case STATUS_SALED: {
			setStatus("已售");
			break;
		}
		case STATUS_LOCKED: {
			setStatus("锁定");
			break;
		}
		default: {
			setStatus("未知");
		}
		}
	}


	public void numsInc() {
		this.nums++;
	}
	
	
	public void setIndex(int index) {
		this.index = index;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNums(int nums) {
		this.nums = nums;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public int getNums() {
		return nums;
	}

	public float getPrice() {
		return price;
	}

	public String getStatus() {
		return status;
	}
}
