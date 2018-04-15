package beans;

/**
 * 支付宝预下单请求的结果
 * @author GT
 *
 */
public class AlipayPreCreateResponseBean {
	
	private String code;
	private String msg;
	private String subcode;
	private String submsg;
	private String out_trade_no;
	private String qr_code;
	private String sign;
	
	public String getSubcode() {
		return subcode;
	}
	public void setSubcode(String subcode) {
		this.subcode = subcode;
	}
	public String getSubmsg() {
		return submsg;
	}
	public void setSubmsg(String submsg) {
		this.submsg = submsg;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getQr_code() {
		return qr_code;
	}
	public void setQr_code(String qr_code) {
		this.qr_code = qr_code;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "AlipayPreCreateResponseBean [code=" + code + ", msg=" + msg + ", out_trade_no=" + out_trade_no
				+ ", qr_code=" + qr_code + ", sign=" + sign + "]";
	}
	
}
