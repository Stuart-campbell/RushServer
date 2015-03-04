package co.uk.rushorm.rushserver;

import co.uk.rushorm.core.RushObject;

/**
 * Created by Stuart on 02/03/15.
 */
public class ServerDBVersion extends RushObject {
    
    private long version;

    public ServerDBVersion() {
    
    }
    
    public ServerDBVersion(long version) {
        this.version = version;
    }
    
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
