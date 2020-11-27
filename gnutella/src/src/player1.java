import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class player1 {
	public static void main(String[] args) throws IOException {

		// LOCAL FILE SETUP
		List<String> AvailableFileLists = new ArrayList<String>();
		AvailableFileLists.add("player1.txt");

		// USER INPUT COMMAND INTERATION
		UserInteraction userHandler = new UserInteraction(AvailableFileLists);
		Thread u = new Thread(userHandler);
		u.start();

		// LOCAL PEERS SERUP
		List<Integer> KnownNodelist = new ArrayList<Integer>();
		KnownNodelist.add(7777);
		KnownNodelist.add(6666);

		// LOCAL PORT SETUP create a new UDP socket to receive packets. port number is
		// random generated
		int portNUmber = portNumberGenerator();
		DatagramSocket udpSocket = new DatagramSocket(portNUmber);
		System.out.println("My port number is " + portNUmber);

		// PERIODICALLY PING TO HOST and player2 for now. need to ping everyone later
		PingHandler handler = new PingHandler(portNUmber, 7777, AvailableFileLists.get(0), 2);
		Thread t = new Thread(handler);
		t.start();

		PingHandler handler2 = new PingHandler(portNUmber, 6666, AvailableFileLists.get(0), 2);
		Thread t2 = new Thread(handler2);
		t2.start();

		// CHECK TIME OUT
		Hashtable<Integer, Integer> nodesTable = new Hashtable<Integer, Integer>();
		CheckfFunction checkHealth = new CheckfFunction(nodesTable);
		Thread check = new Thread(checkHealth);
		check.start();

		// NODE KEEP LISTENING ANY NEW PACKET
		while (true) {
			byte[] receive = new byte[256];
			DatagramPacket udpPacket = new DatagramPacket(receive, receive.length);
			udpSocket.receive(udpPacket);
			String received = new String(udpPacket.getData(), 0, udpPacket.getLength()); // print out data
			// System.out.println(received);
			// process data
			// If received PONG
			if (received.substring(0, 4).equals("PONG")) {
				int portNfromPong = Integer.parseInt(received.substring(5, 9));
				System.out.println("received PONG back from " + portNfromPong);
				nodesTable.put(portNfromPong,
						(int) (new Timestamp(System.currentTimeMillis()).getTime() / 1000L) + 2 * 10);

			} else if (received.substring(0, 4).equals("PING")) {
				int portNfromPing = Integer.parseInt(received.substring(5, 9));
				int TTL = Integer.parseInt(received.substring(10, 11));
				System.out.println("received PING from " + portNfromPing);
				// keep ping back if found new peers
				if (!KnownNodelist.contains(portNfromPing)) {
					KnownNodelist.add(portNfromPing);
					PingHandler pinghandler = new PingHandler(portNUmber, portNfromPing, AvailableFileLists.get(0),
							TTL);
					Thread h = new Thread(pinghandler);
					h.start();
				}
				// PONG back
				PongHandler Ponghandler = new PongHandler(portNUmber, portNfromPing);
				Thread p = new Thread(Ponghandler);
				p.start();

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
		}

	}

	private static int portNumberGenerator() {
		Random rand = new Random();
		return 8000 + rand.nextInt(1000);
	}

}
