public class RMILightBulbImpl extends java.rmi.server.UnicastRemoteObject implements RMILightBulb 
{
 	public RMILightBulbImpl() throws java.rmi.RemoteException 
	{setBulb(false);}
	private boolean lightOn;
	public void on() throws java.rmi.RemoteException { setBulb(true); }
	public void off() throws java.rmi.RemoteException {setBulb(false);}
	public boolean isOn() throws java.rmi.RemoteException 
    {	return getBulb(); }
 	public void setBulb (boolean value) { lightOn = value; }
 	public boolean getBulb () { return lightOn; }
}
