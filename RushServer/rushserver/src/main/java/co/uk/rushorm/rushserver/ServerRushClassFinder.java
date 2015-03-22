package co.uk.rushorm.rushserver;


import net.sourceforge.stripes.util.ResolverUtil;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushClassFinder;
import co.uk.rushorm.core.RushConfig;

/**
 * Created by Stuart on 02/03/15.
 */
public class ServerRushClassFinder implements RushClassFinder {
    
    private final String packageName;
    
    public ServerRushClassFinder(ServerRushConfig rushConfig) {
        this.packageName = rushConfig.getPackageName();
    }
    
    @Override
    public List<Class<? extends Rush>> findClasses(RushConfig rushConfig) {

        ResolverUtil<Rush> resolver = new ResolverUtil<>();
        resolver.findImplementations(Rush.class, packageName);
        Set<Class<? extends Rush>> classes = resolver.getClasses();
        if(!classes.contains(ServerDBVersion.class)) {
            classes.add(ServerDBVersion.class);
        }
        List<Class<? extends Rush>> list = new ArrayList<>();
        for (Class<? extends Rush> clazz : classes) {
            list.add(clazz);
        }
        return list;
    }
}
