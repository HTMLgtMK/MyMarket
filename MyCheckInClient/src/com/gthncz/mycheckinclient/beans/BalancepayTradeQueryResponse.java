package com.gthncz.mycheckinclient.beans;

/**
 * 余额支付结果查询数据结构
 * @author GT
 *
 */
public class BalancepayTradeQueryResponse {
	private int code;
	private String msg;
	private String outTradeNo;
	private int status;
	private String statusDetail;
	
	public void clear() {
		code = 0;
		msg = null;
		outTradeNo = null;
		status = 0;
		statusDetail = null;
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
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getStatusDetail() {
		return statusDetail;
	}
	public void setStatusDetail(String statusDetail) {
		this.statusDetail = statusDetail;
	}
	@Override
	public String toString() {
		return "BalancepayTradeQueryResponse [code=" + code + ", msg=" + msg + ", outTradeNo=" + outTradeNo
				+ ", status=" + status + ", statusDetail=" + statusDetail + "]";
	}
	
}
