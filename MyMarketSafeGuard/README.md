
## 无人超时安保系统开发日志

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

安保系统主要功能是检测未付款商品被带出店铺。
检测时需要连接读写器使用, 检测到有标签经过时, 
查找本地服务器有没有提交记录, 再提交商家后台查询是否已经购买.

安保系统开启了一个店铺销售商品记录服务, 自助收银系统在检测到支付完成后,
向店铺安保系统提交购买记录, 这样顾客出店铺时, 检测到已经购买的商品可以被过滤,
减小商家后台压力。

下面是开发中遇到的几个问题：

1. `ServerSocket.accept()` 方法阻塞当前进程, 当外界干预需要停止监听端口时, 由于被阻塞而不能被停止, 
	直到下一个`Request`请求到达。</br>
	解决方法:
	设置 `ServerSocket.setSoTimeout(long millionSecond)` , 该方法在`accept()`阻塞进程指定时间后，
	可以使得继续运行(抛出Socket Exception).
	```java
	ServerSocket serverSocket = null;
	try {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(10000); // 10s自动退出阻塞向后运行
	} catch (IOException e) {
		e.printStackTrace();
		// return
		listenerFlag = false;
	}
	while(listenerFlag) { // 不断接收请求
		Socket socket = null;
		try {
			socket = serverSocket.accept();
		} catch (IOException e) {
			//e.printStackTrace(); // TODO 不输出异常
			continue; // 跳过
		}
		// 接收数据, 生成任务, 发送到处理线程
		if(socket == null) continue;
		handleSocket(socket);
	}
	```
	
2. 使用了`ThreadPool`, 由于店铺中可能有多个自助收银系统, 为减少创建和销毁线程已经不过多的创建线程, 使用了线程池。
	```java
	private ExecutorService executorService;
	executorService = Executors.newFixedThreadPool(nThreads); // 固定线程数量的线程池 maxThreadNum = coreThreadNum
	```
	
3. 多线程使用检查队列(Queue)出现`java.util.ConcurrentModificationException`异常,
	在使用前必须先申请信号量，使用完后释放信号量。
