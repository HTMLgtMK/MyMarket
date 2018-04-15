package helper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * 二维码相关帮助类
 * @author GT
 *
 */
public class QRCodeHelper {
	
	private static final int BLACK = 0xFF000000;//注意顺序: a, r,g,b 每个颜色8bit
	private static final int WHITE = 0xFFFFFFFF;

	/**
	 * ZXing方式生成二维码
	 * @param text 二维码内容
	 * @param width 二维码宽
	 * @param height 二维码高
	 * @return Image 返回图片对象
	 */
	public static Image zxingQRCodeCreate(String text,int width, int height) {
		
		HashMap<EncodeHintType, String> his = new HashMap<>();
		//设置编码字符集
		his.put(EncodeHintType.CHARACTER_SET, "utf-8");
		
		try {
			// 1. 生成二维码
			BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height,his);
			
			// 2. 获取二维码宽高
			int matrixWidth = bitMatrix.getWidth();
			int matrixHeight = bitMatrix.getHeight();
			
			// 3. 建立图像缓冲
			WritableImage writableImage = new WritableImage(matrixWidth, matrixHeight);
			PixelWriter writer = writableImage.getPixelWriter();
			for(int i=0;i<matrixHeight;++i) {
				for(int j=0;j<matrixWidth;++j) {
					writer.setArgb(i, j, bitMatrix.get(i, j) ? BLACK: WHITE);
				}
			}
			return writableImage;
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
