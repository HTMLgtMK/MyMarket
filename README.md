
## 无人超市商品管理模块开发日志

-------------------------------------------------

2018.04.26 10:57

1. 恢复了24日的工作。。。添加了撤销交易的功能
	自助收银系统中，支付界面返回购物车界面需要撤销此次交易。
	
2018.04.26 15:11
	
2. 完成优惠使用提交。
	1. 在接收到优惠信息时，利用`Collections.sort()`方法，对DiscountList排序，这里`DiscountBean`实现了`Comparable`接口。 
	2. 遍历每个商品，判断是否有这类商品的优惠信息，若有，选择符合条件的优惠。
	3. 设置优惠剩余数量减1，设置该商品使用优惠。
	
	优惠信息分成两个部分，即优惠基础信息`(DiscountBaseBean)`和优惠使用信息`(DiscountBean)`, 
	商品中设置的是基础信息，上传给接口的是使用信息.

-------------------------------------------------

2018.04.25 

Git操作不熟悉害死人哪，撤销修改直接返回到了23日的状态。。

1. 添加了优惠功能。每种商品可以有多种优惠，但只能使用一种优惠，程序自动选择一种最优方案。
	最优策略：商品折扣比例+优惠金额 总数最大即为最优。
	
	业务流程: 扫描标签(EPC) --(访问接口)-> 获取商品详情+优惠信息(所有符合条件的优惠)
			->HashMap存储优惠->计算优惠
			
明天还得恢复23日的工作，心痛

-------------------------------------------------

2018.04.24 

1. 添加了撤销交易功能。
	自助收银系统中，支付界面返回购物车界面需要撤销此次交易。

-------------------------------------------------

2018.04.23 21:52

完成了微信支付的全部流程。目前支付宝支付和微信支付均可正常完成流程。

1. 添加了Params类, 用于存放应用中所需要的全部常量。

2018.04.23 16:45

这几天没有做多少内容。主要工作是完成了微信支付。

1. 修改了支付界面逻辑。
	原来的计划是 同时对支付宝和微信请求预下单，但是测试后发现，同时进行预下单会导致预下单请求时间过长。
	现在的逻辑是 单独对支付宝和微信请求预下单，这样可以保证请求时间不会过长！

2. 支付业务流程为:
	1. 提交数据到商户后台，获取交易订单号。
	2. 根据商户订单号，分别对支付宝和微信请求预下单。
	
3. 支付界面主要部分有TabPane组成。
	TabPane的组成有下面几个部分:
	* tab-header-area	 - StackPane
	* headers-region 	 - StackPane
	* tab-header-background - StackPane
	* control-buttons-tab	- StackPane
	* tab-down-button		- Pane
	* arrow					- StackPane
	* tab 					- Tab
	* tab-label				- Label
	* tab-close-button		- StackPane
	* tab-content-area		- StackPane
	
	可对这些部分分别设置style:
	```css
	.tab-header-background{
		-fx-background-color:transparent;
	}
	```

4. FXML中设置样式方法:
	```fxml
	<TabPane fx:id="tabPane_checkin"  tabClosingPolicy="UNAVAILABLE"
			stylesheets="@./style_checkin_pay.css">
	...
	</TabPane>
	```
	`@`后面为相对路径。

	java 中动态设置样式的方法:
	```java
	scene.getStylesheets().add(getClass().getResource("style_checkin_pay.css").toExternalForm());
	```
	
5. TabPane中切换界面，要停止刚才的tabController中的timer。

-------------------------------------------------
2018.04.15 19:39

总结下最近完成的情况。

1. 修改了自助收银界面。修改界面为全屏显示，借鉴网页开发知识，设计自助收银全部的界面，包含在一个ScrollPane中，
	动态设置每个页面的宽度，使的每次只能刚好显示一个界面，然后只需要设置ScrollPane的`HValue`即可。
	注：后面为了安全，应该隐藏滚动条。
	
2. 运用 `<fx:include fx:id="" source="">` 标签，可分别为每个界面设置控制器。
	执行的顺序是 childrenController -> parentController 。
	注意：parentController中若需要使用子控制器，命名方式应为: `<fx:id>`Controller， 否则会反射失败!
	
3. TableView使用时，行数据对象GoodsBean应该含有`get`方法，否则在初始化时不能正确的初始化。
	TableView初始化使用Invoke反射获取`get`方法，这是固定的！
	
4. 扫描速度由原来的2s扫描一次修改为100ms扫描一次，可以获取更高的准确率和速度！
	获取得到的EPC号要要提交到后台获取商品信息，然后再予以显示！
	
5. 两个childController交换数据必须通过parentController。在parentController中设置监听器和代理类进行控制。

6. 完成了支付宝支付接口的调用和数据显示。注意goods_detail数据需要进行Base64编码。
	另外，GoodsDetailList(ArrayList) 转换为 JSONArray 的方法为: 
	`` JSONArray  goodsArr = JSONArray.fromObject(goodsDetailList); ```
	
7. 利用com-google-zxing-core.jar包生成QRCode。
	JavaFX中有WritableImage类，可设置每个像素的颜色。
	但是使用 `setArgb``方法是，注意a r g b 4个通道的颜色要全部给出，给出3个的话，高8位的a默认为0，即全透明，到时候什么也看不到。。
	
-------------------------------------------------

2018.04.11 20:38

1. 创建MyMarket repository

2. 完成了自助收银服务的基础部分
	目前实现了自动扫描标签，将标签EPC显示在表格中。

接下来的任务是:
	1. 点击支付显示支付对话框
	2. 完成自助收银服务接入支付宝二维码，利用支付宝开发SDK。
	3. 扫描支付宝付款后，显示支付成功信息，返回首页继续扫描。
	
下面是遇到的一些问题:

	1. 询查返回标签EPC问题
		java使用对象传递可以在函数中修改对象中值，在InventoryBean中包括了巡查的基本信息。
		在jni中，先getEPClenandEPC() jcharArray数组，再获取EPC中的元素jchar*，
		接下来给jcharArray 赋值，有两种方法赋值：
		
		1. 单个元素赋值
		```C++
		env->SetCharArrayRegion(j_charArray, index, 1, &temp);//index是元素下标,temp是数值
		```
		2. 数组赋值
		```C++
		env->SetCharArrayRegion(j_charArray, index, len, jcharX);//index是元素开始下标, jcharX是jchar*类型
		```
		
	2. 遇到如下问题:
		>#
		># A fatal error has been detected by the Java Runtime Environment:
		>#
		>#  EXCEPTION_ACCESS_VIOLATION (0xc0000005) at pc=0x049ef762, pid=2736, tid=0x000019a0
		>#
		># JRE version: Java(TM) SE Runtime Environment (8.0_171-b11) (build 1.8.0_171-b11)
		># Java VM: Java HotSpot(TM) Client VM (25.171-b11 mixed mode windows-x86 )
		># Problematic frame:
		># j  goods.CheckInBox$1.run()V+143
		>#	
		># Failed to write core dump. Minidumps are not enabled by default on client versions of Windows
		>#
		># If you would like to submit a bug report, please visit:
		>#   http://bugreport.java.com/bugreport/crash.jsp
		>#	
		
		一般情况是jni中语法有问题, 需要仔细检查。
		如果 pc=0x0 , 也可能是传入了空值。
		
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
		