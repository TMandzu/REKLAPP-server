package ge.reklapp.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.sql.Date;

/**
 * Created by Tornike on 10.07.2016.
 */
@XmlRootElement
public class UserInfo {

    public UserInfo() {
    }

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "surname")
    private String surname;

    @XmlElement(name = "pin")
    private String pin;

    @XmlElement(name = "country")
    private String country;

    @XmlElement(name = "city")
    private String city;

    @XmlElement(name = "street_address")
    private String street_address;

    @XmlElement(name = "mobile_number")
    private String mobile_number;

    @XmlElement(name = "sex")
    private String sex;

    @XmlElement(name = "birthdate")
    @XmlSchemaType(name = "date")
    protected Date birthdate;

    @XmlElement(name = "relationship")
    private String relationship;

    @XmlElement(name = "number_of_children")
    private String number_of_children;

    @XmlElement(name = "average_monthly_income")
    private String average_monthly_income;

    @XmlElement(name = "email")
    private String email;

    @XmlElement(name = "old_mobile_number")
    private String old_mobile_number;

    @XmlElement(name = "money")
    private double money;

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    @Override
    public int hashCode() {
        return mobile_number.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserInfo other = (UserInfo) obj;
        return this.mobile_number.equals(other.getMobile_number());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getNumber_of_children() {
        return number_of_children;
    }

    public void setNumber_of_children(String number_of_children) {
        this.number_of_children = number_of_children;
    }

    public String getAverage_monthly_income() {
        return average_monthly_income;
    }

    public void setAverage_monthly_income(String average_monthly_income) {
        this.average_monthly_income = average_monthly_income;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOld_mobile_number() {
        return old_mobile_number;
    }

    public void setOld_mobile_number(String old_mobile_number) {
        this.old_mobile_number = old_mobile_number;
    }

}
