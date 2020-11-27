import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class main {
    public static void main(String[] args) throws UnknownHostException,RemoteException, AlreadyBoundException  {
        System.out.println(System.getProperty("java.library.path"));
        System.loadLibrary("execute");

        Object obj =  new Object();
        Object cmdObj =  new Object();
        RemoteCmdAgentImpl cmdRegister = new RemoteCmdAgentImpl();
        obj = cmdRegister.excute("GetLocalTime",cmdObj);

        Thread beaconSender = new BeaconSender();
        beaconSender.run();
    }
}
