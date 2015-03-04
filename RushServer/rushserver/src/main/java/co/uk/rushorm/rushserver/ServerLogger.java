package co.uk.rushorm.rushserver;

import java.util.logging.Level;

import co.uk.rushorm.core.Logger;
import co.uk.rushorm.core.RushConfig;

/**
 * Created by Stuart on 02/03/15.
 */
public class ServerLogger implements Logger {

    private final java.util.logging.Logger logger;
    private final RushConfig rushConfig;

    public ServerLogger(RushConfig rushConfig) {
        this.rushConfig = rushConfig;
        logger = java.util.logging.Logger.getLogger(ServerLogger.class.getName());
    }
    
    @Override
    public void log(String message) {
        if(rushConfig.log()) {
            logger.log(Level.INFO, message);
        }
    }

    @Override
    public void logSql(String sql) {
        if(rushConfig.log()) {
            logger.log(Level.SEVERE, sql);
        }
    }

    @Override
    public void logError(String error) {
        logger.log(Level.SEVERE, error);
    }
}
