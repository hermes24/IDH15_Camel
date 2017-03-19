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
public class PinkHandler extends RouteBuilder {

    private final Connection c;


    public PinkHandler(Connection c) {
        this.c = c;
    }

    @Override
    public void configure() {

        // Quartz timer om deze Job te kunnen schedulen.
        // Deze job draait maar 1x per dag. 
        // Aan het einde van de dag worden alle aanvragen in 1x doorgestuurd naar onze hosting partij Pink.
        from("quartz://PinkScheduler/dailyJob?cron=0/36+*+*+*+*+?").process((Exchange exchange) -> {
            System.out.println("PinkHandler Timer class");

            Statement stmt = c.createStatement();
            try {

                // Verzamel alle ranges welke de status PROCESSED hebben in de database.
                // PROCESSED betekent : Ontvangen in mailmanager en installatiepartij ingelicht dat hij onderhanden is. 
                c.setAutoCommit(false);
                String sqlSelect = "select range.id, customer.name,customer.cluster, range.range,range.partner,process.status "
                        + "from range "
                        + "left join process on process.id = range.status "
                        + "left join customer on customer.id = range.customer "
                        + "where process.status = 'PROCESSED';";

                ResultSet rs = stmt.executeQuery(sqlSelect);

                // Nu gaan we het bericht bouwen.
                // De header is voor ieder bericht hetzelfde.
                String messageHead
                        = "Geachte Partner," + "\n" + "\n"
                        + "Graag de volgende aanvragen in behandeling nemen :" + "\n" + "\n";

                // De body bevat alle regels die terugkomen vanuit de database.
                // We loopen langs alle regels en zetten deze in een nette opmaak.
                String messageBody = "";
                ArrayList ids = new ArrayList();
                while (rs.next()) {
                    messageBody = messageBody + "Klantnaam : " + rs.getString("name") + "\n" + "Range : " + rs.getString("range") + "\n" + "Cluster : " + rs.getString("cluster") + "\n" + "\n";
                    ids.add(rs.getInt("id"));
                }

                // Ook de footer is voor ieder bericht hetzelfde.
                String messageFooter
                        = "Zijn er vragen of opmerkingen, dan vernemen wij dit graag via telefoonnummer : 088-xxxxxxx" + "\n" + "\n"
                        + "Met vriendelijke groet," + "\n"
                        + "PharmaPartners";

                // Header + body + footer = volledige bericht.
                String message = messageHead + messageBody + messageFooter;

                // Als bovenstaande geen regels teruggeeft, hoeven we ook niets te melden bij Pink.
                // Hebben we wel regels, dan kunnen we de database updaten en het bericht verzenden.
                if (!messageBody.isEmpty()) {

                    // Pas nu voor alle records de status PROCESSED aan naar SEND.
                    for (Object id : ids) {

                        Statement stmtUpdate = c.createStatement();
                        try {

                            String sqlUpdate = "UPDATE range set status = 3 where id = " + id.toString();
                            stmtUpdate.executeUpdate(sqlUpdate);
                            stmtUpdate.close();

                        } catch (SQLException e) {
                            System.err.println("Error message PinkHandler " + e.getClass().getName() + ": " + e.getMessage());
                        }
                    }

                    // Nu gaan we de header van het bericht opbouwen.
                    // Onderwerp + to address         
                    // En daarna versturen we het geheel naar de mailQueue.
                    Map<String, Object> map = new HashMap<>();
                    map.put("Subject", "Range aanvraag Pink");
                    map.put("to", "luc.hermes@gmail.com");
                    ProducerTemplate template = exchange.getContext().createProducerTemplate();
                    template.sendBodyAndHeaders("mail:queue:mailQueue", message, map);

                }

                stmt.close();
                c.commit();

            } catch (SQLException e) {
                //System.err.println("Error message PinkHandler " + e.getClass().getName() + ": " + e.getMessage());
            }

        });

    }

}
