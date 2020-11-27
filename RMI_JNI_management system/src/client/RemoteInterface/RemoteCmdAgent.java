package RemoteInterface;
import java.io.Serializable;
import java.rmi.RemoteException;

public interface RemoteCmdAgent extends java.rmi.Remote {
    public Object excute(String CmdID, Object cmdObj) throws RemoteException;
}
