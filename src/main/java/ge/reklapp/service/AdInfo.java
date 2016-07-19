package ge.reklapp.service;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Tornike on 15.07.2016.
 */
public class AdInfo {
    @XmlElement(name = "status", required = true)
    private String status;

    @XmlElement(name = "pair_id", required = true)
    private int pair_id;

    @XmlElement(name = "link", required = true)
    private String link;

    @XmlElement(name = "company", required = true)
    private String company;

    @XmlElement(name = "product", required = true)
    private String product;

    @XmlElement(name = "description", required = true)
    private String description;

    @XmlElement(name = "view_gain", required = true)
    private double view_gain;

    @XmlElement(name = "ad_id", required = true)
    private int ad_id;

    public int getAd_id() {
        return ad_id;
    }

    public void setAd_id(int ad_id) {
        this.ad_id = ad_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPair_id() {
        return pair_id;
    }

    public void setPair_id(int pair_id) {
        this.pair_id = pair_id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getView_gain() {
        return view_gain;
    }

    public void setView_gain(double view_gain) {
        this.view_gain = view_gain;
    }

    public AdInfo(){
        this.status = "";
        this.pair_id = 0;
        this.link = "";
        this.company = "";
        this.product = "";
        this.description = "";
        this.view_gain = 0;
    }

}
