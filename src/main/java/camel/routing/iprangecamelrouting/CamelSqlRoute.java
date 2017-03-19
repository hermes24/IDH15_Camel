/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camel.routing.iprangecamelrouting;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;

/**
 *
 * @author luche
 */
public class CamelSqlRoute extends RouteBuilder {

    private final Connection c;
    private final AddressStack addressStack;

    public CamelSqlRoute(Connection c, AddressStack addressStack) {
        this.c = c;
        this.addressStack = addressStack;

    }

    @Override
    public void configure() {

        from("jetty:http://localhost:3001/mailmanagersql").process((Exchange exchange) -> {
            String body = exchange.getIn().getBody(String.class);
            System.out.println("Request ontvangen in MailManager");

            // Inboud van bericht omzetten naar een customer object.

            ObjectMapper mapper = new ObjectMapper();
            Customer customer = mapper.readValue(body, Customer.class);

            // Het request bevat niet het daadwerkelijke ID van het customer database record.
            // Deze gaan we eerst ophalen 
            
            int customerId = 0;
            Statement stmt = c.createStatement();
            try {
                c.setAutoCommit(false);
                String sql = "SELECT id FROM customer WHERE name = '" + customer.getCustomer() + "';";
                ResultSet rs = stmt.executeQuery(sql);
                customerId = rs.getInt("id");
                stmt.close();
                c.commit();
            } catch (SQLException e) {
                System.err.println(e.getClass().getName() + " : " + e.getMessage());
            }
            
            // Op basis van het verkregen ID voeren we een update uit in de database.
            // We zetten het record van de betreffende range op de status NEW 
            // NEW = aangemeld bij mailmanager 

            stmt = c.createStatement();
            try {

                c.setAutoCommit(false);
                String sql = "UPDATE range SET status = '1' WHERE customer = '" + customerId + "';";
                stmt.executeUpdate(sql);
                stmt.close();
                c.commit();
            } catch (SQLException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
                        
            // Betreft het een spoed aanvraag, dan meteen actie

            if(customer.getPrio().equals("true")){
                String id = Integer.toString(customerId);
                System.out.println("SPOED TRUE in SQL ROUTE");

                Map<String, Object> mapSpoed = new HashMap<>();
                mapSpoed.put("id", id);
                mapSpoed.put("name", customer.getCustomer());
                mapSpoed.put("range", customer.getRange());     
                ProducerTemplate template = exchange.getContext().createProducerTemplate();
                template.sendBodyAndHeaders("direct:prio", body, mapSpoed);
            }
            
            // Volgende halte -> bevestigingsmail naar afdeling van aanvraag.         
            // Routering en inhoud bericht bouwen.
            // En doorsturen naar mailQueue
            Map<String, Object> map = new HashMap<>();
            map.put("Subject", "Range aanvraag ontvangen in MailManager");
            map.put("to", addressStack.getAddress(customer.getUnit()));

//            exchange.getOut().setHeaders(map);
//            exchange.getOut().setBody(
            exchange.getIn().setHeaders(map);
            exchange.getIn().setBody(
                    "Gegevens aanvraag : " + "\n" + "\n"
                    + "Klantnaam : " + customer.getCustomer() + "\n"
                    + "Range : " + customer.getRange() + "\n"
                    + "Cluster :" + customer.getCluster() + "\n"
                    + "Leverancier : " + customer.getUnit() + "\n"
                    + "Spoed : " + customer.getPrio() + "\n"
            );

        }).to("mail:queue:mailQueue");
    }
}
