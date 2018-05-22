package beans;

/**
 * 微信支付预下单数据结构
 * @author GT
 *
 */
public class WxpayPreCreateResponseBean {
	/**
	 * 必须返回的结果
	 */
	private String return_code;//通信结果码，必须有, SUCCESS/FAIL
	private String return_msg;//通信返回消息，必须有
	
	private String out_trade_no;// 商户自己生成的订单号码
	
	/**
	 *  在return_code为SUCCESS的时候有返回
	 */
	private String appid;//微信服务号id
	private String mch_id;//商户号
	private String device_info;//设备号，自定义参数
	private String nonce_str;//随机字符串
	private String sign;//签名
	private String result_code;//预下单请求返回码, SUCCESS/FAIL
	private String err_code;//错误代码
	private String err_code_des;//错误代码描述
	
	/**
	 * 在return_code和result_code都为SUCCESS的时候有返回
	 */
	private String trade_type;//交易类型
	private String code_url;//二维码链接
	private String prepay_id;//预支付交易会话标识
	
	public enum RETURN_CODE{//通信标识 结果枚举
		SUCCESS,
		FAIL
	};
	
	public enum TRADE_TYPE{//交易类型
		JSAPI,	//公众号支付
		NATIVE,	//扫描支付
		APP		//APP支付
	};
	
	public enum ERR_CODE{//错误码
		NOAUTH,	//商户无此接口权限 	
		NOTENOUGH,	//余额不足
		ORDERPAID,	//商户订单已支付
		ORDERCLOSED, 	//订单已关闭
		SYSTEMERROR,	//系统错误
		APPID_NOT_EXIST, 	//APPID不存在
		MCHID_NOT_EXIS, //MCHID不存在
		APPID_MCHID_NOT_MATCH, //appid和mch_id不匹配
		LACK_PARAMS,	//缺少参数
		OUT_TRADE_NO_USED, //商户订单号重复
		SIGNERROR, //签名错误
		XML_FORMAT_ERROR, //XML格式错误
		REQUIRE_POST_METHOD, //请使用post方法
		POST_DATA_EMPTY, //post数据为空
		NOT_UTF8	//编码格式错误
	}
	
	public void clear() {//清空数据
		this.return_code = "";
		this.return_msg = "";
		this.out_trade_no = "";
		this.appid = "";
		this.mch_id = "";
		this.device_info = "";
		this.nonce_str = "";
		this.sign = "";
		this.result_code = "";
		this.err_code = "";
		this.err_code_des = "";
		this.trade_type = "";
		this.code_url = "";
		this.prepay_id = "";
	}
	
	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getReturn_code() {
		return return_code;
	}

	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}

	public String getReturn_msg() {
		return return_msg;
	}

	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getDevice_info() {
		return device_info;
	}

	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}

	public String getNonce_str() {
		return nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getResult_code() {
		return result_code;
	}

	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}

	public String getErr_code() {
		return err_code;
	}

	public void setErr_code(String err_code) {
		this.err_code = err_code;
	}

	public String getErr_code_des() {
		return err_code_des;
	}

	public void setErr_code_des(String err_code_des) {
		this.err_code_des = err_code_des;
	}

	public String getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}

	public String getCode_url() {
		return code_url;
	}

	public void setCode_url(String code_url) {
		this.code_url = code_url;
	}

	public String getPrepay_id() {
		return prepay_id;
	}

	public void setPrepay_id(String prepay_id) {
		this.prepay_id = prepay_id;
	}

	@Override
	public String toString() {
		return "WxpayResponseBean [return_code=" + return_code + ", return_msg=" + return_msg + ", appid=" + appid
				+ ", mch_id=" + mch_id + ", device_info=" + device_info + ", nonce_str=" + nonce_str + ", sign=" + sign
				+ ", result_code=" + result_code + ", err_code=" + err_code + ", err_code_des=" + err_code_des
				+ ", trade_type=" + trade_type + ", code_url=" + code_url + ", prepay_id=" + prepay_id + "]";
	};
	
}
