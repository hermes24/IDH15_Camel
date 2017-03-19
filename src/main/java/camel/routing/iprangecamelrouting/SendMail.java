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

/**
 *
 * @author luche
 */
public class SendMail {

    private ProducerTemplate template;

    public void sendMail(String message, String subject, String to) {
        System.out.println("SendMail class");

        // Mail bericht verder opbouwen met juiste gegevens
        Map<String, Object> map = new HashMap<>();
//            map.put("Subject", "Opdracht ontvangen in mailmanager");
//            map.put("From", "luc.hermes@gmail.com");
//            map.put("to", adressStack.popUnit());
        
        String body = message;

        map.put("Subject", subject);
        map.put("to", to);

        
        System.out.println("MAP : " + map);
        System.out.println("BODY : " + body);

        template.sendBodyAndHeaders("smtps://luchermes@smtp.gmail.com?password=Novell202lh", body, map);
    }
}
