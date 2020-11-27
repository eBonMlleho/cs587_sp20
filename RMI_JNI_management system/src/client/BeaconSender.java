import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import RemoteInterface.*;

import java.rmi.server.ServerNotActiveException;
import java.util.Random;

public class BeaconSender extends Thread {
    Beacon baecon;
    Random rand = new Random(System.currentTimeMillis());

    BeaconSender() throws UnknownHostException {
        baecon = new Beacon();
        baecon.clientVersion = 1;
        baecon.clientID = rand.nextInt(Integer.MAX_VALUE);
        baecon.cmdAgentID = InetAddress.getLocalHost().getHostAddress();
        baecon.startTime = Math.toIntExact(System.currentTimeMillis() / 1000L);
    }

    @Override
    public void run() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost",1099);
            BeaconListener remoteRegistry = (BeaconListener) registry.lookup("becaonListener");

            while (true){
                int asd = remoteRegistry.deposit(baecon);
                sleep(2000);
            }
        } catch (RemoteException | InterruptedException | NotBoundException e) {
            e.printStackTrace();
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
    }
}
