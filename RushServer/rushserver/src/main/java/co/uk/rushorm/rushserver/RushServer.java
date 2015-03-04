package co.uk.rushorm.rushserver;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.Logger;
import co.uk.rushorm.core.RushClassFinder;
import co.uk.rushorm.core.RushColumn;
import co.uk.rushorm.core.RushConfig;
import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushObjectDeserializer;
import co.uk.rushorm.core.RushObjectSerializer;
import co.uk.rushorm.core.RushQueProvider;
import co.uk.rushorm.core.RushStatementRunner;
import co.uk.rushorm.core.RushStringSanitizer;

/**
 * Created by Stuart on 02/03/15.
 */
public class RushServer {

    public static void initialize(ServerRushConfig rushConfig) {
        initialize(rushConfig, new ArrayList<RushColumn>());
    }

    public static void initialize(ServerRushConfig rushConfig, List<RushColumn> columns) {

        Logger logger = new ServerLogger(rushConfig);
        RushStringSanitizer rushStringSanitizer = new ServerRushStringSanitizer();
        RushClassFinder rushClassFinder = new ServerRushClassFinder(rushConfig);
        RushStatementRunner statementRunner = new ServerRushStatementRunner(rushConfig);

        RushQueProvider queProvider = new ServerRushQueueProvider(rushConfig);
        RushObjectDeserializer rushObjectDeserializer = new ServerJsonDeserializer();
        RushObjectSerializer rushObjectSerializer = new ServerJsonSerializer();

        RushCore.initialize(rushClassFinder, statementRunner, queProvider, rushConfig, rushStringSanitizer, logger, columns, rushObjectSerializer, rushObjectDeserializer);
    }
    
}
