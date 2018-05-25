
## 自助收银终端项目开发日志

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
