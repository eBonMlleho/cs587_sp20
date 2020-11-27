import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class player2 {

	public static void main(String[] args) throws IOException {

		// LOCAL FILE SETUP
		List<String> AvailableFileLists = new ArrayList<String>();
		AvailableFileLists.add("player2.txt");

		// USER INPUT COMMAND INTERATION
		UserInteraction userHandler = new UserInteraction(AvailableFileLists);
		Thread u = new Thread(userHandler);
		u.start();

		// create a new UDP socket to receive packets. host port is at 6666.
		DatagramSocket udpSocket = new DatagramSocket(6666);

		// KEEP TRACK OF ALL PEERS
		List<Integer> KnownNodelist = new ArrayList<Integer>();

		// CHECK TIME OUT
		Hashtable<Integer, Integer> nodesTable = new Hashtable<Integer, Integer>();
		CheckfFunction checkHealth = new CheckfFunction(nodesTable);
		Thread check = new Thread(checkHealth);
		check.start();

		// 2. LOOP FOREVER - HOST IS ALWAYS WAITING TO PONG!
		while (true) {
			byte[] receive = new byte[256];
			DatagramPacket udpPacket = new DatagramPacket(receive, receive.length); // receive the data
			udpSocket.receive(udpPacket); // this function will block here if no udp packet has arrived

			String received = new String(udpPacket.getData(), 0, udpPacket.getLength()); // print out data
			// System.out.println(received);
			// process data
			// if received PING request, then reply with PONG
			if (received.substring(0, 4).equals("PING")) {
				int portNfromPing = Integer.parseInt(received.substring(5, 9));
				int TTL = Integer.parseInt(received.substring(10, 11));
				System.out.println("received PING from " + portNfromPing);
				// keep ping back if found new peers
				if (!KnownNodelist.contains(portNfromPing)) {
					KnownNodelist.add(portNfromPing);
					PingHandler pinghandler = new PingHandler(6666, portNfromPing, AvailableFileLists.get(0), TTL);
					Thread h = new Thread(pinghandler);
					h.start();
				}
				// PONG back
				PongHandler handler = new PongHandler(6666, portNfromPing);
				Thread t = new Thread(handler);
				t.start();
				// add peer's file into local file array list
				String peerfileName = received.substring(12);
				if (!AvailableFileLists.contains(peerfileName)) {
					AvailableFileLists.add(peerfileName);

				}
				// TTL Ping to peers one more player for now
				TTL--;
				if (TTL > 0) {
					Set<Integer> ids = nodesTable.keySet();

					for (Integer ID : ids) {
						if (ID != portNfromPing) {
							PingHandler Peerpinghandler = new PingHandler(portNfromPing, ID, peerfileName, TTL);
							Thread ph = new Thread(Peerpinghandler);
							ph.start();
						}

					}
				}

			}
			// If received PONG
			else if (received.substring(0, 4).equals("PONG")) {
				int portNfromPong = Integer.parseInt(received.substring(5, 9));
				System.out.println("received PONG back from " + portNfromPong);
				nodesTable.put(portNfromPong,
						(int) (new Timestamp(System.currentTimeMillis()).getTime() / 1000L) + 2 * 10);

			}

		}

	}

}
