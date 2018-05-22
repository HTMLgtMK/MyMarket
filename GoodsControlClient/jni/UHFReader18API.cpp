#include <fstream>
#include "UHFReader18API.h"

namespace MyUHFAPI {
	MyUHF::MyUHF() {
		port = 6;
		ComAdr = 0xFF;
		Baud = 5;
		Frmhandle = 0;
		Mem = 0x01;//EPC存储区
	}
	int MyUHF::init() {
		//初始化读写器
		//1. 初始化dll
		//2. 自动连接读写器
		//3. 自动读取读写器信息
		//4. 初始化其他必要全局变量
		int ret = 0;
		ret = initLib();
		if (ret == 0) {
			ret = func_AutoOpenComPort(&port, &ComAdr, Baud, &Frmhandle);
			if (ret == 0) {
				Password[0] = 0x0; Password[1] = 0x0;
				Password[2] = 0x0; Password[3] = 0x0;
				maskFlag = 0;
				maskadr = 0;
				maskLen = 0;
				ret = func_GetReaderInformation(&ComAdr, // 输入/输出变量，远距离读写器的地址。
					&VersionInfo, // 指向输出数组变量（输出的是每字节都转化为字符的数据），远距离读写器版本信息，长度2个字节。第1个字节为版本号，第2个字节为子版本号。
					&ReaderType, // 输出变量，读写器类型代码,0x09代表UHFREADER18M。
					&TrType, // 输出变量读写器协议支持信息
					&dmaxfre, // 输出变量，Bit7-Bit6用于频段设置用；Bit5-Bit0表示当前读写器工作的最大频率
					&dminfre, // 输出变量, 输出变量, Bit7-Bit6用于频段设置用；Bit5-Bit0表示当前读写器工作的最小频率
					&powerdBm, //输出变量, 读写器的输出功率。范围是0到18.
					&ScanTime, // 输出变量，读写器询查命令最大响应时间。
					Frmhandle);
				if (ret == 0) {
					return 0;
				}
				else {
					return ret;
				}
			}
			else {//if auto connect fail
				return ret;
			}
		}
		else {
			return ret;
		}
	}
	void MyUHF::ecode2EPC(const char* ecode, int len, unsigned char * dst) {
		len /= 2;
		char *s = (char*)malloc(sizeof(char)*2);
		for (int i = 0; i < len; ++i) {
			s[0] = ecode[i * 2]; s[1] = ecode[i*2+1];
			dst[i] = strtoul(s, NULL, 16);
		}
		free(s);
	}
	int MyUHF::initLib() {

		//新建文件测试位置
		/*
		std::ofstream of;
		of.open("test1.dat", std::ios::out | std::ios::app);
		of << "1" << std::endl;
		of.close();
		*/
		HMODULE module = LoadLibrary("UHFReader18.dll");//加载dll文件
		if (module != NULL) {
			if (!(func_AutoOpenComPort = (AutoOpenComPort)GetProcAddress(module, "AutoOpenComPort"))) {
				return -1;
			}
			if (!(func_OpenComPort = (OpenComPort)GetProcAddress(module, "OpenComPort"))) {
				return -2;
			}
			if (!(func_CloseComPort = (CloseComPort)GetProcAddress(module, "CloseComPort"))) {
				return -3;
			}
			if (!(func_GetReaderInformation = (GetReaderInformation)GetProcAddress(module, "GetReaderInformation"))) {
				return -4;
			}
			if (!(func_WriteScanTime = (WriteScanTime)GetProcAddress(module, "WriteScanTime"))) {
				return -5;
			}
			if (!(func_SetWorkMode = (SetWorkMode)GetProcAddress(module, "SetWorkMode"))) {
				return -6;
			}
			if (!(func_GetWorkModeParameter = (GetWorkModeParameter)GetProcAddress(module, "GetWorkModeParameter"))) {
				return -7;
			}
			if (!(func_ReadActiveModeData = (ReadActiveModeData)GetProcAddress(module, "ReadActiveModeData"))) {
				return -8;
			}
			if (!(func_Inventory_G2 = (Inventory_G2)GetProcAddress(module, "Inventory_G2"))) {
				return -9;
			}
			if (!(func_ReadCard_G2 = (ReadCard_G2)GetProcAddress(module, "ReadCard_G2"))) {
				return -10;
			}
			if (!(func_WriteCard_G2 = (WriteCard_G2)GetProcAddress(module, "WriteCard_G2"))) {
				return -11;
			}
			if (!(func_EraseCard_G2 = (EraseCard_G2)GetProcAddress(module, "EraseCard_G2"))) {
				return -12;
			}
			if (!(func_DestroyCard_G2 = (DestroyCard_G2)GetProcAddress(module, "DestroyCard_G2"))) {
				return -13;
			}
			if (!(func_WriteEPC_G2 = (WriteEPC_G2)GetProcAddress(module, "WriteEPC_G2"))) {
				return -14;
			}
			return 0;
		}
		else {
			return GetLastError();
		}
	}
}