package beans;

public class Params {
	/*服务器地址*/
	public static final String URL_SERVER_BASE = "http://localhost:8888/";
	/*员工登陆地址*/
	public static final String URL_ADMIN_LOGIN = URL_SERVER_BASE + "api/admin/Public/login";
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
}
