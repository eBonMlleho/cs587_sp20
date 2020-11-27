//Author: Zhanghao
//Time: 2/9/2020

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.DatagramPacket; 
import java.util.concurrent.TimeUnit;
import java.util.*;
import java.net.DatagramSocket; 
import java.net.InetAddress; 
import java.net.SocketException; 
import java.sql.Timestamp;
import java.time.Instant;

public class Server {

	public static void main(String[] args)  {

		int udpPacketNumber = 0; // keeps track of how many clients were created
		int threadID = 0;
		ArrayList<ClientsHandler> clients = new ArrayList<ClientsHandler>();
		ArrayList<Thread> clientThreads = new ArrayList<Thread>();		
	
		DatagramSocket udpSocket = null; //receive udp socket at port 7777

		//create hashTable to store client INFO
		Hashtable<Integer, clientInfo> agentsTable = new Hashtable<Integer, clientInfo>();
		
		//create a new UDP socket for client to send 
		try {
			udpSocket = new DatagramSocket(7777); // provide MYSERVICE at port 7777
		} catch (IOException e) {
			System.out.println("Could not listen on port: 7777");
			System.exit(-1);
		}

		//create a thread for check health status in the hash Table periodically.
		CheckFunction checkHealth = new CheckFunction(agentsTable,clientThreads);
		Thread check = new Thread(checkHealth);
		check.start();
	

		// 2. LOOP FOREVER - SERVER IS ALWAYS WAITING TO PROVIDE SERVICE!
		while (true) { 
			
			DatagramPacket udpPacket = null;
			
			byte[] receive = new byte[50]; 
			System.out.println("Total number of UDP packets received: " + ++udpPacketNumber);

			
			try {
				//1.9 create a DatgramPacket to receive the data.
				udpPacket = new DatagramPacket(receive, receive.length);
				
				udpSocket.receive(udpPacket);	//this function will block here if no udp packet has arrived
				//UDPHandler udphandler = new UDPHandler(udpPacket,udpSocket); not sure if this line do anything
				String received = new String(udpPacket.getData(), 0, udpPacket.getLength());
				// process string from udp packet
				String[] stringArray = received.split(" ");
				try{
					int id_id = Integer.parseInt(stringArray[0].trim());
					int start_time = Integer.parseInt(stringArray[1].trim());
					int time_interval = Integer.parseInt(stringArray[2].trim());
					int client_port = Integer.parseInt(stringArray[3].trim());
					String client_ip = stringArray[4].trim();
					
					//if id already exists, then update timeout information
					if(isInTable(id_id,agentsTable)){
						agentsTable.get(id_id).updateTimeout();
					//if id not exists, then create new agent and update info to the agentsTable Hashtable
					}else{
						// insert data
						agentsTable.put(id_id,new clientInfo(id_id,start_time,time_interval,client_port,client_ip,threadID));
						System.out.println("start up UNIX time of this client is:" + start_time);
						//create thread
						ClientsHandler handler = new ClientsHandler(client_port, client_ip);	
						Thread t = new Thread(handler);
						t.start();
						clientThreads.add(threadID,t);
						
						threadID++;	
					}
				} catch (NumberFormatException nfe){
     				 System.out.println("NumberFormatException: " + nfe.getMessage());
    			}

			} catch (IOException e) {
				System.out.println("Accept failed: 6666");
				e.printStackTrace();
				System.exit(-1);
			}

			// 2.3 GO BACK TO WAITING FOR OTHER CLIENTS
			// (While the thread that was created handles the connected client's
			// request)

		} // end while loop

	} // end of main method


	public static boolean isInTable(int id, Hashtable<Integer, clientInfo> table){

		return table.containsKey(id);

	}

} // end of class Server

class clientInfo{
	int id;
	int startuptime;
	int timeinterval;
	int cmdPort;
	String ipAddress;
	int threadID;
	int timeout;


	public clientInfo(int id, int sut, int timeinterval, int cmdPort, String ip, int threadID){
		this.id = id;
		startuptime = sut;
		this.timeinterval = timeinterval;
		this.cmdPort = cmdPort;
		ipAddress = ip;
		
		this.threadID = threadID;
		timeout = startuptime + 2*timeinterval;
	}

	public int getThreadID(){
		return threadID;
	}

	public int getPort(){
		return cmdPort;
	}

	public String getIpAddress(){
		return ipAddress;
	}

	// public void updateTimeout(int newTimeinterval){
	// 	timeout = (new Timestamp(System.currentTimeMillis()).getTime()/ 1000L) + 2*newTimeinterval;

	// }

	public void updateTimeout(){
		timeout = (int)(new Timestamp(System.currentTimeMillis()).getTime()/ 1000L) + 2*timeinterval;
	}

	public int getTimeOut(){

		return timeout;
	}

}


class CheckFunction implements Runnable{
	Hashtable <Integer, clientInfo> table;
	ArrayList<Thread> clientThreads;

	CheckFunction(Hashtable <Integer, clientInfo> agentsTable, ArrayList<Thread> clientThreads){
		table = agentsTable;
		this.clientThreads = clientThreads;
	}
	public void run() {
		
		while(true){
			System.out.println("This should print out every 10 seconds");
			Set<Integer> ids = table.keySet();

			for(Integer ID : ids){

				int timeout = table.get(ID).getTimeOut();
	/////test//////////
				//System.out.println(table.get(ID).getThreadID() + "this is thread ID");

				if(timeout <= (new Timestamp(System.currentTimeMillis()).getTime()/ 1000L)){
					//todo PRINT OUT WHO DIED
					System.out.println("ID:"+ID + " is timeout!");
					
					//timeoout already
					table.remove(ID);
					
					//todo close its thread
					//clientThreads.get(table.get(ID).getThreadID()).stop();
				}
			}
			try{
				Thread.sleep(10000);
			}catch(InterruptedException e){
           		 e.printStackTrace();
			}
			
        }	
	}

}



class ClientsHandler implements Runnable {
	public String message; 
	PrintWriter out;
	int port;
	String ip;
	Socket clientSocket = null;
	ClientsHandler(int port, String ip) {
		this.port = port;
		this.ip = ip;
	}

	// This is the client handling code
	public void run() {
		Scanner in;

		try {
			// 1. USE THE SOCKET TO READ WHAT THE CLIENT IS SENDING

				clientSocket = new Socket("localhost",port);
				
				System.out.println("Received new agent IP address: " + ip + " thread starts..."); 
				
				out = new PrintWriter(new BufferedOutputStream(clientSocket.getOutputStream()));
				out.println("Manager is now talking to you");
				out.flush();
				
				out.println("GET OS");
				out.flush();
				out.println("GET TIME");
				out.flush();
				in = new Scanner(new BufferedInputStream(clientSocket.getInputStream()));
				message = in.nextLine();
				System.out.println("The OS Info from client: " + message);

				message = in.nextLine();
				System.out.println("The local time from client is: " + message);

			/**
			 * receive message from client and print out
			 */

		} catch (IOException e) {
			e.printStackTrace();
		}

		// This handling code dies after doing all the printing
	} // end of method run()

} // end of class ClientHandler
