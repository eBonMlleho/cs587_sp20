import RemoteInterface.*;

import RemoteInterface.Beacon;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.InputMismatchException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

public class Manager implements Runnable {
    static int clientVersion = 1;
    static private ScheduledExecutorService scheduledExecutorService = null;
    static private ConcurrentHashMap<String,RemoteClient> clientArray = new ConcurrentHashMap<>();

    @Override
    public void run() {
        // set up clean offline machines
        if (scheduledExecutorService == null){// if instance of manager is firs-time run
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleWithFixedDelay(new cleanOfflineClient(),0,10, TimeUnit.SECONDS);
        }
    }

    static public Boolean clientIsRegisted(String clientID){ return clientArray.containsKey(clientID);}

    static public void updateClientStatus(String clientID){
        if (!clientIsRegisted(clientID))
            throw new InputMismatchException();
        else {
            Integer currentTime = Math.toIntExact(System.currentTimeMillis() / 1000L);
            if ((currentTime - clientArray.get(clientID).getLastResponseTime()) < 20)
                System.out.println("Recv client:" + clientID + " beacon package");
            else
                System.out.println("Client:" + clientID + " is online now");

            clientArray.get(clientID).lastResponTime = (int) currentTime;
        }
    }

    static public void registerClinet(Beacon beacon) throws RemoteException, NotBoundException {
        RemoteClient newClient = new RemoteClient(beacon.startTime,
                                                  beacon.clientVersion,
                                                  beacon.cmdAgentID,
                                                  beacon.clientID);
        clientArray.put(newClient.cmdAgentID, newClient);

        Registry registry = LocateRegistry.getRegistry("localhost",1080);
        RemoteCmdAgent remoteCmdAgent= (RemoteCmdAgent) registry.lookup("listener");

        GetLocalTime localeTime = new GetLocalTime();
        localeTime = (GetLocalTime) remoteCmdAgent.excute("GetLocalTime",localeTime);
        System.out.println(localeTime.time);

        GetVersion localVersion = new GetVersion();
        localVersion = (GetVersion) remoteCmdAgent.excute("GetLocalVersion",localVersion);
        System.out.println(localVersion.version);
    }

    //class for clean time-out machine
    public static class cleanOfflineClient implements Runnable{
        @Override
        public void run(){
            clientArray.forEach((k, v) -> {
                Integer currentTime = Math.toIntExact(System.currentTimeMillis() / 1000L);
                if ((currentTime -  v.lastResponTime) > 20 && (currentTime -  v.lastResponTime) < 35){
                    System.out.println("Client" + v.cmdAgentID + " is current offline because NO RESPONSE");
                }
            });
        }
    }


    //zijijiade
    public static class RemoteClient{
        String cmdAgentID;
        int clientID,startTime,clientVersion;
        //int timeout;
        int lastResponTime;

        public RemoteClient(int startTime,int clientVersion,String cmdAgentID, int clientID){
            this.cmdAgentID = cmdAgentID;
            this.clientID = clientID;
            this.startTime = startTime;
            this.clientVersion = clientVersion;
            lastResponTime = startTime;
            //timeout = this.startTime + 10;
        }

        public int getLastResponseTime(){
            return lastResponTime;
        }


    }


}
