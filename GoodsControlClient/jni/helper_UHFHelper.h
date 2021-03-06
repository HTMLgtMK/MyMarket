/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class helper_UHFHelper */

#ifndef _Included_helper_UHFHelper
#define _Included_helper_UHFHelper
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     helper_UHFHelper
 * Method:    init
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_helper_UHFHelper_init
  (JNIEnv *, jclass);

/*
 * Class:     helper_UHFHelper
 * Method:    autoOpenComPort
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_helper_UHFHelper_autoOpenComPort
  (JNIEnv *, jclass);

/*
 * Class:     helper_UHFHelper
 * Method:    openComPort
 * Signature: (S)I
 */
JNIEXPORT jint JNICALL Java_helper_UHFHelper_openComPort
  (JNIEnv *, jclass, jshort);

/*
 * Class:     helper_UHFHelper
 * Method:    closeComPort
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_helper_UHFHelper_closeComPort
  (JNIEnv *, jclass);

/*
 * Class:     helper_UHFHelper
 * Method:    getUHFReaderInfo
 * Signature: (Lbeans/UHFReaderBean;)I
 */
JNIEXPORT jint JNICALL Java_helper_UHFHelper_getUHFReaderInfo
  (JNIEnv *, jclass, jobject);

/*
 * Class:     helper_UHFHelper
 * Method:    inventory_G2
 * Signature: (Lbeans/InventoryBean;)I
 */
JNIEXPORT jint JNICALL Java_helper_UHFHelper_inventory_1G2
  (JNIEnv *, jclass, jobject);

/*
 * Class:     helper_UHFHelper
 * Method:    writeEPC_G2
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_helper_UHFHelper_writeEPC_1G2
  (JNIEnv *, jclass, jstring);

#ifdef __cplusplus
}
#endif
#endif
