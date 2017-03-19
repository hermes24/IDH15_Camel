/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camel.routing.iprangecamelrouting;

import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;

/**
 *
 * @author luche
 */
public class Mail extends RouteBuilder {

    private final AddressStack adressStack;

    public Mail(AddressStack adressStack) {
        this.adressStack = adressStack;
    }

    @Override
    public void configure() {

        from("mail:queue:mailQueue").process((Exchange exchange) -> {
            
            ProducerTemplate template = exchange.getContext().createProducerTemplate();

            // Mail bericht verder opbouwen met juiste gegevens
            // Het subject,mailadres en body komen vanuit de exchange.
            
            Map<String, Object> map = new HashMap<>();
            map.put("Subject", exchange.getIn().getHeader("Subject"));
            map.put("From", "luc.hermes@gmail.com");
            map.put("to", exchange.getIn().getHeader("to"));      
            String body = exchange.getIn().getBody(String.class);
            
            System.out.println("MailQueue, bericht verzonden met de volgende gegevens : " + map);

            template.sendBodyAndHeaders("smtps://luchermes@smtp.gmail.com?password=Novell202lh", body, map);

        });
    }
}
