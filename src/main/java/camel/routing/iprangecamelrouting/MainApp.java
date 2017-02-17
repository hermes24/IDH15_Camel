package camel.routing.iprangecamelrouting;

import com.mongodb.MongoClient;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;

/**
 * A Camel Application
 */
public class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    @SuppressWarnings("resource")
    public static void main(String args[]) throws Exception {

        // Setup registry & Beans for new connections
        SimpleRegistry registry = new SimpleRegistry();
        MongoClient mongo = new MongoClient("localhost", 27017);
        registry.put("mongo", mongo);
        
        // Build new camelContext with created registry object
        CamelContext context = new DefaultCamelContext(registry);
        
        // Define routes
        context.addRoutes(new HttpReceiver());
        context.addRoutes(new CamelMongoRoute(mongo));
        
        // Start Camel !
        context.start();

    }

}
