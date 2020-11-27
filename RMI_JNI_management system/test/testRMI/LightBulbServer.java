import java.rmi.*; 
import java.rmi.server.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
public class LightBulbServer {
 public static void main(String args[]) {
  try {
   RMILightBulbImpl bulbService = new RMILightBulbImpl();
   RemoteRef location = bulbService.getRef();
   System.out.println (location.remoteToString());
   
   
   Registry registry = LocateRegistry.createRegistry(6001);
   registry.rebind("rmi://localhost/RMILightBulb", bulbService);
   
   
   //String registry = "localhost"; // where the registry server locates
   //if (args.length >=1) {
   // registry = args[0];
   //}
   //String registration = "rmi://" + registry + "/RMILightBulb";
   //Naming.rebind( registration, bulbService );
 } catch (Exception e) { System.err.println ("Error - " + e); } } }
