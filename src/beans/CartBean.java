package beans;

public class CartBean {
	private int index;//序号
	private String name;//商品名称
	private int nums;//商品数量
	private float price;//单价
	private String status;//状态
	
	public void numsInc() {
		this.nums++;
	}
	
	
	public void setIndex(int index) {
		this.index = index;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNums(int nums) {
		this.nums = nums;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public int getNums() {
		return nums;
	}

	public float getPrice() {
		return price;
	}

	public String getStatus() {
		return status;
	}
}
