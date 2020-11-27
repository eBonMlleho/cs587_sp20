/*
 * Author: Zhanghao Wen
 */
public class c_char {
	byte[] buf = new byte[1];

	// the size of buf
	public int getSize() {
		return buf.length;

	}

	// the int value represented by buf
	public int getValue() {
		return buf[0];
	}

	public char getChar() {
		return (char) buf[0];
	}

	public void setValue(boolean boo) {
		if (boo) {
			buf[0] = 1;
		} else {
			buf[0] = 0;
		}
	}

	public void setValue(byte[] b) {
		buf[0] = (byte) Integer.parseInt(new String(b));
	}

	public void setOSValue(byte b) {
		buf[0] = b;
	}

}
