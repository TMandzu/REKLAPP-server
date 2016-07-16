package ge.reklapp.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Tornike on 16.07.2016.
 */
@XmlRootElement
public class LoginInformation {
    @XmlElement(name = "mobile_number", required = true)
    private String mobile_number;

    @XmlElement(name = "password", required = true)
    private String password;

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginInformation(){}

    public LoginInformation(String mobile_number, String password){
        this.mobile_number = mobile_number;
        this.password = password;
    }

    @Override
    public int hashCode() {
        return mobile_number.hashCode()*1000000007 + password.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LoginInformation other = (LoginInformation) obj;
        return this.mobile_number.equals(other.getMobile_number()) && this.password.equals(other.getPassword());
    }
}