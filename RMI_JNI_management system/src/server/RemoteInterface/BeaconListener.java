package RemoteInterface;

import java.rmi.Remote;
import java.rmi.NotBoundException;
import java.rmi.server.ServerNotActiveException;

public interface BeaconListener extends Remote {

    int checkVersion()throws java.rmi.RemoteException;
    byte[] getUpadteBinary()throws java.rmi.RemoteException;
    int deposit(Beacon b) throws java.rmi.RemoteException, ServerNotActiveException, NotBoundException;

}