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
public class DirectAction extends RouteBuilder {

    private final Connection c;

    public DirectAction(Connection c) {
        this.c = c;
    }

    public void configure() {

        from("direct:prio").process((Exchange exchange) -> {
            System.out.println("SPOED QUEUE");
            String body = exchange.getIn().getBody(String.class);

            // Inboud van bericht omzetten naar een customer object.
            ObjectMapper mapper = new ObjectMapper();
            Customer customer = mapper.readValue(body, Customer.class);

            Statement stmt = c.createStatement();
            try {

                c.setAutoCommit(false);
                String id = (String) exchange.getIn().getHeader("id");

                // En meteen een update naar status SEND.
                String sqlUpdate = "UPDATE range set status = 3 where customer = " + id + ";";
                stmt.executeUpdate(sqlUpdate);
                stmt.close();
                c.commit();
            } catch (SQLException e) {
                System.err.println(e.getClass().getName() + " : " + e.getMessage());
            }

            // Bericht bouwen naar Partner.
            String messageHead
                    = "Geachte Partner," + "\n" + "\n"
                    + "De volgende range aanvragen zijn in behandeling genomen:" + "\n" + "\n";

            String messageBody = "Klantnaam : " + customer.getCustomer() + "\n" + "Range : " + customer.getRange() + "\n" + "Cluster :  " + customer.getCluster() + "\n" + "\n";

            String messageFooter
                    = "Uw aanvraag is in behandeling, we houden u op de hoogte van de voortgang. " + "\n" + "\n"
                    + "Met vriendelijke groet," + "\n"
                    + "PharmaPartners";

            String message = messageHead + messageBody + messageFooter;

            Map<String, Object> map = new HashMap<>();
            map.put("Subject", "MailSPOED naar Partner");
            map.put("to", "luc.hermes@gmail.com");
            ProducerTemplate template = exchange.getContext().createProducerTemplate();
            template.sendBodyAndHeaders("mail:queue:mailQueue", message, map);

            // Nu gaan we het bericht bouwen.
            // De header is voor ieder bericht hetzelfde.
            String messageHeadPink
                    = "Geachte Partner," + "\n" + "\n"
                    + "Graag de volgende aanvragen in behandeling nemen :" + "\n" + "\n";

            // De body bevat alle regels die terugkomen vanuit de database.
            // We loopen langs alle regels en zetten deze in een nette opmaak.
            String messageBodyPink = "Klantnaam : " + customer.getCustomer() + "\n" + "Range : " + customer.getRange() + "\n" + "\n";

            // Ook de footer is voor ieder bericht hetzelfde.
            String messageFooterPink
                    = "Zijn er vragen of opmerkingen, dan vernemen wij dit graag via telefoonnummer : 088-xxxxxxx" + "\n" + "\n"
                    + "Met vriendelijke groet," + "\n"
                    + "PharmaPartners";

            // Header + body + footer = volledige bericht.
            String messagePink = messageHeadPink + messageBodyPink + messageFooterPink;

            Map<String, Object> mapPink = new HashMap<>();
            mapPink.put("Subject", "RangeSPOED aanvraag Pink");
            mapPink.put("to", "luc.hermes@gmail.com");
            ProducerTemplate templatePink = exchange.getContext().createProducerTemplate();
            templatePink.sendBodyAndHeaders("mail:queue:mailQueue", messagePink, mapPink);

        });
    }
}
