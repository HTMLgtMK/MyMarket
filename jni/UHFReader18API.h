#ifndef __UHFREADER18_H__
#define __UHFREADER18_H__

#include <Windows.h>
namespace MyUHFAPI {
	class MyUHF {
	public:
		MyUHF();
		/**
		 * ����UHFReader18��һЩ�ӿں���
		 */
		 /*�Զ����Ӵ���*/
		typedef long (FAR WINAPI *AutoOpenComPort)(long* port, unsigned char *ComAdr, unsigned char baud, long *Frmhandle);
		/*����ָ������*/
		typedef long (FAR WINAPI *OpenComPort)(long Port, unsigned char *ComAdr, unsigned char Baud, long *FrmHandle);
		/*�رմ�������*/
		typedef long (FAR WINAPI *CloseComPort)(void);
		/*��ö�д������Ϣ*/
		typedef long (FAR WINAPI *GetReaderInformation)(unsigned char *ComAdr, // ����/���������Զ�����д���ĵ�ַ��
			unsigned char *VersionInfo, // ָ���������������������ÿ�ֽڶ�ת��Ϊ�ַ������ݣ���Զ�����д���汾��Ϣ������2���ֽڡ���1���ֽ�Ϊ�汾�ţ���2���ֽ�Ϊ�Ӱ汾�š�
			unsigned char *ReaderType, // �����������д�����ʹ���,0x09����UHFREADER18M��
			unsigned char *TrType, // ���������д��Э��֧����Ϣ
			unsigned char * dmaxfre, // ���������Bit7-Bit6����Ƶ�������ã�Bit5-Bit0��ʾ��ǰ��д�����������Ƶ��
			unsigned char *dminfre, // �������, �������, Bit7-Bit6����Ƶ�������ã�Bit5-Bit0��ʾ��ǰ��д����������СƵ��
			unsigned char *powerdBm, //�������, ��д����������ʡ���Χ��0��18.
			unsigned char *ScanTime, // �����������д��ѯ�����������Ӧʱ�䡣
			long FrmHandle); // ����������������д�����Ӷ˿ڶ�Ӧ�ľ��
	/*����ѯ�����������Ӧʱ��*/
		typedef long (FAR WINAPI *WriteScanTime)(unsigned char *ComAdr, unsigned char *ScanTime, long FrmHandle);
		/*���ù���ģʽ*/
		typedef long (FAR WINAPI *SetWorkMode)(unsigned char *ComAdr, unsigned char * Parameter, long FrmHandle);
		/*��ȡ����ģʽ����*/
		typedef long (FAR WINAPI *GetWorkModeParameter)(unsigned char *ComAdr, unsigned char * Parameter, long FrmHandle);
		/*��ȡ����ģʽ����*/
		typedef long (FAR WINAPI *ReadActiveModeData)(unsigned char *ActiveModeData, unsigned char * Datalength, long FrmHandle);

		/**
		 * EPCC1-G2 Э�麯��
		 */
		 /*G2 ѯ������*/
		typedef long  (FAR WINAPI *Inventory_G2)(unsigned char *ComAdr, unsigned char AdrTID, unsigned char LenTID, unsigned char TIDFlag, unsigned char *EPClenandEPC, long * Totallen, long *CardNum, long FrmHandle);
		/*G2 ��ȡ��������*/
		typedef long  (FAR WINAPI *ReadCard_G2)(unsigned char *ComAdr, unsigned char * EPC, unsigned char Mem, unsigned char WordPtr, unsigned char Num, unsigned char * Password, unsigned char maskadr, unsigned char maskLen, unsigned char maskFlag, unsigned char * Data, unsigned char EPClength, unsigned char * errorcode, long FrmHandle);
		/*G2 д����*/
		typedef long  (FAR WINAPI *WriteCard_G2)(unsigned char *ComAdr, unsigned char * EPC, unsigned char Mem, unsigned char WordPtr, unsigned char Writedatalen, unsigned char * Writedata, unsigned char * Password, unsigned char maskadr, unsigned char maskLen, unsigned char maskFlag, long *WrittenDataNum, unsigned char EPClength, unsigned char * errorcode, long FrmHandle);
		/*G2���������*/
		typedef long  (FAR WINAPI *EraseCard_G2)(unsigned char *ComAdr, unsigned char * EPC, unsigned char Mem, unsigned char WordPtr, unsigned char Num, unsigned char * Password, unsigned char maskadr, unsigned char maskLen, unsigned char maskFlag, unsigned char EPClength, unsigned char * errorcode, long FrmHandle);
		/*G2���ٱ�ǩ����*/
		typedef long  (FAR WINAPI *DestroyCard_G2)(unsigned char *ComAdr, unsigned char * EPC, unsigned char * Password, unsigned char maskadr, unsigned char maskLen, unsigned char maskFlag, unsigned char EPClength, unsigned char * errorcode, long FrmHandle);
		/*G2дEPC������*/
		typedef long  (FAR WINAPI *WriteEPC_G2)(unsigned char *ComAdr, unsigned char * Password, unsigned char * WriteEPC, unsigned char WriteEPClen, unsigned char * errorcode, long FrmHandle);

