package beans;

import java.util.Arrays;

/**
 * UFHReader读写器信息的数据结构
 * @author GT
 *
 */
public class UHFReaderBean {
	private short ComAdr; // 输入/输出变量，远距离读写器的地址。
	private char VersionInfo[]; // 指向输出数组变量（输出的是每字节都转化为字符的数据），远距离读写器版本信息，长度2个字节。第1个字节为版本号，第2个字节为子版本号。
	private short ReaderType; // 输出变量，读写器类型代码,0x09代表UHFREADER18M。
	private short TrType; // 输出变量读写器协议支持信息
	private short dmaxfre; // 输出变量，Bit7-Bit6用于频段设置用；Bit5-Bit0表示当前读写器工作的最大频率
	private short dminfre; // 输出变量, 输出变量, Bit7-Bit6用于频段设置用；Bit5-Bit0表示当前读写器工作的最小频率
	private short powerdBm; //输出变量, 读写器的输出功率。范围是0到18.
	private short ScanTime; // 输出变量，读写器询查命令最大响应时间。
	private int FrmHandle; // 输入变量，返回与读写器连接端口对应的句柄
	
	public UHFReaderBean() {
		VersionInfo = new char[2];
	}

	public short getComAdr() {
		return ComAdr;
	}

	public void setComAdr(short comAdr) {
		ComAdr = comAdr;
	}

	public char[] getVersionInfo() {
		return VersionInfo;
	}

	public void setVersionInfo(char[] versionInfo) {
		VersionInfo = versionInfo;
	}

	public short getReaderType() {
		return ReaderType;
	}

	public void setReaderType(short readerType) {
		ReaderType = readerType;
	}

	public short getTrType() {
		return TrType;
	}

	public void setTrType(short trType) {
		TrType = trType;
	}

	public short getDmaxfre() {
		return dmaxfre;
	}

	public void setDmaxfre(short dmaxfre) {
		this.dmaxfre = dmaxfre;
	}

	public short getDminfre() {
		return dminfre;
	}

	public void setDminfre(short dminfre) {
		this.dminfre = dminfre;
	}

	public short getPowerdBm() {
		return powerdBm;
	}

	public void setPowerdBm(short powerdBm) {
		this.powerdBm = powerdBm;
	}

	public short getScanTime() {
		return ScanTime;
	}

	public void setScanTime(short scanTime) {
		ScanTime = scanTime;
	}

	public int getFrmHandle() {
		return FrmHandle;
	}

	public void setFrmHandle(int frmHandle) {
		FrmHandle = frmHandle;
	}

	@Override
	public String toString() {
		return "UHFReaderBean [ComAdr=" + ComAdr + ", VersionInfo=" + Arrays.toString(VersionInfo) + ", ReaderType="
				+ ReaderType + ", TrType=" + TrType + ", dmaxfre=" + dmaxfre + ", dminfre=" + dminfre + ", powerdBm="
				+ powerdBm + ", ScanTime=" + ScanTime + ", FrmHandle=" + FrmHandle + "]";
	}
}
