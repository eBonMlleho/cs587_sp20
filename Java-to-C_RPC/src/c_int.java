/*
 * Author: Zhanghao Wen
 */
public class c_int {
	byte[] buf = new byte[4]; // little endian

	// the size of buf
	public int getSize() {
		return buf.length;
	}

	// the int value represented by buf
	public int getValue() {
		int result = 0;

		if (buf[0] < 0) {
			result += (buf[0] << 24) & 0xFF000000;
		} else {
			result += buf[0] << 24;
		}

		if (buf[1] < 0) {
			result += (buf[1] << 16) & 0xFF0000;
		} else {
			result += buf[1] << 16;
		}

		if (buf[2] < 0) {
			result += (buf[2] << 8) & 0xFF00;
		} else {
			result += buf[2] << 8;
		}

		if (buf[3] < 0) {
			result += buf[3] & 0xFF;
		} else {
			result += buf[3];
		}
		return result;
	}

	// copy the value in b into buf
	public void setValue(byte[] b) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = b[i];
		}
	}

	// set buf according to v
	public void setValue(int v) {
		buf[0] = (byte) (v >> 24);
		buf[1] = (byte) (v >> 16);
		buf[2] = (byte) (v >> 8);
		buf[3] = (byte) (v /* >> 0 */);
	}

	// return buf
	public byte[] toByte() {
		return buf;
	}

}
