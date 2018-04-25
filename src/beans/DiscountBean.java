package beans;

/**
 * 优惠数据结构
 * @author GT
 *
 */
public class DiscountBean {
	
	private int id;
	private int discount_id;
	private int goods_type_id;
	private String name;
	private float extent;
	private int coin;
	private int rest;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDiscount_id() {
		return discount_id;
	}
	public void setDiscount_id(int discount_id) {
		this.discount_id = discount_id;
	}
	public int getGoods_type_id() {
		return goods_type_id;
	}
	public void setGoods_type_id(int goods_type_id) {
		this.goods_type_id = goods_type_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getExtent() {
		return extent;
	}
	public void setExtent(float extent) {
		this.extent = extent;
	}
	public int getCoin() {
		return coin;
	}
	public void setCoin(int coin) {
		this.coin = coin;
	}
	public int getRest() {
		return rest;
	}
	public void setRest(int rest) {
		this.rest = rest;
	}
	@Override
	public String toString() {
		return "DiscountBean [id=" + id + ", discount_id=" + discount_id + ", goods_type_id=" + goods_type_id
				+ ", name=" + name + ", extent=" + extent + ", coin=" + coin + ", rest=" + rest + "]";
	}
	
}
