package ge.reklapp.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Tornike on 11.07.2016.
 */
@XmlRootElement
public class Money {
    @XmlElement(name = "amount", required = true)
    private double amount;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Money(){}

    public Money(double amount) {
        this.amount = amount;
    }
}