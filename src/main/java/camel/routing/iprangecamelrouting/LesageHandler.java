/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camel.routing.iprangecamelrouting;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;

/**
 *
 * @author luche
 */
public class LesageHandler extends RouteBuilder {

    private final Connection c;

    public LesageHandler(Connection c) {
        this.c = c;
    }

    @Override
    public void configure() {

        from("quartz://LesageScheduler/dailyJob?cron=0/49+*+*+*+*+?").process((Exchange exchange) -> {
            System.out.println("LesageHandler Timer class");

            Statement stmt = c.createStatement();
            try {

                c.setAutoCommit(false);
                String sqlSelect = "select range.id, customer.name,customer.cluster,range.range,range.partner,process.status "
                        + "from range "
                        + "left join process on process.id = range.status "
                        + "left join customer on customer.id = range.customer "
                        + "where partner = 'Lesage Automatisering' "
                        + "and process.status = 'NEW';";

                ResultSet rs = stmt.executeQuery(sqlSelect);

                String messageHead
                        = "Geachte Partner," + "\n" + "\n"
                        + "De volgende range aanvragen zijn in behandeling genomen:" + "\n" + "\n";

                String messageBody = "";
                ArrayList ids = new ArrayList();
                while (rs.next()) {
                    messageBody = messageBody + "Klantnaam : " + rs.getString("name") + "\n" + "Range : " + rs.getString("range") + "\n" + "Cluster : " + rs.getString("cluster") + "\n" + "\n";
                    ids.add(rs.getInt("id"));
                }

                String messageFooter
                        = "Binnen 5 werkdagen ontvangt u van ons een bericht over de status van uw aanvraag." + "\n" + "\n"
                        + "Met vriendelijke groet," + "\n"
                        + "PharmaPartners";

                String message = messageHead + messageBody + messageFooter;

                if (!messageBody.isEmpty()) {

                    for (Object id : ids) {

                        Statement stmtUpdate = c.createStatement();
                        try {

                            String sqlUpdate = "UPDATE range set status = 2 where id = " + id.toString();
                            stmtUpdate.executeUpdate(sqlUpdate);
                            stmtUpdate.close();

                        } catch (SQLException e) {
                            System.err.println("Error message LesageHandler " + e.getClass().getName() + ": " + e.getMessage());
                        }
                    }
                    
                    // Nu gaan we de header van het bericht opbouwen.
                    // Onderwerp + to address         
                    // En daarna versturen we het geheel naar de mailQueue.
                    Map<String, Object> map = new HashMap<>();
                    map.put("Subject", "Mail naar Lesage");
                    map.put("to", "luc.hermes@gmail.com");
                    ProducerTemplate template = exchange.getContext().createProducerTemplate();
                    template.sendBodyAndHeaders("mail:queue:mailQueue", message, map);
                    
                }
                stmt.close();
                c.commit();

            } catch (SQLException e) {
                //System.err.println("Error message LesageHandler " + e.getClass().getName() + ": " + e.getMessage());
            }

        });

    }

}
