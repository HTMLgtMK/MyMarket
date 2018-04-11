package beans;

/**
 * 员工信息
 * @author GT
 *
 */
public class AdminstratorInfo {
	private int id;
	private String user_login;//用户登录名
	private String name;//用户实名
	private String mobile;//用户手机号
	private int user_status;//员工状态,0:离职,1:正常,2:未验证 
	private int create_time;
	private int sex;//员工性别,1:男,2:女
	private int birthday;
	private int post_id;
	private String post_name;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUser_login() {
		return user_login;
	}
	public void setUser_login(String user_login) {
		this.user_login = user_login;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public int getUser_status() {
		return user_status;
	}
	public void setUser_status(int user_status) {
		this.user_status = user_status;
	}
	public int getCreate_time() {
		return create_time;
	}
	public void setCreate_time(int create_time) {
		this.create_time = create_time;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getBirthday() {
		return birthday;
	}
	public void setBirthday(int birthday) {
		this.birthday = birthday;
	}
	public int getPost_id() {
		return post_id;
	}
	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}
	public String getPost_name() {
		return post_name;
	}
	public void setPost_name(String post_name) {
		this.post_name = post_name;
	}
	@Override
	public String toString() {
		return "AdminUserInfo [id=" + id + ", user_login=" + user_login + ", name=" + name + ", mobile=" + mobile
				+ ", user_status=" + user_status + ", create_time=" + create_time + ", sex=" + sex + ", birthday="
				+ birthday + ", post_id=" + post_id + ", post_name=" + post_name + "]";
	}
}
