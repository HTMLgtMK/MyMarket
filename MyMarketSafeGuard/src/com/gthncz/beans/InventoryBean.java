package com.gthncz.beans;

import java.util.Arrays;

/**
 * 询查标签的信息结构
 * @author GT
 *
 */
public class InventoryBean {
	private char EPClenandEPC[];
	private int Totallen;
	private int CardNum;
	private char errorcode;
	public InventoryBean() {
		EPClenandEPC = new char[5000];
		CardNum = 0;
		Totallen = 0;
		errorcode = 0;
	}
	
	public char[] getEPClenandEPC() {
		return EPClenandEPC;
	}
	public void setEPClenandEPC(char[] ePClenandEPC) {
		EPClenandEPC = ePClenandEPC;
	}
	public int getTotallen() {
		return Totallen;
	}
	public void setTotallen(int totallen) {
		Totallen = totallen;
	}
	public int getCardNum() {
		return CardNum;
	}
	public void setCardNum(int cardNum) {
		CardNum = cardNum;
	}
	public char getErrorcode() {
		return errorcode;
	}
	public void setErrorcode(char errorcode) {
		this.errorcode = errorcode;
	}
	@Override
	public String toString() {
		return "InventoryBean [EPClenandEPC=" + Arrays.toString(EPClenandEPC) + ", Totallen=" + Totallen + ", CardNum="
				+ CardNum + ", errorcode=" + errorcode + "]";
	}
}
