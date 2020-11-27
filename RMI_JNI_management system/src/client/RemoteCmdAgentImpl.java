import RemoteInterface.GetLocalTime;
import RemoteInterface.GetVersion;
import RemoteInterface.RemoteCmdAgent;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteCmdAgentImpl extends UnicastRemoteObject implements RemoteCmdAgent {
    protected RemoteCmdAgentImpl() throws RemoteException {
    }

    @Override
    public Object excute(String cmdID, Object cmdObj) {
        if (cmdID.compareTo("GetLocalTime") == 0)
            return C_GetLocalTime((GetLocalTime) cmdObj);
        else{
            Object localtime = new GetVersion();
            ((GetVersion) localtime).version = 123123;
            return localtime;

        }
    }

    public native Object C_GetLocalTime(GetLocalTime cmdObj);
}
