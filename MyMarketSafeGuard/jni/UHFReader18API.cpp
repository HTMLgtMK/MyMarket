#include <fstream>
#include "UHFReader18API.h"

namespace MyUHFAPI {
	MyUHF::MyUHF() {
		port = 6;
		ComAdr = 0xFF;
		Baud = 5;
		Frmhandle = 0;
		Mem = 0x01;//EPC�洢��
	}
	int MyUHF::init() {
		//��ʼ����д��
		//1. ��ʼ��dll
		//2. �Զ����Ӷ�д��
		//3. �Զ���ȡ��д����Ϣ
		//4. ��ʼ��������Ҫȫ�ֱ���
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
				ret = func_GetReaderInformation(&ComAdr, // ����/���������Զ�����д���ĵ�ַ��
					&VersionInfo, // ָ���������������������ÿ�ֽڶ�ת��Ϊ�ַ������ݣ���Զ�����д���汾��Ϣ������2���ֽڡ���1���ֽ�Ϊ�汾�ţ���2���ֽ�Ϊ�Ӱ汾�š�
					&ReaderType, // �����������д�����ʹ���,0x09����UHFREADER18M��
					&TrType, // ���������д��Э��֧����Ϣ
					&dmaxfre, // ���������Bit7-Bit6����Ƶ�������ã�Bit5-Bit0��ʾ��ǰ��д�����������Ƶ��
					&dminfre, // �������, �������, Bit7-Bit6����Ƶ�������ã�Bit5-Bit0��ʾ��ǰ��д����������СƵ��
					&powerdBm, //�������, ��д����������ʡ���Χ��0��18.
					&ScanTime, // �����������д��ѯ�����������Ӧʱ�䡣
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

		//�½��ļ�����λ��
		/*
		std::ofstream of;
		of.open("test1.dat", std::ios::out | std::ios::app);
		of << "1" << std::endl;
		of.close();
		*/
		HMODULE module = LoadLibrary("UHFReader18.dll");//����dll�ļ�
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