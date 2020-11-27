import java.rmi.*;
public class LightBulbClient {
 public static void main(String args[]) {
  try { String registry = "localhost"; // the registry server’s IP
   if (args.length >=1) { registry = args[0]; }
   String registration = "rmi://" + registry + "/RMILightBulb";
   Remote remoteService = Naming.lookup ( registration );
   RMILightBulb bulbService = (RMILightBulb) remoteService;
   bulbService.off();
   System.out.println ("Bulb state : " + bulbService.isOn()  );
   System.out.println ("Invoking bulbservice.off()");
   bulbService.off();
   System.out.println ("Bulb state : " + bulbService.isOn() );
  } catch (NotBoundException nbe) {
   System.out.println ("No light bulb service available in registry!");
  } catch (RemoteException re) { System.out.println ("RMI - " + re);
  } catch (Exception e) { System.out.println ("Error - " + e); }
 }
}
