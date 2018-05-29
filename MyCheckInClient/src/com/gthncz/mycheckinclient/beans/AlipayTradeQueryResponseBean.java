package com.gthncz.mycheckinclient.beans;

public class AlipayTradeQueryResponseBean {
	
	private int code;
	private String status;
	private String out_trade_no;
	
	public final String TRADE_FINISHED = "TRADE_FINISHED";//交易完成
	public final String TRADE_SUCCESS = "TRADE_SUCCESS";//交易成功
	public final String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";//交易创建，等待付款
	public final String TRADE_CLOSED = "TRADE_CLOSED";//交易关闭
	public final String UPDATE_DB_ERR = "UPDATE_DB_ERR";//交易成功,但更新数据库失败
	public final String UNKONE_STATUS = "UNKONE_STATUS";//未知状态
	public final String ACQ_TRADE_NOT_EXIST = "ACQ.TRADE_NOT_EXIST";//未扫描二维码，订单尚未创建
	
	public void clear() {
		code = 0;
		status = null;
		out_trade_no = null;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	@Override
	public String toString() {
		return "AlipayTradeQueryResponseBean [code=" + code + ", status=" + status + ", out_trade_no=" + out_trade_no
				+ "]";
	}
	
}
