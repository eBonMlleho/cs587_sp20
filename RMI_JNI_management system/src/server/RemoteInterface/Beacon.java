package RemoteInterface;

import java.io.Serializable;

public class Beacon implements Serializable {

    public String cmdAgentID;
    public int clientID,startTime,clientVersion;

    public Beacon(){
    }

    public void setCmdAgentID(String id){ this.cmdAgentID = id;}

}
