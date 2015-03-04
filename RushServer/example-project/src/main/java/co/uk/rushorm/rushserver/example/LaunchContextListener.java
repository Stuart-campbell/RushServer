package co.uk.rushorm.rushserver.example;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import co.uk.rushorm.rushserver.RushServer;
import co.uk.rushorm.rushserver.ServerRushConfig;

/**
 * Created by Stuart on 03/03/15.
 */
public class LaunchContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServerRushConfig rushConfig = new ServerRushConfig("rush", "rush", "localhost", this.getClass().getPackage().getName());
        rushConfig.setInDebug(true);
        rushConfig.setLog(true);
        RushServer.initialize(rushConfig);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
