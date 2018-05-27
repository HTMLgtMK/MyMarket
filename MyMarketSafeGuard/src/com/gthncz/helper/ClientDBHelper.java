package com.gthncz.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.gthncz.beans.Params;
import com.gthncz.beans.SimpleGoodsBean;

/**
 * 数据库操作
 * 需要提前安装 MariaDB 数据库
 * 
 * @author GT
 *
 */
public class ClientDBHelper {
	private static ClientDBHelper instance;
	private String DB_NAME = null;
	private static final String DEFAULT_DB_NAME = "db_market_safeguard";
	private static final String TB_SIMPLE_GOODS = "tb_simple_goods";
	private String USER = null;
	private static final String DEFAULT_USER = "root";
	private String PWD = null;
	private static final String DEFAULT_PWD = "GT";
	private int PORT = DEFAULT_PORT;
	private static final int DEFAULT_PORT = 3306;
	private Connection connection;
	
	private ClientDBHelper() {};
	
	public static ClientDBHelper getInstance() {
		if(instance == null) {
			instance = new ClientDBHelper();
			initParams();
		}
		return instance;
	}
	
	private static void initParams() {
		HashMap<String, String> ini = INIHelper.getIniSet(Params.INI_NAME);
		instance.DB_NAME = ini.get("db_name");
		instance.USER = ini.get("db_user");
		instance.PWD = ini.get("db_pwd");
		String port = ini.get("db_port");
		instance.DB_NAME = instance.DB_NAME == null ? DEFAULT_DB_NAME : instance.DB_NAME;
		instance.USER = instance.DB_NAME == null ? DEFAULT_USER : instance.USER;
		instance.PWD = instance.DB_NAME == null ? DEFAULT_PWD : instance.PWD;
		instance.PORT = port == null ? DEFAULT_PORT : Integer.valueOf(port);
	}

	/**
	 * 是否已经连接数据库
	 * @return
	 */
	public boolean isConnected() {
		return connection != null;
	}
	
	private void connect() throws SQLException {
		// connect string : jdbc:(mysql|mariadb):[replication:|failover:|sequential:|aurora:]//<hostDescription>[,<hostDescription>...]/[database][?<key1>=<value1>[&<key2>=<value2>]] 
//		String fullUrl = "jdbc:mariadb//localhost:3306/DB?user=root&password=gt";
		String url = "jdbc:mariadb://localhost:" + PORT + "/" +DB_NAME;
		connection = DriverManager.getConnection(url, USER, PWD);
	}
	
	/**
	 * 获取数据连接
	 * @return
	 */
	public Connection getConnection() {
		if(connection == null) {
			try {
				connect();
			} catch (SQLException e) {
				e.printStackTrace();
				// continue ??
			}
		}
		return connection;
	}
	
	/**
	 * 初始化表格
	 * @throws SQLException SQL操作异常, SQL操作失败
	 */
	public void init() throws SQLException {
		Statement statement = getConnection().createStatement();
		/*创建 简单商品表*/
		String sql = "CREATE TABLE IF NOT EXISTS `"+ TB_SIMPLE_GOODS +"` ("
				+ "`epc` char(128) NOT NULL COMMENT '商品EPC号',"
				+ "`name` varchar(128) NOT NULL DEFAULT '' COMMENT '商品名称',"
				+ "`status` tinyint(3) NOT NULL DEFAULT '1' COMMENT '商品状态',"
				+ "`modify_time` int(11) NOT NULL DEFAULT '0' COMMENT '更新时间',"
				+ "PRIMARY KEY(`epc`) "
				+ ") ENGINE=InnoDb DEFAULT CHARSET=utf8mb4 COMMENT '简单商品表';";
		statement.execute(sql);
		statement.close();
	}
	
	/**
	 * 关闭连接
	 */
	public void close() {
		if(connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			connection = null;
		}
	}
	
	/**
	 * 检查某个商品是否已经存在
	 * @param epc
	 * @return
	 * @throws SQLException 
	 */
	public boolean isGoodsExists(String epc) throws SQLException {
		Statement statement = null;
		boolean exists = false;
		statement = getConnection().createStatement();
		String sql = "SELECT COUNT(*) as `count` FROM `"+TB_SIMPLE_GOODS+"` WHERE `epc`= '"+epc+"' LIMIT 1;";
		ResultSet resultSet = statement.executeQuery(sql);
		if(resultSet.wasNull()) {
			exists = false;
		}else {
			exists = resultSet.first();
			int count = 0;
			if(exists) {
				int columnIndex = resultSet.findColumn("count");
				count = resultSet.getInt(columnIndex);
			}
			exists = count == 0 ? false : true;
		}
		statement.close();
		return exists; // if resultSet.first() == true
	}
	
	public SimpleGoodsBean getSimpleGoodsBean(String epc) throws SQLException {
		Statement statement = null;
		statement = getConnection().createStatement();
		String sql = "SELECT * FROM `"+TB_SIMPLE_GOODS+"` WHERE `epc`= '"+epc+"' LIMIT 1;";
		ResultSet resultSet = statement.executeQuery(sql);
		SimpleGoodsBean bean = null;
		if(resultSet.next()) {
			bean = new SimpleGoodsBean();
			int nameColumnIndex = resultSet.findColumn("name");
			int timeColumnIndex = resultSet.findColumn("time");
			int statusColumnIndex = resultSet.findColumn("status");
			bean.setEpc(epc);
			bean.setName(resultSet.getString(nameColumnIndex));
			bean.setTime(resultSet.getLong(timeColumnIndex));
			bean.setStatus(resultSet.getInt(statusColumnIndex));
		}
		statement.close();
		return bean;
	}
	
	public void insertSimpleGoods(SimpleGoodsBean bean) throws SQLException {
		if(bean == null) return ;
		Statement statement = getConnection().createStatement();
		String sql = String.format("INSERT INTO `%s`(`epc`, `name`, `status`, `modify_time`) VALUES('%s', '%s', %d, %d);", 
				TB_SIMPLE_GOODS, bean.getEpc(), bean.getName(), bean.getStatus(), bean.getTime());
		statement.execute(sql);
		statement.close();
	}
	
	public void updateSimpleGoodsBean(SimpleGoodsBean bean) throws SQLException {
		if(bean == null) return ;
		Statement statement = getConnection().createStatement();
		String sql = String.format("UPDATE `%s` SET name='%s', status='%d', modify_time='%d' WHERE epc='%s';", 
				TB_SIMPLE_GOODS, bean.getName(), bean.getStatus(), bean.getTime(), bean.getEpc());
		statement.execute(sql);
		statement.close();
	}
	
}
