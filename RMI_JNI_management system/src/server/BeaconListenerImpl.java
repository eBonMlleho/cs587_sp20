import RemoteInterface.Beacon;
import RemoteInterface.BeaconListener;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

public class BeaconListenerImpl extends UnicastRemoteObject implements BeaconListener, Serializable {

    protected BeaconListenerImpl() throws RemoteException {
    }

    @Override
    public int checkVersion() { return Manager.clientVersion; }

    @Override
    public int deposit(Beacon b) throws ServerNotActiveException, RemoteException, NotBoundException {
        if (Manager.clientIsRegisted(b.cmdAgentID))
            Manager.updateClientStatus(b.cmdAgentID);
        else{
            b.cmdAgentID = getClientHost();
            Manager.registerClinet(b);
        }

        return 0;
    }

    @Override
    public byte[] getUpadteBinary() {
        return new byte[0];
    }
}