		/*ȫ�ֺ�������*/
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

		//ȫ�ֱ���
		long port;//��д���Ķ˿�
		unsigned char ComAdr;//�˿ڵ�ַ
		unsigned char Baud;//������
		long Frmhandle;//�������
		unsigned char VersionInfo;//��д���汾��Ϣ
		unsigned char ReaderType;//��д�����ʹ��룬0x09����UHFREADER18M
		unsigned char TrType; //��д��Э��֧����Ϣ
		unsigned char dmaxfre;//���Ƶ������
		unsigned char dminfre;//��СƵ������
		unsigned char powerdBm;//��д�����������
		unsigned char ScanTime;//��д��Ѳ������������Ӧʱ��
		unsigned char parameter;//����ģʽ����
		unsigned char ActiveModeData[5000];//��ȡ����ģʽ�¶�д����������
		unsigned char Datalength;//ActiveModeData���ֽڴ�С
		unsigned char AdrTID;//ѯ��TID������ʼ��ַ
		unsigned char LenTID;//ѯ��TID������������
		unsigned char TIDFlag;//1: ѯ��TID���� 2: ѯ��EPC��
		unsigned char EPClenandEPC[5000];//��ȡ���ĵ��ӱ�ǩ��EPC����
		long Totallen;//EPClenandEPC���ֽ���
		long CardNum;//���ӱ�ǩ���ֽ���
		unsigned char EPC[320];//���ӱ�ǩ��EPC��
		unsigned char Mem;//ѡ��Ҫ��ȡ�Ĵ洢���� 0x00: �������� 0x01: EPC�洢��, 0x02��TID�洢����0x03: �û��洢��
		unsigned char WordPtr;//ָ��Ҫ��ȡ������ʼ��ַ
		unsigned char Num;//Ҫ��ȡ���ֵĸ���
		unsigned char Password[4];//4���ֽڣ���������
		unsigned char Data[320];//�ӱ�ǩ�ж�ȡ������
		unsigned char EPClength;//EPC�ŵ��ֽڳ���
		unsigned char errorcode;//��д����������״̬Ϊ0xFcʱ�����ش������
		unsigned char maskadr;//EPC��Ĥ��ʼ�ֽڵ�ַ
		unsigned char maskLen;//��Ĥ�ֽ���
		unsigned char maskFlag;//��Ĥʹ�ܱ��, 1:��Ĥʹ�ܣ�0: ��Ĥ��ֹ
		unsigned char Writedatalen;//��д����ֽ���
		unsigned char Writedata;//��д�����
		long WrittenDataNum;//�Ѿ�д����ֵĸ���
		unsigned char WriteEPC;//���ӱ�ǩд���EPC��
		unsigned char WriteEPClen;//WriteEPC�ĳ���

		/*��ʼ����д��*/
		int init();

		/*��char[] ת����16���Ƶ��ֽ� */
		void ecode2EPC(const char* ecode,int len,unsigned char* dst);

	protected:
		/*��ʼ���⺯�������� ��̬����ģ��ľ�� */
		int initLib();
	};
}
#endif // !__UHFREADER18_H__