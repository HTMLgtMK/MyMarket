
## 无人超市商品管理模块开发日志

-------------------------------------------------

2018.04.11 20:38

1. 创建MyMarket repository

-------------------------------------------------

2018.04.10 19:55

1. 完成标签写入功能 
	jni 查看类的方法签名:
	```cmd
	javap -s -classpath .\bin beans.InventoryBean
	```
	获取方法名ID:
	jmethodID ib_setCardNum = env->GetMethodID(clazz, "setCardNum", "(I)V");
	
	jni新建对象
	1. jclass clazz = env->FindClass(env,"className");//获取类
	2. jmethodID construct = env->GetMethodID(clazz, "<init>", "()V");//构造方法函数固定为<init>
	3. jobject obj = env->NewObject(env, clazz, construct);
	
	jni调用类公共方法
	jobject result = env->CallObjectMethod(obj, methodID, args...);
	
-------------------------------------------------

2018.04.10 14:31

目前的进度:
1. 完成员工登陆界面功能
2. 完成商品管理界面布局，完成导航栏功能
3. 完成UHFReader18 接口编写和调用测试

下面是一些注意和总结:
1. jni 开发流程:
	以下命令都在MyMarket根目录下操作。
	1. 编写好native 类
		```java
		package helper;
		public class UHFHelper {
			public static native int init();
			...
			static {
				System.loadLibrary("helper_UHFHelper");
			}
		}
		```
	2. 将native类编译成class字节码文件
		```cmd
		javac .\src\beans\UHFReaderBean.java -encoding utf-8 -d .\bin
		javac .\src\beans\InventoryBean.java -encoding utf-8 -d .\bin
		javac .\src\helper\UHFHelper.java -classpath .\bin -encoding utf-8 -d .\bin
		```
		注：有import自定义类的，要先生成自定义类的
	3. 利用javah工具生成.h头文件
		```cmd
		javah -jni -classpath .\bin -d .\jni helper.UHFHelper
		```
		将生成helper_UHFHelper.h 头文件
	4. 编写helper_UHFHelper.cpp 实现函数功能
	5. 利用VS自带的`CL`命令将cpp编译成`.dll`动态库
		```cmd
		cl -I"%JAVA_HOME%\jdk_8u171_32\include" -I"%JAVA_HOME%\jdk_8u171_32\include\win32" -LD .\jni\helper_UHFHelper.cpp .\jni\UHFReader18API.cpp
		```
		-I: include， 头文件所在的位置
		-LD: 表示编译成DLL
		如果有依赖的头文件，则需要一起编译链接。
		
2. 加载动态库方法
	1. C/C++加载动态库方法:
		```C++
		#include <Windows.h> // 需要引入的头文件
		HMODULE module = LoadLibrary("UHFReader18.dll");//加载动态库
		if(module != NULL) printf("加载成功!");
		else printf("加载失败!");
		```
	2. Java加载动态库方法:
		```java
		//方法一,在java.buid.path里查找
		System.loadLibrary("helper_UHFHelper");
		//方法二,需要写全名
		System.load("E:\MyMarket\helper_UHFHelper.dll");
		```

3. dll平台问题
	给定的 UHFReader18.dll是32bits的。
	查看dll文件的位数可以使用VS自带的dumpbin.exe工具:
	```cmd
	dumpbin /headers .\UHFReader18.dll
	```
	1. Can't load AMD 64-bit .dll on a IA 32-bit platform
		由于jdk是32位的，而 helper_UHFHelper.dll 是64的。
		解决方法: 将helper_UHFHelper.cpp利用32的CL重新编译。
		
	2. C LoadModule() 加载失败
		返回错误码:193 , %1 不是有效的 Win32 应用程序。
		由于jdk和helper_UHFHelper.dll是64位的，而 UHFReader18.dll 是32位的。
		解决方法: 将jdk换成32位，将helper_UHFHelper.cpp利用32的CL重新编译。
		