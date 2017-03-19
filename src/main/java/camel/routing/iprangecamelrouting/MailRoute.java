/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camel.routing.iprangecamelrouting;


import java.sql.Connection;
import org.apache.camel.builder.RouteBuilder;


/**
 *
 * @author luche
 */
public class MailRoute extends RouteBuilder {
    
    private Connection c;
    
public MailRoute(Connection c){
        this.c = c;
    }

    public void configure() {
        
//        
//
//        from("jetty:http://localhost:3001/x").process((Exchange exchange) -> {
//            String body = exchange.getIn().getBody(String.class);
//            System.out.println("MailManager");
//            System.out.println("JSON string from GUI : " + body);
//            exchange.getOut().setBody("Bye " + body);
//            exchange.getOut().setHeader(MinaConstants.MINA_CLOSE_SESSION_WHEN_COMPLETE, true);
//            
//            ObjectMapper mapper = new ObjectMapper();
//            Customer customer = mapper.readValue(body, Customer.class);
//            
//            System.out.println("Customer Java object : " + customer.toString());            
//            
//        });
        // Route 1: This route sends and email for every file found in the specified 
        // folder, with the file content as email body and a fixed email subject.
        // To make it work: replace the placeholders below with real data
        from("file:folderWithFilesToMail?noop=true")
                .setHeader("subject", simple("Duimen omhoog!!"))
                .to("smtps://luchermes@smtp.gmail.com?password=Novell202lh&to=luchermes@gmail.com&from=camellover@gmail.com");

        // Route 2: This route searches your inbox for emails with a specific 
        // subject. For every match, an entry is added to the log, and for every 
        // match the email content is appended to a file.
        // TODO: if too many files in the inbox match, the route does not give log output and does not create files
        // To make it work: replace the placeholders below with real data
        from(
                "imaps://imap.gmail.com?username=luchermes&password=Novell202lh"
                + "&searchTerm.subjectOrBody='Duimen omhoog!!'"
                + "&delete=false"
                //								+ "&unseen=false"
                + "&consumer.delay=60000")
                .to("file:studmails"
                        + "?fileExist=Append")
                .to("log:studentmail?level=INFO");







    }
}
