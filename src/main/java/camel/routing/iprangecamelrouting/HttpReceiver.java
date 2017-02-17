package camel.routing.iprangecamelrouting;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mina.MinaConstants;

/**
 * A Camel Java DSL Router
 */
public class HttpReceiver extends RouteBuilder {

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {

        from("jetty:http://localhost:12345").process(new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {
                String body = exchange.getIn().getBody(String.class);
                System.out.println(body);
                exchange.getOut().setBody("Bye " + body);
                exchange.getOut().setHeader(MinaConstants.MINA_CLOSE_SESSION_WHEN_COMPLETE, true); 

            }

        });
    
    }
}
