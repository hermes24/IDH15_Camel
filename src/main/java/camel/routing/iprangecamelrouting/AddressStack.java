/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camel.routing.iprangecamelrouting;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 *
 * @author luche
 */
public class AddressStack {

    Stack<String> email = new Stack<>();

    public void pushUnit(String unit) throws EmptyStackException {

        try {
            if (unit.equals("TB")) {
                email.push("luc.hermes@pharmapartners.nl");
            } else if (unit.equals("TAB")) {
                email.push("eric.marsilje@pharmapartners.nl");
            } else if (unit.equals("CSH")){
                email.push("mark.avontuur@pharmapartners.nl");
            } else if (unit.equals("PINK")){
                email.push("luchermes@gmail.com");
            }

        } catch (EmptyStackException e) {
        }

        System.out.println("mailaddress in Stack : " + email);
    }
    
    public String getAddress(String request) {

        String address;
        switch (request) {
            case "TB":
                address = "luc.hermes@gmail.com";
                break;
            case "TAB":
                address = "eric.marsilje@pharmapartners.nl";
                break;
            case "CSH":
                address = "eric.marsilje@pharmapartners.nl";
                break;
            case "CSF":
                address = "luc.hermes@pharmapartners.nl";
                break;
            case "DSF":
                address = "luc.hermes@pharmapartners.nl";
                break;
            case "DSH":
                address = "luc.hermes@pharmapartners.nl";
                break;
            default:
                throw new IllegalArgumentException("Verkeerde aanvraag : " + request);
        }
        return address;

    }

    public String popUnit() throws EmptyStackException {
        String mail = null;
        try {
            mail = email.pop();
        } catch (EmptyStackException e) {
        }
        return mail;
    }

}
