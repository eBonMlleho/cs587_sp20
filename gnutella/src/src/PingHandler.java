import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * every node needs an thread that can keep listening ping request and pong
 * back.
 * 
 * @author Zhanghao
 *
 */
public class PingHandler implements Runnable {

	public String message;
	PrintWriter out;
	int port;
	Socket clientSocket = null;
	DatagramSocket udpSocket;
	int targetPort;
	String filename;
	int TTL;

	PingHandler(int portNUmber, int targetPortNumber, String fileName, int TTLValue) {
		port = portNUmber;
		targetPort = targetPortNumber;
		filename = fileName;
		TTL = TTLValue;
	}

	// This is the player handling code
	public void run() {
		while (true) {
			try {
				udpSocket = new DatagramSocket();
				InetAddress address = InetAddress.getByName("localhost");
				// send UDP packet to host
				byte[] buffer;
				// TTL--;

				String msg = "PING:" + Integer.toString(port) + " " + Integer.toString(TTL) + " " + filename;
				buffer = msg.getBytes();

				DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, targetPort);
				udpSocket.send(request);

				// every 5s ping once
				System.out.println("PING to:" + targetPort + " successfully");
				Thread.sleep(10000); // ping every 5 seconds

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
