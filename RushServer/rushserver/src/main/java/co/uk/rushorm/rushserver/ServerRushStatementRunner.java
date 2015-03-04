package co.uk.rushorm.rushserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import co.uk.rushorm.core.RushQue;
import co.uk.rushorm.core.RushSearch;
import co.uk.rushorm.core.RushStatementRunner;

/**
 * Created by Stuart on 02/03/15.
 */
public class ServerRushStatementRunner implements RushStatementRunner {
    
    private static final String connectionString = "jdbc:mysql://%s/?user=%s&password=%s";
    private static final String GET_DATABASE = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '%s'";
    private static final String CREATE_DATABASE = "CREATE DATABASE %s";

    private final ServerRushConfig rushConfig;
    
    public ServerRushStatementRunner(ServerRushConfig rushConfig) {
        this.rushConfig = rushConfig;
    }
    
    @Override
    public void runRaw(String statement, RushQue que) {
        try {
            PreparedStatement preparedStatement = ((ServerRushQueue)que).getConnection().prepareStatement(statement);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ValuesCallback runGet(String statement, RushQue que) {

        try {
            PreparedStatement preparedStatement = ((ServerRushQueue) que).getConnection().prepareStatement(statement);
            final ResultSet resultSet = preparedStatement.executeQuery();
            return new ServerValuesCallback(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class ServerValuesCallback implements ValuesCallback {

        private boolean hasNext = false;
        private final ResultSet resultSet;
        private ServerValuesCallback(ResultSet resultSet) {
            this.resultSet = resultSet;
            try {
                hasNext = resultSet.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public List<String> next() {
            try {
                List<String> row = new ArrayList<>();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    row.add(resultSet.getString(i));
                }
                hasNext = resultSet.next();
                return row;
            } catch (SQLException e) {
                e.printStackTrace();
                hasNext = false;
                return null;
            }
        }

        @Override
        public void close() {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void startTransition(RushQue que) {

    }

    @Override
    public void endTransition(RushQue que) {

    }

    @Override
    public boolean isFirstRun() {
        try {
            Connection connection = DriverManager.getConnection(String.format(connectionString, rushConfig.getServerName(), rushConfig.getUsername(), rushConfig.getPassword()));
            Statement checkIfExists = connection.createStatement();
            ResultSet resultSet = checkIfExists.executeQuery(String.format(GET_DATABASE, rushConfig.dbName()));
            if(resultSet.next()) {
                return false;
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate(String.format(CREATE_DATABASE, rushConfig.dbName()));
                /*Statement disableForeignKeyChecks = connection.createStatement();
                disableForeignKeyChecks.execute("SET FOREIGN_KEY_CHECKS=0;");*/
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void initializeComplete(long version) {
        ServerDBVersion serverDBVersion = new RushSearch().findSingle(ServerDBVersion.class);
        if(serverDBVersion != null) {
            serverDBVersion.setVersion(version);
        } else {
            serverDBVersion = new ServerDBVersion(version);
        }
        serverDBVersion.save();
    }

    @Override
    public boolean requiresUpgrade(long version, RushQue que) {
        ServerDBVersion serverDBVersion = new RushSearch().findSingle(ServerDBVersion.class);
        Logger.getLogger(ServerRushStatementRunner.class.getName()).log(Level.INFO, String.format("Old version: %d New version %d", serverDBVersion.getVersion(), version));
        return serverDBVersion.getVersion() < version;
    }
}
