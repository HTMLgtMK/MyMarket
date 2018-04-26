package beans;

/**
 * 优惠使用数据结构
 * @author GT
 *
 */
public class DiscountBean extends DiscountBaseBean implements Comparable<DiscountBean>{
	
	/*使用信息*/
	protected int use;//使用数量
	
	/**
	 * 使用一份该优惠
	 * @return
	 */
	public boolean inc() {
		if(rest > use) {//仍然有可用优惠
			++use;
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 计算优惠值
	 * @param price 商品价格
	 * @param count 商品数量
	 * @return
	 */
	public int calculateDiscount(int price , int count) {
		int ext_dis = (int) (price*count* (1-getExtent()));
		return ext_dis + -coin;
	}
	
	
	@Override
	public String toString() {
		return "DiscountBean [use=" + use + ", id=" + id + ", discount_id=" + discount_id + ", goods_type_id="
				+ goods_type_id + ", name=" + name + ", extent=" + extent + ", coin=" + coin + ", rest=" + rest + "]";
	}


	/**
	 * 实现该接口用于ArrayList排序
	 */
	@Override
	public int compareTo(DiscountBean obj) {
		if(this.extent != obj.extent) {
			return this.extent > obj.getExtent() ? 1 : -1;
		}else {
			return -(this.coin - obj.getCoin());
		}
	}
}
