package goods;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Main;
import beans.GoodsTypeBean;
import beans.InventoryBean;
import beans.Params;
import helper.DialogUtil;
import helper.NetworkHelper;
import helper.UHFHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import net.sf.json.JSONObject;

public class GoodsAddController implements Initializable {
	private static final String TAG = GoodsAddController.class.getSimpleName();

	@FXML
	private Label label_uhf_status;
	@FXML
	private Button btn_uhf_connect;

	@FXML
	private TextField tf_typename;
	@FXML
	private Button btn_select_goodsType;

	@FXML
	private DatePicker datepicker_goodsdate;
	@FXML
	private TextField tf_goodsbatchnumber;
	@FXML
	private TextField tf_goodsid;
	@FXML
	private ScrollPane scrollpane_info;
	private VBox vbox_info;

	@FXML
	private Button btn_getgoodsId;
	@FXML
	private Button btn_writein;
	@FXML
	private Button btn_test;

	@FXML
	private Button btn_submit;

	private Parent parent;

	private GoodsTypeBean goodsTypeBean;/* 选择的商品类别 */

	private Timer timer;
	private boolean isWriting; // 正在写入的标识
	
	private Timer readRFIDTimer; // 测试RFID的计时器
	private boolean isReading; 

	public static GoodsAddController getInstance() {
		URL location = GoodsAddController.class.getResource("goods_add.fxml");
		FXMLLoader loader = new FXMLLoader(location);
		Parent parent = null;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		GoodsAddController instance = loader.getController();
		instance.parent = parent;
		parent.addEventFilter(WindowEvent.WINDOW_HIDING, new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				if (instance.timer != null) {
					instance.timer.cancel();
				}
				if(instance.readRFIDTimer != null) {
					instance.readRFIDTimer.cancel();
				}
			}
		});
		return instance;
	}

	public Parent getRoot() {
		return parent;
	}

	public GoodsAddController() {
		// !important Don't use this constrcutor to create instance
	}

	private void initViews() {
		// 为scrollPane 添加content
		vbox_info = new VBox();
		vbox_info.setSpacing(10);
		vbox_info.setPadding(new Insets(10));
		scrollpane_info.setContent(vbox_info);

		btn_getgoodsId.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event event) {
				getGoodsId();
			};
		});

		btn_submit.setDisable(true);// 初始时不可提交
		btn_submit.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				submit();
			}
		});

		btn_writein.setDisable(true);// 初始设置写入按钮无效
		btn_writein.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				startWriteIn();
			}
		});
		
		btn_test.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				if(!isReading) {
					startTest(); // Read RFID
				}else {
					stopTest();
				}
			}
		});
		initTestButton();
		
		datepicker_goodsdate.setValue(LocalDate.now());
		btn_select_goodsType.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				selectGoodsType();
			}
		});
		/* 设置面板滚动 */
		vbox_info.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				scrollpane_info.setVvalue(1);
			}
		});
	}
	
	private void initTestButton() {
		btn_test.setDisable(!Main.isUhfConnected());
		if(!isReading) {
			btn_test.setText("测试");
		}else {
			btn_test.setText("停止");
		}
	}
	
	protected void stopTest() {
		if(isReading) {
			if(readRFIDTimer != null) {
				readRFIDTimer.cancel();
			}
			readRFIDTimer = null;
			isReading = false;
		}
		initTestButton();
	}

	private Text readingText;
	private InventoryBean inventoryBean = new InventoryBean();
	
	/** 测试RFID标签 */
	private void startTest() {
		if(isWriting || isReading) return;
		if(readRFIDTimer == null) {
			readRFIDTimer = new Timer();
			readRFIDTimer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					readRFID();
				}
			}, 0, 100);
		}
		isReading = true;
		initTestButton();
		readingText = new Text();
		vbox_info.getChildren().add(readingText);
	}

	

	/** 选择商品类别 */
	private void selectGoodsType() {
		Dialog<GoodsTypeBean> dialog = new Dialog<>();
		GoodsTypeSelectController controller = GoodsTypeSelectController.getInstance();
		Parent parent = controller.getRoot();
		controller.setSelectItem(goodsTypeBean);
		dialog.getDialogPane().setContent(parent);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.NO);
		dialog.setResultConverter(new Callback<ButtonType, GoodsTypeBean>() {

			@Override
			public GoodsTypeBean call(ButtonType param) {
				if (param.equals(ButtonType.OK)) {
					GoodsTypeBean typeBean = controller.getSelectedItem();
					GoodsAddController.this.goodsTypeBean = typeBean;
					if (typeBean != null) {
						tf_typename.setText(typeBean.getName());
					}
					return typeBean;
				}
				return null;
			}
		});
		dialog.show();
		controller.start();
	}

	/* UHFReader 连接按钮点击事件 */
	private class MyUHFConnectionButtonClicked implements EventHandler<Event> {
		@Override
		public void handle(Event event) {
			if (Main.isUhfConnected()) { // intent to 断开连接
				disconnectUHFReader();
			} else { // intent to 连接读写器
				connectUHFReader();
			}
		}
	}

	private MyUHFConnectionButtonClicked myUHFConnectionButtonClicked = new MyUHFConnectionButtonClicked();

	private void initUHFReader() {
		if (Main.isUhfConnected()) {
			label_uhf_status.setText("已连接");
			btn_uhf_connect.setText("断开");
		} else {
			label_uhf_status.setText("未连接");
			btn_uhf_connect.setText("连接");
		}
		btn_uhf_connect.setOnMouseClicked(myUHFConnectionButtonClicked);
	}

	/** 连接UHF读写器 */
	protected void connectUHFReader() {
		showMessage(LEVEL_MSG_INFO, "正在连接读写器...");
		Stage dialog = DialogUtil.getLoadingDialog(null);
		dialog.show();
		(new Thread(new Runnable() {

			@Override
			public void run() {
				final int ret = UHFHelper.init();
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						dialog.close();
						if (ret == 0x00) {
							showMessage(LEVEL_MSG_INFO, "连接读写器成功!");
							Main.setUhfConnected(true);
						} else {
							showMessage(LEVEL_MSG_ERROR,
									String.format("连接失败: %s(0x%02X)", UHFHelper.CODE_MSG_MAP.get(ret), ret));
						}
						// 重新设置状态
						initUHFReader();
						initTestButton();
					}
				});
			}
		})).start();
	}

	/** 断开连接UHF读写器 */
	protected void disconnectUHFReader() {
		showMessage(LEVEL_MSG_INFO, "正在断开连接读写器...");
		Stage dialog = DialogUtil.getLoadingDialog(null);
		dialog.show();
		(new Thread(new Runnable() {

			@Override
			public void run() {
				final int ret = UHFHelper.closeComPort();
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						dialog.close();
						if (ret == 0x00) {
							showMessage(LEVEL_MSG_INFO, "断开连接读写器成功!");
							Main.setUhfConnected(false);
						} else {
							showMessage(LEVEL_MSG_ERROR,
									String.format("断开连接失败: %s(0x%02X)", UHFHelper.CODE_MSG_MAP.get(ret), ret));
						}
						// 重新设置状态
						initUHFReader();
						initTestButton();
					}
				});
			}
		})).start();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initViews();
	}

	public void startWriteIn() {
		if (isWriting || isReading)
			return;
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				writeIn();
			}
		}, 0, 100);
		isWriting = true;
	}

	/**
	 * 写入商品ID到标签
	 */
	private void writeIn() {
		if (!Main.isUhfConnected()) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					showMessage(LEVEL_MSG_WARNING, "UHF读写器尚未连接!");
					if (timer != null) {
						timer.cancel();
						timer = null;
					}
					return;
				}
			});
			isWriting = false;
			return;
		}
		final String goods_id = tf_goodsid.getText();
		final int ret = UHFHelper.writeEPC_G2(goods_id);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (ret == 0) {
					showMessage(LEVEL_MSG_INFO, "写入成功:" + goods_id);
					btn_submit.setDisable(false);
					Logger.getLogger(GoodsAddController.class.getSimpleName()).log(Level.INFO, "写入成功:" + goods_id);
					if (timer != null) {
						timer.cancel();
						timer = null;
					}
					isWriting = false;
				} else {
					showMessage(LEVEL_MSG_ERROR, String.format("写入失败: %s(0x%x)", UHFHelper.CODE_MSG_MAP.get(ret), ret));
				}
			}
		});
	}

	/**
	 * 提交表单逻辑
	 */
	private void submit() {
		// 商品类别id
		int type_id = goodsTypeBean == null ? 0 : goodsTypeBean.getId();
		if (type_id == 0) {
			showMessage(LEVEL_MSG_WARNING, "请先选择商品类别!");
			return;
		}
		// 商品生产时间
		Logger.getLogger(GoodsAddController.class.getSimpleName()).log(Level.INFO,
				datepicker_goodsdate.getValue().toString());
		final Timestamp timestamp = Timestamp.valueOf(datepicker_goodsdate.getValue().atStartOfDay());
		final String batchNumber = tf_goodsbatchnumber.getText();
		if ("".equals(batchNumber)) {
			showMessage(LEVEL_MSG_WARNING, "请输入生产批号!");
			return;
		}
		final String goods_id = tf_goodsid.getText();
		showMessage(LEVEL_MSG_INFO, "提交新商品:" + goods_id);
		(new Thread(new Runnable() {

			@Override
			public void run() {
				String spec = Params.URL_GOODS_SUBMIT;
				HashMap<String, String> map = new HashMap<>();
				map.put("id", goods_id);
				map.put("type_id", String.valueOf(type_id));
				map.put("manufacture_date", String.valueOf(timestamp.getTime() / 1000));
				map.put("batch_number", batchNumber);
				String json = NetworkHelper.downloadString(spec, map, "POST");
				JSONObject jsonObj = JSONObject.fromObject(json);
				final int code = jsonObj.getInt("code");
				final String msg = jsonObj.getString("msg");
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						if (code == 1) {
							tf_goodsid.setText("");// 清空goods_id
							btn_submit.setDisable(true);
							showMessage(LEVEL_MSG_INFO, msg);
						} else {
							showMessage(LEVEL_MSG_ERROR, msg);
						}
					}
				});
			}
		})).start();

	}

	/**
	 * 开始内部加载逻辑
	 */
	public void start() {
		initUHFReader();
		isWriting = false;
	}

	private final int LEVEL_MSG_INFO = 0x01;
	private final int LEVEL_MSG_WARNING = 0x02;
	private final int LEVEL_MSG_ERROR = 0x03;

	private Paint mPaint = null;

	/** 在消息面板上显示信息 */
	private void showMessage(int level, String msg) {
		Text text = new Text(msg);
		switch (level) {
		case LEVEL_MSG_INFO: {
			mPaint = Paint.valueOf("#000000");
			break;
		}
		case LEVEL_MSG_WARNING: {
			mPaint = Paint.valueOf("#FF7F24");
			break;
		}
		case LEVEL_MSG_ERROR: {
			mPaint = Paint.valueOf("#FF0000");
			break;
		}
		default: {
			mPaint = Paint.valueOf("#000000");
			break;
		}
		}
		text.setFill(mPaint);
		vbox_info.getChildren().add(text);
	}

	/**
	 * 获取商品ID
	 */
	private void getGoodsId() {
		int type_id = goodsTypeBean == null ? 0 : goodsTypeBean.getId();
		if (type_id == 0) {
			showMessage(LEVEL_MSG_WARNING, "请先选择商品类别!");
			return;
		}
		showMessage(LEVEL_MSG_INFO, "开始获取商品ID...");
		(new Thread(new Runnable() {

			@Override
			public void run() {
				String spec = Params.URL_GOODS_GETGOODSID;
				HashMap<String, String> map = new HashMap<>();
				map.put("goodsType", String.valueOf(type_id));
				String json = NetworkHelper.downloadString(spec, map, "POST");
				Logger.getLogger(GoodsAddController.class.getSimpleName()).log(Level.INFO, " get json: " + json);

				JSONObject jsonObj = JSONObject.fromObject(json);
				int code = jsonObj.getInt("code");
				final String msg = jsonObj.getString("msg");
				if (code != 1) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							showMessage(LEVEL_MSG_ERROR, msg);
						}
					});
				} else {
					JSONObject data = jsonObj.getJSONObject("data");
					final String id = data.getString("goods_id");
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							showMessage(LEVEL_MSG_INFO, msg + " 商品ID: " + id);

							GoodsAddController.this.tf_goodsid.setText(String.valueOf(id));
							btn_writein.setDisable(false);// 写入按钮有效
						}
					});
				}
			}
		})).start();
	}
	
	/** 读RFID标签, 读到即停止 */
	protected void readRFID() {
		inventoryBean.setTotallen(0);// 置0
		inventoryBean.setCardNum(0);// 标签数置0
		int ret = UHFHelper.inventory_G2(inventoryBean);
		Logger.getLogger(TAG).log(Level.INFO, "** 信息 >> invention return : "
				+ String.format("%s (0x%02x) ", UHFHelper.CODE_MSG_MAP.get(ret), ret));
		/**
		 * 0x01 询查时间结束前返回 0x02 询查时间结束使得询查退出 0x03
		 * 如果读到的标签数量无法在一条消息内传送完，将分多次发送。如果Status为0x0D，则表示这条数据结束后，还有数据。 0x04
		 * 还有电子标签未读取，电子标签数量太多，MCU存储不了 0xFB 无电子标签可操作
		 */
		if ((ret == 0x01) || (ret == 0x02) || (ret == 0x03) || (ret == 0x04)) {// || (ret == 0xFB)
			int m = 1;
			final StringBuilder epcListBuilder = new StringBuilder();
			for (int CardIndex = 0; CardIndex < inventoryBean.getCardNum(); CardIndex++) {
				int EPClen = inventoryBean.getEPClenandEPC()[m - 1];
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < EPClen; ++i) {
					builder.append(
							String.format("%02X", Integer.valueOf(inventoryBean.getEPClenandEPC()[m + i])));
				}
				m = m + EPClen + 1;
				String epc = builder.toString();
				Logger.getLogger(TAG).log(Level.INFO, String.format("%d epc :%s", CardIndex, epc));
				epcListBuilder.append(epc);
				epcListBuilder.append("\r\n");
			}
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					readingText.setText(String.format("扫描成功！\r\n %s", epcListBuilder.toString()));
					readRFIDTimer.cancel(); 
					readRFIDTimer = null;
					isReading = false;
					initTestButton();
				}
			});
		} else if (ret == 0xFB) {
			// NONE
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					readingText.setText(String.format("扫描结果: %s(0x%02X)", UHFHelper.CODE_MSG_MAP.get(ret), ret));
				}
			});
		} else {
			// 出错
			Logger.getLogger(TAG).log(Level.INFO,
					String.format("出错: %s (0x%02x)", UHFHelper.CODE_MSG_MAP.get(ret), ret));
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					readingText.setText(String.format("出错: %s (0x%02x)", UHFHelper.CODE_MSG_MAP.get(ret), ret));
					readRFIDTimer.cancel(); 
					readRFIDTimer = null;
					isReading = false;
					initTestButton();
				}
			});
		}
	}
}
