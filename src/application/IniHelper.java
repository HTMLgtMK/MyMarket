package application;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * 配置帮助类
 * @author GT
 *
 */
public class IniHelper {
	
	private static IniHelper mInstance;
	
	private IniHelper() {}
	
	public static IniHelper getInstance() {
		if(mInstance == null) {
			mInstance = new IniHelper();
		}
		return mInstance;
	}
	
	public void putValue(String filePath, String key,String value) throws IOException, ParserConfigurationException, SAXException {
		File file = new File(filePath);
		if(!file.exists()) {
			file.createNewFile();
		}
		DocumentBuilderFactory factor = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factor.newDocumentBuilder();
		Document doc = builder.parse(file);
		Element element = doc.createElement(key);
		element.setTextContent(value);
	}
	
	public String getValue(String filePath, String key) throws ParserConfigurationException, SAXException, IOException {
		File file = new File(filePath);
		if(!file.exists()) {
			return "";
		}
		DocumentBuilderFactory factor = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factor.newDocumentBuilder();
		Document doc = builder.parse(file);
		Node node = doc.getElementsByTagName(key).item(0);
		return node.getTextContent();
	}
}
