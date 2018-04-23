## 待办事项

1. 接入支付宝,[扫码支付](https://docs.open.alipay.com/194) 
	使用步骤：
	1. 收银员在商家收银系统操作生成支付宝订单，并生成二维码；
	2. 用户登录支付宝钱包，点击首页“付款-扫码付”或直接点击“扫一扫”，进入扫一扫界面；
	3. 用户扫收银员提供的二维码，核对金额，确认支付；
	4. 用户付款后商家收银系统会拿到支付成功或者失败的结果。
	[扫码支付接入指引](https://docs.open.alipay.com/194/106078)
	
2. 提交交易信息到后台，由后台创建订单，并返回支付二维码和支付结果

3. 使用fx:include标签时，Controller之间初始化的顺序: childController -> ParentController
	> log:
	信息: treeView Clicked: 自助收银
	四月 12, 2018 6:55:45 下午 checkin.CheckInControl start
	信息: start
	四月 12, 2018 6:55:45 下午 checkin.CheckInWelcomeControl initialize
	信息: initialize width:1920.0 height:1080.0
	四月 12, 2018 6:55:45 下午 checkin.CheckInControl initialize
	信息: initialize

4. Controller变量的命名必须是<fx:id>Controller格式, 否则无法正常工作！
	```xml
	<!-- in main.fxml -->
	<fx:include fx:id="page1" source="checkin_welcome.fxml" />
	```
	```java
	@FXML
	private CheckInWelcomeControl page1Controller;
	```
	Reference: [JavaFx Nested Controllers (FXML <include>)](https://stackoverflow.com/questions/12543487/javafx-nested-controllers-fxml-include)
	
5. 修改退出当前交易支付时，需要修改后台数据库中订单的状态!