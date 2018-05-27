#ifndef __UHFREADER18_H__
#define __UHFREADER18_H__

#include <Windows.h>
namespace MyUHFAPI {
	class MyUHF {
	public:
		MyUHF();
		/**
		 * 定义UHFReader18的一些接口函数
		 */
		 /*自动连接串口*/
		typedef long (FAR WINAPI *AutoOpenComPort)(long* port, unsigned char *ComAdr, unsigned char baud, long *Frmhandle);
		/*连接指定串口*/
		typedef long (FAR WINAPI *OpenComPort)(long Port, unsigned char *ComAdr, unsigned char Baud, long *FrmHandle);
		/*关闭串口连接*/
		typedef long (FAR WINAPI *CloseComPort)(void);
		/*获得读写器的信息*/
		typedef long (FAR WINAPI *GetReaderInformation)(unsigned char *ComAdr, // 输入/输出变量，远距离读写器的地址。
			unsigned char *VersionInfo, // 指向输出数组变量（输出的是每字节都转化为字符的数据），远距离读写器版本信息，长度2个字节。第1个字节为版本号，第2个字节为子版本号。
			unsigned char *ReaderType, // 输出变量，读写器类型代码,0x09代表UHFREADER18M。
			unsigned char *TrType, // 输出变量读写器协议支持信息
			unsigned char * dmaxfre, // 输出变量，Bit7-Bit6用于频段设置用；Bit5-Bit0表示当前读写器工作的最大频率
			unsigned char *dminfre, // 输出变量, 输出变量, Bit7-Bit6用于频段设置用；Bit5-Bit0表示当前读写器工作的最小频率
			unsigned char *powerdBm, //输出变量, 读写器的输出功率。范围是0到18.
			unsigned char *ScanTime, // 输出变量，读写器询查命令最大响应时间。
			long FrmHandle); // 输入变量，返回与读写器连接端口对应的句柄
	/*设置询查命令最大响应时间*/
		typedef long (FAR WINAPI *WriteScanTime)(unsigned char *ComAdr, unsigned char *ScanTime, long FrmHandle);
		/*设置工作模式*/
		typedef long (FAR WINAPI *SetWorkMode)(unsigned char *ComAdr, unsigned char * Parameter, long FrmHandle);
		/*读取工作模式参数*/
		typedef long (FAR WINAPI *GetWorkModeParameter)(unsigned char *ComAdr, unsigned char * Parameter, long FrmHandle);
		/*读取主动模式数据*/
		typedef long (FAR WINAPI *ReadActiveModeData)(unsigned char *ActiveModeData, unsigned char * Datalength, long FrmHandle);

		/**
		 * EPCC1-G2 协议函数
		 */
		 /*G2 询查命令*/
		typedef long  (FAR WINAPI *Inventory_G2)(unsigned char *ComAdr, unsigned char AdrTID, unsigned char LenTID, unsigned char TIDFlag, unsigned char *EPClenandEPC, long * Totallen, long *CardNum, long FrmHandle);
		/*G2 读取数据命令*/
		typedef long  (FAR WINAPI *ReadCard_G2)(unsigned char *ComAdr, unsigned char * EPC, unsigned char Mem, unsigned char WordPtr, unsigned char Num, unsigned char * Password, unsigned char maskadr, unsigned char maskLen, unsigned char maskFlag, unsigned char * Data, unsigned char EPClength, unsigned char * errorcode, long FrmHandle);
		/*G2 写命令*/
		typedef long  (FAR WINAPI *WriteCard_G2)(unsigned char *ComAdr, unsigned char * EPC, unsigned char Mem, unsigned char WordPtr, unsigned char Writedatalen, unsigned char * Writedata, unsigned char * Password, unsigned char maskadr, unsigned char maskLen, unsigned char maskFlag, long *WrittenDataNum, unsigned char EPClength, unsigned char * errorcode, long FrmHandle);
		/*G2块擦除命令*/
		typedef long  (FAR WINAPI *EraseCard_G2)(unsigned char *ComAdr, unsigned char * EPC, unsigned char Mem, unsigned char WordPtr, unsigned char Num, unsigned char * Password, unsigned char maskadr, unsigned char maskLen, unsigned char maskFlag, unsigned char EPClength, unsigned char * errorcode, long FrmHandle);
		/*G2销毁标签命令*/
		typedef long  (FAR WINAPI *DestroyCard_G2)(unsigned char *ComAdr, unsigned char * EPC, unsigned char * Password, unsigned char maskadr, unsigned char maskLen, unsigned char maskFlag, unsigned char EPClength, unsigned char * errorcode, long FrmHandle);
		/*G2写EPC号命令*/
		typedef long  (FAR WINAPI *WriteEPC_G2)(unsigned char *ComAdr, unsigned char * Password, unsigned char * WriteEPC, unsigned char WriteEPClen, unsigned char * errorcode, long FrmHandle);

