
/*
 * Author: Zhanghao Wen
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class GetLocalOS {
	c_char[] OS = new c_char[16];
	c_char valid = new c_char();
	DataInputStream inStream;
	DataOutputStream outStream;
	Socket clientSocket = null;
	String str = "";

	public int execute(String IP, int port) throws IOException {

		// create a buffer
		int length = OS.length + valid.getSize();
		byte[] lengthToByteArray = toBytes(length);

		byte[] buf = new byte[100 + 4 + length];

		// Marshall parameters into the buffer
		String cmd = this.getClass().getSimpleName();
		byte[] cmd1 = cmd.getBytes();
		for (int i = 0; i < cmd1.length; i++) {
			buf[i] = cmd1[i];
		}

		for (int i = 100; i < lengthToByteArray.length + 100; i++) {
			buf[i] = lengthToByteArray[i - 100];
		}

		// TCP connection
		clientSocket = new Socket(IP, port);
		inStream = new DataInputStream(clientSocket.getInputStream());
		outStream = new DataOutputStream(clientSocket.getOutputStream());
		outStream.write(buf);

		byte[] garbage = new byte[104];
		// ignore the 104 bytes
		inStream.readFully(garbage);

		byte[] bget = new byte[16];
		inStream.readFully(bget);
		byte[] osValid = new byte[1];
		inStream.readFully(osValid);

		for (int i = 0; i < OS.length; i++) {
			OS[i] = new c_char();
			OS[i].setOSValue(bget[i]);
		}

		valid.setValue(osValid);
		str = new String(bget);

		return 0;
	}

	public String getOSText() {
		return str;
	}

	private byte[] toBytes(int i) {
		byte[] result = new byte[4];

		result[0] = (byte) (i >> 24);
		result[1] = (byte) (i >> 16);
		result[2] = (byte) (i >> 8);
		result[3] = (byte) (i /* >> 0 */);

		return result;
	}
}
