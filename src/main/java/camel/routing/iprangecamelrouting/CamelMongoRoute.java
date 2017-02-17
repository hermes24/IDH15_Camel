package camel.routing.iprangecamelrouting;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mina.MinaConstants;

/**
 * A Camel Java DSL Router
 */
//public class CamelMongoRoute extends RouteBuilder {
//
//    @Override
//    public void configure() throws Exception {
//        from("jetty:http://localhost:3001/mongoInsert").to("mongodb:mongo?database=camelRangeApp&collection=range&operation=insert").log("Received insert statement");
//
//}
//}
public class CamelMongoRoute extends RouteBuilder {
    
    private MongoClient mongo;
    
    public CamelMongoRoute(MongoClient mongo){
        this.mongo = mongo;
    }

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    @Override
    public void configure() {

        from("jetty:http://localhost:3001/mongoInsert").process((Exchange exchange) -> {
            String body = exchange.getIn().getBody(String.class);
            System.out.println(body);
            exchange.getOut().setBody("Bye " + body);
            exchange.getOut().setHeader(MinaConstants.MINA_CLOSE_SESSION_WHEN_COMPLETE, true);
            
            MongoDatabase db = mongo.getDatabase("camelRangeApp");
            MongoCollection table = db.getCollection("range");

        });
    
    }
}