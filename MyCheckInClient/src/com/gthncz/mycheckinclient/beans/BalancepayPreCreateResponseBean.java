package com.gthncz.mycheckinclient.beans;

/**
 * 余额支付预下单请求结果数据结构
 * @author GT
 *
 */
public class BalancepayPreCreateResponseBean {
	// 预下单成功时返回的值
	private String id;
	private String userId;
	private String outTradeNo;
	private String token;
	// 必须返回的值
	private int code;
	private String msg;
	
	public void clear() {
		id = null;
		userId = null;
		outTradeNo = null;
		token = null;
		code = 0;
		msg = null;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	@Override
	public String toString() {
		return "BalancepayPreCreateResponseBean [id=" + id + ", userId=" + userId + ", outTradeNo=" + outTradeNo
				+ ", token=" + token + ", code=" + code + ", msg=" + msg + "]";
	}
	
}
