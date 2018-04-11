package helper;

import java.util.ArrayList;

import beans.InventoryBean;
import beans.UHFReaderBean;

/**
 * UHFReader读写器接口
 * @author GT
 *
 */
public class UHFHelper {

	/**
	 * 初始化DLL库，并自动连接读写器
	 * @return 返回0表示初始化成功, 否则返回error code
	 */
	public static native int init();
	
	/**
	 * 自动打开端口
	 * @return 返回0表示初始化成功, 否则返回error code
	 */
	public static native int autoOpenComPort();
	
	/**
	 * 指定端口打开读写器
	 * @param port 指定端口
	 * @return 返回0表示初始化成功, 否则返回error code
	 */
	public static native int openComPort(short port);
	
	/**
	 * 关闭端口
	 * @return 关闭状态
	 */
	public static native int closeComPort();
	
	/**
	 * 获取读写器信息
	 * @param bean 输出变量，获取UHFReader info
	 * @return
	 */
	public static native int getUHFReaderInfo(UHFReaderBean bean);
	
	/**
	 * 询查标签
	 * @param bean 传入询查参数，用于传出
	 * @return 返回询查结果，具体参照手册
	 */
	public static native int inventory_G2(InventoryBean bean);
	
	/**
	 * 写入标签EPC号
	 * @param ecode Ecode编码
	 * @return 返回写入结果, 0: 成功， 其它为失败，具体查看手册
	 */
	public static native int writeEPC_G2(String ecode);
	
	static {
		System.loadLibrary("helper_UHFHelper");
	}
}