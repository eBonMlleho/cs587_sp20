import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class PongHandler implements Runnable {
	public String message;
	PrintWriter out;
	int targetPort;
	String ip;
	Socket clientSocket = null;
	DatagramSocket udpSocket;
	int localPort;

	PongHandler(int localportNumber, int portNUmber) {
		targetPort = portNUmber;
		localPort = localportNumber;
	}

	// This is the Pong back thread. only run once.
	public void run() {
		// while (true) {
		try {
			udpSocket = new DatagramSocket();
			InetAddress address = InetAddress.getByName("localhost");
			// send UDP packet to host
			byte[] buffer;
			// String msg = "";
			String msg = "PONG:" + Integer.toString(localPort);
			buffer = msg.getBytes();

			DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, targetPort);
			udpSocket.send(request);
			System.out.println("PONG to " + targetPort + " successfully");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }

	}
}