		/*全局函数变量*/
		AutoOpenComPort func_AutoOpenComPort;
		OpenComPort func_OpenComPort;
		CloseComPort func_CloseComPort;
		GetReaderInformation func_GetReaderInformation;
		WriteScanTime func_WriteScanTime;
		SetWorkMode func_SetWorkMode;
		GetWorkModeParameter func_GetWorkModeParameter;
		ReadActiveModeData func_ReadActiveModeData;

		Inventory_G2 func_Inventory_G2;
		ReadCard_G2 func_ReadCard_G2;
		WriteCard_G2 func_WriteCard_G2;
		EraseCard_G2 func_EraseCard_G2;
		DestroyCard_G2 func_DestroyCard_G2;
		WriteEPC_G2 func_WriteEPC_G2;

		//全局变量
		long port;//读写器的端口
		unsigned char ComAdr;//端口地址
		unsigned char Baud;//波特率
		long Frmhandle;//操作句柄
		unsigned char VersionInfo;//读写器版本信息
		unsigned char ReaderType;//读写器类型代码，0x09代表UHFREADER18M
		unsigned char TrType; //读写器协议支持信息
		unsigned char dmaxfre;//最大频率设置
		unsigned char dminfre;//最小频率设置
		unsigned char powerdBm;//读写器的输出功率
		unsigned char ScanTime;//读写器巡查命令的最大响应时间
		unsigned char parameter;//工作模式参数
		unsigned char ActiveModeData[5000];//读取主动模式下读写器发送数据
		unsigned char Datalength;//ActiveModeData的字节大小
		unsigned char AdrTID;//询查TID区的起始地址
		unsigned char LenTID;//询查TID区的数据字数
		unsigned char TIDFlag;//1: 询查TID区， 2: 询查EPC区
		unsigned char EPClenandEPC[5000];//读取到的电子标签的EPC数据
		long Totallen;//EPClenandEPC的字节数
		long CardNum;//电子标签的字节数
		unsigned char EPC[320];//电子标签的EPC号
		unsigned char Mem;//选择要读取的存储区， 0x00: 保留区， 0x01: EPC存储器, 0x02：TID存储器，0x03: 用户存储器
		unsigned char WordPtr;//指定要读取的字起始地址
		unsigned char Num;//要读取的字的个数
		unsigned char Password[4];//4个字节，访问密码
		unsigned char Data[320];//从标签中读取的数据
		unsigned char EPClength;//EPC号的字节长度
		unsigned char errorcode;//读写器返回想用状态为0xFc时，返回错误代码
		unsigned char maskadr;//EPC掩膜起始字节地址
		unsigned char maskLen;//掩膜字节数
		unsigned char maskFlag;//掩膜使能标记, 1:掩膜使能，0: 掩膜禁止
		unsigned char Writedatalen;//待写入的字节数
		unsigned char Writedata;//待写入的字
		long WrittenDataNum;//已经写入的字的个数
		unsigned char WriteEPC;//电子标签写入的EPC号
		unsigned char WriteEPClen;//WriteEPC的长度

		/*初始化读写器*/
		int init();

		/*将char[] 转换成16进制的字节 */
		void ecode2EPC(const char* ecode,int len,unsigned char* dst);

	protected:
		/*初始化库函数，返回 动态加载模块的句柄 */
		int initLib();
	};
}
#endif // !__UHFREADER18_H__