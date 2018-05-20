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
	/*获取商品详情接口地址(带返回优惠信息)*/
	public static final String URL_GETGOODSINFO = URL_SERVER_BASE + "api/market/Goods/getGoodsInfo";
	/*获取商品详情接口地址2*/
	public static final String URL_GETGOODSINFO2 = URL_SERVER_BASE + "api/market/Goods/getGoodsInfo2";
	/*获取优惠地址*/
	public static final String URL_GETDISCOUNTS = URL_SERVER_BASE + "api/market/Goods/getDiscounts";
	/*撤销本次交易接口地址*/
	public static final String URL_REVOKE_DEAL = URL_SERVER_BASE + "api/market/Goods_Sale/revokeDeal";
	/*商品类型列表地址*/
	public static final String URL_GOODSTYPE_INDEX = URL_SERVER_BASE + "api/market/Goods_Type/index";
	/*商品类型添加地址*/
	public static final String URL_GOODSTYPE_ADD = URL_SERVER_BASE + "api/market/Goods_Type/add";
	/*商品类型添加提交地址*/
	public static final String URL_GOODSTYPE_ADDPOST = URL_SERVER_BASE + "api/market/Goods_Type/addPost";
	/*商品类型编辑地址*/
	public static final String URL_GOODSTYPE_EDIT = URL_SERVER_BASE + "api/market/Goods_Type/edit";
	/*商品类型编辑提交地址*/
	public static final String URL_GOODSTYPE_EDITPOST = URL_SERVER_BASE + "api/market/Goods_Type/editPost";
	/*删除商品类地址*/
	public static final String URL_GOODSTYPE_DELETE = URL_SERVER_BASE + "api/market/Goods_Type/delete";
	/*商品列表地址*/
	public static final String URL_GOODS_INDEX = URL_SERVER_BASE + "api/market/Goods/index";
	/*商品下架地址*/
	public static final String URL_GOODS_DELETE = URL_SERVER_BASE + "api/market/Goods/delete";
	/*商品边界提交地址*/
	public static final String URL_GOODS_EDITPOST = URL_SERVER_BASE + "api/market/Goods/editPost";
	/*添加商品提交地址*/
	public static final String URL_GOODS_SUBMIT = URL_SERVER_BASE + "api/market/Goods/submit";
	/*获取商品ID地址*/
	public static final String URL_GOODS_GETGOODSID = URL_SERVER_BASE + "api/market/Goods/getGoodsId";
	/*用户授权请求地址*/
	public static final String URL_USER_GRANT_REQ = URL_SERVER_BASE + "api/user/User_Grant/grantReq";
	/*询查用户授权状态地址*/
	public static final String URL_GRANT_CHECK_STATUS = URL_SERVER_BASE + "api/user/User_Grant/queryGrantStatus";
	/*关闭用户授权地址*/
	public static final String URL_USER_CLOSE_GRANT = URL_SERVER_BASE + "api/user/User_Grant/closeGrant";
}
