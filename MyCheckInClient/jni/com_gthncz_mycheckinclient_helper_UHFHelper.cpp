#include "com_gthncz_mycheckinclient_helper_UHFHelper.h"
#include "UHFReader18API.h"
#include <stdio.h>
using namespace MyUHFAPI;

#ifdef __cplusplus
extern "C" {
#endif
	
	/*Global variable*/
	MyUHF uhf;

	JNIEXPORT jint JNICALL Java_com_gthncz_mycheckinclient_helper_UHFHelper_init(JNIEnv *env, jclass clazz) {
		return uhf.init();
	}

	JNIEXPORT jint JNICALL Java_com_gthncz_mycheckinclient_helper_UHFHelper_autoOpenComPort(JNIEnv *env, jclass helper) {
		return uhf.func_AutoOpenComPort(&(uhf.port), &(uhf.ComAdr), uhf.Baud, &(uhf.Frmhandle));
	}
	
	JNIEXPORT jint JNICALL Java_com_gthncz_mycheckinclient_helper_UHFHelper_openComPort(JNIEnv *env, jclass helper, jshort mPort) {
		uhf.port = mPort;
		uhf.Baud = 5;
		uhf.ComAdr = 0xFF;
		return uhf.func_OpenComPort(uhf.port, &(uhf.ComAdr), uhf.Baud, &(uhf.Frmhandle));
	}

	JNIEXPORT jint JNICALL Java_com_gthncz_mycheckinclient_helper_UHFHelper_closeComPort(JNIEnv *env, jclass clazz) {
		return uhf.func_CloseComPort();
	}

	JNIEXPORT jint JNICALL Java_com_gthncz_mycheckinclient_helper_UHFHelper_getUHFReaderInfo(JNIEnv *env, jclass helper, jobject uhfReaderInfoObj) {
		return 0;
	}
	JNIEXPORT jint JNICALL Java_com_gthncz_mycheckinclient_helper_UHFHelper_inventory_1G2(JNIEnv *env, jclass helper, jobject beanObj){
		jclass clazz = env->GetObjectClass(beanObj);//获取obj中的对象
		jmethodID ib_setEPClenandEPC = env->GetMethodID(clazz, "setEPClenandEPC", "([C)V");
		jmethodID ib_setTotallen = env->GetMethodID(clazz, "setTotallen", "(I)V");
		jmethodID ib_setCardNum = env->GetMethodID(clazz, "setCardNum", "(I)V");
		jmethodID ib_setErrorcode = env->GetMethodID(clazz, "setErrorcode", "(C)V");
		jmethodID ib_getEPClenandEPC = env->GetMethodID(clazz, "getEPClenandEPC", "()[C");
		//询查标签
		uhf.AdrTID = 0;
		uhf.TIDFlag = 0;//1 -- TID区 ， 0 - EPC区
		uhf.LenTID = 0;
		int ret = uhf.func_Inventory_G2(&uhf.ComAdr, uhf.AdrTID, uhf.LenTID, uhf.TIDFlag, uhf.EPClenandEPC, &uhf.Totallen, &uhf.CardNum, uhf.Frmhandle);
		if ((ret == 1) || (ret == 2) || (ret == 3) || (ret == 4) || (ret == 0xFB)) {
			jboolean isCopy;
			jcharArray j_charArr = (jcharArray) env->CallObjectMethod(beanObj, ib_getEPClenandEPC);//j_charArr != NULL
			jchar* epc = env->GetCharArrayElements(j_charArr, &isCopy);
			printf("epc in jni");
			for (int i = 0; i < uhf.Totallen; ++i) {
				epc[i] = (jchar)uhf.EPClenandEPC[i];
				printf("%02x",epc[i]);
			}
			printf("\n");
			env->SetCharArrayRegion( j_charArr, 0, 5000, epc);
			env->CallObjectMethod(beanObj, ib_setEPClenandEPC, j_charArr);
			env->CallObjectMethod(beanObj, ib_setTotallen, uhf.Totallen);
			env->CallObjectMethod(beanObj, ib_setCardNum, uhf.CardNum);
			env->CallObjectMethod(beanObj, ib_setErrorcode, 0);
		}
		return ret;
	}

	JNIEXPORT jint JNICALL Java_com_gthncz_mycheckinclient_helper_UHFHelper_writeEPC_1G2(JNIEnv *env, jclass helper, jstring jstr_encode) {
		jboolean isCopy;
		const char* epc = env->GetStringUTFChars( jstr_encode, &isCopy);
		uhf.ecode2EPC(epc, 32, uhf.EPC);
		uhf.EPClength = 16;//16进制字节数
		int ret = uhf.func_WriteEPC_G2(&(uhf.ComAdr), uhf.Password,
			uhf.EPC, uhf.EPClength, &(uhf.errorcode), uhf.Frmhandle);
		return ret;
	}

#ifdef __cplusplus
}
#endif