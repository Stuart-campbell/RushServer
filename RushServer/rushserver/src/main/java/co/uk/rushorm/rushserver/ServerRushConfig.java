package co.uk.rushorm.rushserver;

import co.uk.rushorm.core.RushConfig;

/**
 * Created by Stuart on 02/03/15.
 */
public class ServerRushConfig implements RushConfig {

    private String dbName = "rushdb";
    private int dbVersion = 1;
    private boolean inDebug = false;
    private boolean log = false;
    private boolean requireTableAnnotation = false;
    
    private String username;
    private String password;
    private String serverName;
    private String packageName;

    public ServerRushConfig(String username, String password, String serverName, String packageName) {
        this.username = username;
        this.password = password;
        this.serverName = serverName;
        this.packageName = packageName;
    }
    
    @Override
    public String dbName() {
        return dbName;
    }

    @Override
    public int dbVersion() {
        return dbVersion;
    }

    @Override
    public boolean inDebug() {
        return inDebug;
    }

    @Override
    public boolean log() {
        return log;
    }

    @Override
    public boolean requireTableAnnotation() {
        return requireTableAnnotation;
    }

    @Override
    public boolean usingMySql() {
        return true;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getServerName() {
        return serverName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setDbVersion(int dbVersion) {
        this.dbVersion = dbVersion;
    }

    public void setInDebug(boolean inDebug) {
        this.inDebug = inDebug;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public void setRequireTableAnnotation(boolean requireTableAnnotation) {
        this.requireTableAnnotation = requireTableAnnotation;
    }
}
