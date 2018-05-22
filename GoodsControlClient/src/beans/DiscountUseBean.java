package beans;

/**
 * 优惠使用数据结构
 * @author GT
 *
 */
public class DiscountUseBean extends DiscountUserBean{
	
	/*使用信息*/
	protected int use;//使用数量
	
	public boolean inc() {
		if( getRest() - use > 0) {
			++use;
			return true;
		}else {
			return false;
		}
	}
	
}
