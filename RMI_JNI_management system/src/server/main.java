import RemoteInterface.BeaconListener;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class main {
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {

        //start manager
        System.out.println("start listening..");

        Manager manager = new Manager();
        manager.run();

        //start baeconListener
        Registry registry = LocateRegistry.createRegistry(1099);
        BeaconListener beaconListener = new BeaconListenerImpl();
        registry.rebind("becaonListener", beaconListener);

    }
}
