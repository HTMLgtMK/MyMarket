package beans;

public class Params {
	/*服务器地址*/
	public static final String URL_SERVER_BASE = "http://localhost:8888/";
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
}
