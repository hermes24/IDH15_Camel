package camel.routing.iprangecamelrouting;

import com.mongodb.MongoClient;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

/**
 * A Camel Java DSL Router
 */
public class CamelMongoRoute extends RouteBuilder {

    public void configure() throws Exception {
        from("jetty:http://localhost:3001/mongoInsert").to("mongodb:mongo?database=camelRangeApp&collection=range&operation=insert").log("Received insert statement");

}
}
