package co.uk.rushorm.rushserver;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.sql.Connection;
import java.sql.SQLException;

import co.uk.rushorm.core.RushConfig;
import co.uk.rushorm.core.RushQue;
import co.uk.rushorm.core.RushQueProvider;

/**
 * Created by Stuart on 02/03/15.
 */
public class ServerRushQueueProvider implements RushQueProvider, ServerRushQueue.ServerRushQueueListener {

    private BoneCP poolDataSource;
    
    public ServerRushQueueProvider(ServerRushConfig rushConfig) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl("jdbc:mysql://" + rushConfig.getServerName() + "/" + rushConfig.dbName());
            config.setUsername(rushConfig.getUsername());
            config.setPassword(rushConfig.getUsername());
            config.setLazyInit(true);
            config.setMinConnectionsPerPartition(5);
            config.setMaxConnectionsPerPartition(20);
            config.setIdleConnectionTestPeriodInMinutes(60);
            config.setIdleMaxAgeInMinutes(240);
            config.setPartitionCount(1);
            config.setIdleConnectionTestPeriodInMinutes(10);
            config.setConnectionTestStatement("/* ping */ SELECT 1");

            poolDataSource = new BoneCP(config); // setup the connection pool
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public RushQue blockForNextQue() {
        try {
            ServerRushQueue serverRushQueue = new ServerRushQueue(poolDataSource.getConnection());
            serverRushQueue.setListener(this);
            return serverRushQueue;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public void waitForNextQue(final RushQueCallback rushQueCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                rushQueCallback.callback(blockForNextQue());
            }
        }).start();
    }

    @Override
    public void queComplete(RushQue que) {
        try {
            ((ServerRushQueue)que).getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getNewConnection() throws SQLException {
        return poolDataSource.getConnection();
    }
}
