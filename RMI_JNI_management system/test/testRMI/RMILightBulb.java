public interface RMILightBulb extends java.rmi.Remote {
  public void on ()     throws java.rmi.RemoteException;
  public void off()     throws java.rmi.RemoteException;
  public boolean isOn() throws java.rmi.RemoteException;
}
