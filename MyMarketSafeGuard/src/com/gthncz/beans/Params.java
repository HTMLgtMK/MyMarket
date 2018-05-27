package com.gthncz.beans;

public class Params {
	/*安保系统配置文件名*/
	public static final String INI_NAME = "safeguard.ini";
	/*服务器地址*/
	public static final String URL_SERVER_BASE = "http://localhost:8888/";
	/*员工登陆地址*/
	public static final String URL_ADMIN_LOGIN = URL_SERVER_BASE + "api/admin/Public/login";
	/*安保系统获取商品简单信息*/
	public static final String URL_GET_SIMPLE_GOODS_INFO = URL_SERVER_BASE + "api/market/Goods/getGoodsSimpleInfo";
}
