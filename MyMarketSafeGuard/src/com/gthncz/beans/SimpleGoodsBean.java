package com.gthncz.beans;

import net.sf.json.JSONObject;

/**
 * 简版商品数据结构
 * @author GT
 *
 */
public class SimpleGoodsBean {
	
	private String epc;
	private long time;
	private int status;
	private String name;
	
	public String getEpc() {
		return epc;
	}
	public void setEpc(String epc) {
		this.epc = epc;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toJSON() {
		JSONObject jsonObj = JSONObject.fromObject(this); // JsonConfig
		return jsonObj.toString();
	}
	
	public static SimpleGoodsBean newInstanceFromJSONObject(JSONObject obj) {
		if(obj == null) return null;
		SimpleGoodsBean bean = new SimpleGoodsBean();
		bean.setEpc(obj.getString("epc"));
		bean.setName(obj.getString("name"));
		bean.setStatus(obj.getInt("status"));
		bean.setTime(obj.getLong("time"));
		return bean;
	}
	
	/**
	 * 本地店铺提交的商品列表
	 * @param obj
	 * @return
	 */
	public static SimpleGoodsBean newInstanceFromLocalStoreJSON(JSONObject obj) {
		if(obj == null) return null;
		SimpleGoodsBean bean = new SimpleGoodsBean();
		bean.setEpc(obj.getString("goods_id"));
		bean.setName(obj.getString("name"));
		bean.setStatus(obj.getInt("status"));
		bean.setTime(System.currentTimeMillis()/1000); // 写入当前时间
		return bean;
	}
	
	@Override
	public String toString() {
		return "SimpleGoodsBean [epc=" + epc + ", time=" + time + ", status=" + status + ", name=" + name + "]";
	}

}
