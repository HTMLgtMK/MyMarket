package beans;

/**
 * 商品类别结构
 * @author GT
 *
 */
public class GoodsTypeBean {
	private int id;
	private String name;
	private String images;
	private float price;
	private String address;
	private String company;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImages() {
		return images;
	}
	public void setImages(String images) {
		this.images = images;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	@Override
	public String toString() {
		return "GoodsTypeBean [id=" + id + ", name=" + name + ", images=" + images + ", price=" + price + ", address="
				+ address + ", company=" + company + "]";
	}
}
