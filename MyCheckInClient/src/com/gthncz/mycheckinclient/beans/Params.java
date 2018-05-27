package com.gthncz.mycheckinclient.beans;

public class Params {
	public static final String INI_NAME = "checkinclient.ini";
	/*服务器根目录地址*/
	public static final String URL_SERVER_BASE = "http://localhost:8888/";
	/*本地安保系统服务器地址*/
	public static final String URL_SAFEGUARD_BASE = "localhost";
	/*安保系统端口*/
	public static final int PORT_SAFEGUARD_BASE = 8686;
	/*员工登陆地址*/
	public static final String URL_LOGIN = URL_SERVER_BASE + "api/admin/Public/login";
	/*订单提交地址*/
	public static final String URL_SUBMIT_DEAL = URL_SERVER_BASE + "api/market/Goods_Sale/submit";
	/*支付宝预下单请求地址*/
	public static final String URL_ALIPAY_PRECREATE = URL_SERVER_BASE + "api/market/Goods_Sale/alipay_qrpay";
	/*支付宝支付结果询查地址*/
	public static final String URL_ALIPAY_QUERY = URL_SERVER_BASE + "api/market/Goods_Sale/alipayQuery";
	/*微信支付预下单请求地址*/
	public static final String URL_WXPAY_PRECREATE = URL_SERVER_BASE + "api/market/Goods_Sale/wxpay_qrpay";
	/*微信支付结果询查地址*/
	public static final String URL_WXPAY_QUERY = URL_SERVER_BASE + "api/market/Goods_Sale/wxpayQuery";
	/*用户授权请求地址*/
	public static final String URL_USER_GRANT_REQ = URL_SERVER_BASE + "api/user/User_Grant/grantReq";
	/*询查用户授权状态地址*/
	public static final String URL_GRANT_CHECK_STATUS = URL_SERVER_BASE + "api/user/User_Grant/queryGrantStatus";
	/*关闭用户授权地址*/
	public static final String URL_USER_CLOSE_GRANT = URL_SERVER_BASE + "api/user/User_Grant/closeGrant";
	/*获取商品详情接口地址(带返回优惠信息)*/
	public static final String URL_GETGOODSINFO = URL_SERVER_BASE + "api/market/Goods/getGoodsInfo";
	/*获取商品详情接口地址2*/
	public static final String URL_GETGOODSINFO2 = URL_SERVER_BASE + "api/market/Goods/getGoodsInfo2";
	/*获取优惠地址*/
	public static final String URL_GETDISCOUNTS = URL_SERVER_BASE + "api/market/Goods/getDiscounts";
	/*撤销本次交易接口地址*/
	public static final String URL_REVOKE_DEAL = URL_SERVER_BASE + "api/market/Goods_Sale/revokeDeal";
	/*余额支付预下单请求地址*/
	public static final String URL_BALANCE_PAY_PRECREATE = URL_SERVER_BASE + "api/market/Goods_Sale/balance_qrpay";
	/*余额支付交易请求结果查询地址*/
	public static final String URL_BALANCE_PAY_TRADE_QUERY = URL_SERVER_BASE + "api/market/Goods_Sale/balancePayQuery";
}
