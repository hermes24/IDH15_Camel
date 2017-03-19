/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camel.routing.iprangecamelrouting;

/**
 *
 * @author luche
 */
public class Customer {

    private String partner;
    private String customer;
    private String cluster;
    private String range;
    private String unit;
    private String prio;

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getPartner() {
        return partner;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getCluster() {
        return cluster;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getRange() {
        return range;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setSpoed(String prio) {
        this.prio = prio;
    }

    public String getPrio() {
        return prio;
    }

    @Override
    public String toString() {
        return partner + " - " + range + " - " + customer + " - " + cluster + " - " + unit + " - " + prio;
    }

}
