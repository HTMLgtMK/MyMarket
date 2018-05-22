package beans;

/**
 * 会员优惠数据结构
 * @author GT
 *
 */
public class DiscountUserBean {
	protected int id;
	protected int discount_id;
//	private int user_id; // 不需要保存
	protected int count;
	protected int rest;
	protected long create_time;
	
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
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getRest() {
		return rest;
	}
	public void setRest(int rest) {
		this.rest = rest;
	}
	public long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}
	@Override
	public String toString() {
		return "DiscountUserBean [id=" + id + ", discount_id=" + discount_id + ", count=" + count + ", rest=" + rest
				+ ", create_time=" + create_time + "]";
	}
	
}
