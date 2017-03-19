package camel.routing.iprangecamelrouting;

import java.sql.Connection;
import java.sql.DriverManager;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * A Camel Application
 */
public class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    @SuppressWarnings("resource")
    public static void main(String args[]) throws Exception {
        
        // maven commandline start commando
        // C:\Users\luche\Documents\Github\IDH15_Camel
        // mvn clean install exec:java -Dexec.mainClass=iprangecamelrouting.mainapp

        // Build new camelContext with created registry object
        CamelContext context = new DefaultCamelContext();

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=true");
        context.addComponent("mail", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        // Setup connection to SQLite DB 
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:C:\\Data\\ranges.db");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        AddressStack adressStack = new AddressStack();

        // Define routes
        context.addRoutes(new CamelSqlRoute(c, adressStack));
        context.addRoutes(new Mail(adressStack));
        context.addRoutes(new WestersHandler(c));
        context.addRoutes(new LesageHandler(c));
        context.addRoutes(new EzorgHandler(c));
        context.addRoutes(new FloatingByteHandler(c));
        
        context.addRoutes(new PinkHandler(c));
        context.addRoutes(new DirectAction(c));

        // Start Camel !
        context.start();

    }

}
