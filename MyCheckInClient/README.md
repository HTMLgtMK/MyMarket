
## 自助收银终端项目开发日志

/**
 *                             _ooOoo_
 *                            o8888888o
 *                            88" . "88
 *                            (| -_- |)
 *                            O\  =  /O
 *                         ____/`---'\____
 *                       .'  \\|     |//  `.
 *                      /  \\|||  :  |||//  \
 *                     /  _||||| -:- |||||-  \
 *                     |   | \\\  -  /// |   |
 *                     | \_|  ''\---/''  |   |
 *                     \  .-\__  `-`  ___/-. /
 *                   ___`. .'  /--.--\  `. . __
 *                ."" '<  `.___\_<|>_/___.'  >'"".
 *               | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *               \  \ `-.   \_ __\ /__ _/   .-` /  /
 *          ======`-.____`-.___\_____/___.-`____.-'======
 *                             `=---='
 *          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *                     佛祖保佑        永无BUG
*/

-------------------------------------------------

2018.05.29 10:30

1. 修改支付界面显示二维码逻辑，多次使用收银系统，若下次交易申请二维码失败，
	则应该清除上次显示的二维码。

2. 修改了`NetworkHelper`的上传部分，直接使用`OutputStreamWriter`，并指定文字编码，
	否则上传中文会出现问题，导致服务器端`json_decode()`出现`MalUTF8Error`.
	```java
	OutputStream os = connection.getOutputStream();
	OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8"); // 使用这个, 指定输出数据的编码
	writer.write(builder.toString());
	writer.flush();
	writer.close();
	os.close();
	```
	本来还以为是Eclipse导出的jar包有问题，原来是上传数据的方式有问题。
	
3. 修改上传购买商品`Base64`编码过程, 不适用`encodeToString()`方法, 该方法生成的字符串编码类型为`ISO-8859-1`,
	还是先转换成byte数组，然后在转换成`UTF-8`的字符串方法比较好。
	```java
	byte[] bytes = Base64.getEncoder().encode(goods_detail.getBytes()); // !important, 使用这个, encodeToString()使用的编码是  ISO-8859-1.
	goods_detail = new String(bytes, StandardCharsets.UTF_8); // ! important
	```
	
4. 运行jar包时，windows系统默认的JVM编码是`GBK`，而程序内部使用的编码全部已经修改成`UTF-8`了.
	运行jar包需要在命令行下，指定 `-Dfile.encoding=UTF-8`，才能正确运行。</br>
	获取JVM编码的方法:
	```java
	Charset charset = Charset.defaultCharset();
	Logger.getLogger(Main.class.getSimpleName()).log(Level.INFO, "default charset :" +charset);
	```
	运行jar包的方法:(一定要是cmd, 不是PowerShell)
	```cmd
	> java -Dfile.encoding=UTF-8 -jar .\MyMarketClient.jar
	```
	
5. 当完成一交易马上经行下一交易，而下交易的金额为0时，出现仍然显示上一次订单信息的问题。
	1. 修改`showQrCode()`函数逻辑，当二维码请求失败时，
		```java
		imageView_qrcode.setImage(null);
		```
	2. 修改购物车清空信息函数
		```java
		public void clearGoods(){
			... 其他
			totalPrice = 0;
			payPrice = 0;
			discountPrice = 0;
		}
		```

-------------------------------------------------

2018.05.25 23:05

1. 添加了配置读写, 用于存储店铺ID和终端ID。
	配置使用`checkini.ini`存储，每一行按照`key`=`value`格式存储.
	
2. 生成了可执行文件, 注: jar必须放在开发目录下, 否则会出现资源找不到的情况。

3. 打包后的执行文件获取联网数据出现中文乱码，原来在开发时执行文件所用的编码是Eclipse设置的编码(utf-8),
	而打包后执行文件获取数据的编码是系统的编码(GBK), 因此在获取网络数据时需要指定数据编码格式为`UTF-8`.
	修改`NetworkHelper`如下部分(感谢用了这个帮助类, 不然又要一个个修改了:))
	```java
	InputStreamReader is = new InputStreamReader(connection.getInputStream(), "UTF-8");// 必须指定编码, 否则接收的数据乱码
	```
	
	到这里，自助收银就算全部完成了!!!
	**结业大吉!!!**
	
-------------------------------------------------

2018.05.22

1. 拆分原`MyMarket`项目为两个项目: 
	1. 自助收银客户端, 只有一个功能: 收银。
	2. 无人超市商品管理客户端, 用于添加商品类别和添加商品。
	
	两个部分都需要用到UHFReader.由于两个UHFHelper的包名不同，因此重新生成了dll文件。

2. 修改了会员登陆界面，学习了`ImageView`在FXML文件中直接设置图片的方法:
	```fxml
	<ImageView styleClass="form-label">
		<image>
			<Image url="file:resource/drawable/password.png"
				backgroundLoading="true" />
		</image>
	</ImageView>
	```

3. Eclipse修改默认文件编码的方法(一劳永逸):
	在Eclipse的安装目录下，找到配置文件`eclipse.ini`, 在文件中的最后一行添加配置:
	```ini
	; ... 其它属性配置
	-Dfile.encoding=UTF-8 ; 设置文件编码为UTF-8格式
	```
	然后重启Eclipse即可。
