package beans;

public class WxpayOrderQueryResponseBean {
	
	private int code;
	private String status;
	private String out_trade_no;
	
	public final String SUCCES="SUCCES"; //支付成功
	public final String REFUND="REFUND";//转入退款
	public final String NOTPAY="NOTPAY";//未支付
	public final String CLOSED="CLOSED";//已关闭
	public final String REVOKED="REVOKED";//已撤销（刷卡支付）
	public final String USERPAYING="USERPAYING";//用户支付中
	public final String PAYERROR="PAYERROR";//支付失败(其他原因，如银行返回失败)
	
	public final String UPDATE_DB_ERR = "UPDATE_DB_ERR";//更新数据库失败
	public final String UNKONE_STATUS = "UNKONE_STATUS";//未知错误
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
		return "WxpayOrderQueryResponseBean [code=" + code + ", status=" + status + ", out_trade_no=" + out_trade_no
				+ "]";
	}
	
	public void clear() {
		this.code = 0;
		this.out_trade_no = "";
		this.status = "";
	}
	
}
