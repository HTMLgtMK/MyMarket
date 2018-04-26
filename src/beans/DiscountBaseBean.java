package beans;

/**
 * 优惠基础数据结构
 * @author GT
 *
 */
public class DiscountBaseBean {
	/*基础信息*/
	protected int id;
	protected int discount_id;
	protected int goods_type_id;
	protected String name;
	protected float extent;
	protected int coin;
	protected int rest;
	
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
