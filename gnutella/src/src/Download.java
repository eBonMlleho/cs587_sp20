import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

class Download extends Thread {

	int portno;
	String sharedDirectory;
	Socket socket;
	String filename;

	Download(Socket socket, int portno, String FileDir) {
		this.socket = socket;
		this.portno = portno;
		this.sharedDirectory = FileDir;
	}

	public void run() {
		try {

			InputStream is = socket.getInputStream(); // Connecting Client acting as a server to the file requesting
														// Client
			ObjectInputStream ois = new ObjectInputStream(is);
			OutputStream os = socket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			filename = (String) ois.readObject(); // Fileaname to be downloaded
			String FileLocation;
			if (filename.startsWith("Invalied File")) {
				System.out.println(filename + "  Modified by server...");
			} else {
				while (true) {
					File myFile = new File(sharedDirectory + "//" + filename);
					long length = myFile.length();
					byte[] mybytearray = new byte[(int) length]; // Sending file length of the file to be downloaded to
																	// the client
					oos.writeObject((int) myFile.length());
					oos.flush();
					FileInputStream fileInSt = new FileInputStream(myFile);
					BufferedInputStream objBufInStream = new BufferedInputStream(fileInSt);
					// transferring the contents of the file as stream of bytes
					objBufInStream.read(mybytearray, 0, (int) myFile.length());
					System.out.println("sending file of " + mybytearray.length + " bytes");
					oos.write(mybytearray, 0, mybytearray.length);
					oos.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}