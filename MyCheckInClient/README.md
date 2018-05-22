
## 自助收银终端项目开发日志

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
