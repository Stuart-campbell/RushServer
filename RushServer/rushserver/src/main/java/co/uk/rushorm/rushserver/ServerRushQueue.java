package co.uk.rushorm.rushserver;

import java.sql.Connection;
import java.sql.SQLException;

import co.uk.rushorm.core.RushQue;

/**
 * Created by Stuart on 02/03/15.
 */
public class ServerRushQueue implements RushQue {

    public interface ServerRushQueueListener {
        public Connection getNewConnection() throws SQLException;
    }
    
    private ServerRushQueueListener listener;
    
    private Connection connection;

    public void setListener(ServerRushQueueListener listener) {
        this.listener = listener;
    }
    
    public Connection getConnection() {
        try {
            if(connection == null || connection.isClosed()) {
                setConnection(listener.getNewConnection());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public ServerRushQueue(Connection connection) {
        this.connection = connection;
    }
    
}
