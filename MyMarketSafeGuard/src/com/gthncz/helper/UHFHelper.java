package com.gthncz.helper;

import java.util.HashMap;
import java.util.Map;

import com.gthncz.beans.InventoryBean;
import com.gthncz.beans.UHFReaderBean;

/**
 * UHFReader读写器接口
 * @author GT
 *
 */
public class UHFHelper {
	/**
	 * UHFReader询查返回结果信息映射表
	 */
	public static final Map<Integer, String> CODE_MSG_MAP = new HashMap<Integer, String>() {
		/**
		 * 内部类的序列号
		 */
		private static final long serialVersionUID = 3424570387716145496L;

		{// static region
			put(0x01, "询查时间结束前返回");
			put(0x02, "指定的询查时间溢出");
			put(0x03, "本条消息之后，还有消息");
			put(0x04, "读写模块存储控件已满");
			put(0x05, "访问密码错误");
			put(0x09, "销毁密码错误");
			put(0x0a, "销毁密码不能为全0");
			put(0x0b, "电子标签不支持该命令");
			put(0x0c, "对该命令，访问密码不能为0");
			put(0x0d, "电子标签已经被设置了读保护，不能再次设置");
			put(0x0e, "电子标签没有被设置读保护，不需要解锁");
			put(0x10, "有字节空间被锁定，写入失败");
			put(0x11, "不能锁定");
			put(0x12, "已经锁定，不能再次锁定");
			put(0x13, "参数保存失败,但设置的值在读写模块断电前有效");
			put(0x14, "无法调整");
			put(0x15, "询查时间结束前返回");
			put(0x16, "指定的询查时间溢出");
			put(0x17, "本条消息之后，还有消息");
			put(0x18, "读写模块存储空间已满");
			put(0x19, "电子不支持该命令或者访问密码不能为0");
			put(0xF9, "命令执行出错");
			put(0xFA, "有电子标签，但通信不畅，无法操作");
			put(0xFB, "无电子标签可操作");
			put(0xFC, "电子标签返回错误代码");
			put(0xFD, "命令长度错误");
			put(0xFE, "不合法的命令");
			put(0xFF, "参数错误");
			put(0x30, "通讯错误");
			put(0x31, "CRC校验错误");
			put(0x32, "返回数据长度有错误");
			put(0x33, "通讯繁忙，设备正在执行其他指令");
			put(0x34, " 繁忙，指令正在执行");
			put(0x35, "端口已打开");
			put(0x36, "端口已关闭");
			put(0x37, "无效句柄");
			put(0x38, "无效端口");
			put(0xEE, "返回指令错误");
		}
	};

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
		System.loadLibrary("com_gthncz_helper_UHFHelper");
	}
}