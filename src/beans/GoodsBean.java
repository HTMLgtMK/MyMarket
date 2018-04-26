package beans;

public class GoodsBean extends GoodsTypeBean {
	
	/*基础信息*/
	private String goods_id;
	private int type_id;
	private long  manufacture_date;
	private String batch_number;
	private int status;//状态, 1:待售, 2:已售
	
	/*使用信息*/
	
	//优惠基础信息
	private DiscountBaseBean discount;
	
	public void setDiscount(DiscountBean discountBean) {
		discount = discountBean;
	}
	
	public DiscountBaseBean getDiscount() {
		return discount;
	}
	
	
	public String getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}
	public int getType_id() {
		return type_id;
	}
	public void setType_id(int type_id) {
		this.type_id = type_id;
	}
	public long getManufacture_date() {
		return manufacture_date;
	}
	public void setManufacture_date(long manufacture_date) {
		this.manufacture_date = manufacture_date;
	}
	public String getBatch_number() {
		return batch_number;
	}
	public void setBatch_number(String batch_number) {
		this.batch_number = batch_number;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "GoodsBean [goods_id=" + goods_id + ", type_id=" + type_id + ", manufacture_date=" + manufacture_date
				+ ", batch_number=" + batch_number + ", status=" + status + ", discount=" + discount + "]";
	}
	
}
