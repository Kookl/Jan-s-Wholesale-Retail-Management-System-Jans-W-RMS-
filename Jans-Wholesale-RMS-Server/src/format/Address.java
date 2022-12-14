package format;

import java.io.Serializable;

public class Address implements Serializable {
    private String street;
    private String town;
    private String parish;


    public Address(String street, String city, String parish) {
        this.street = street;
        this.town = city;
        this.parish = parish;
    }

    public Address() {
        this.street = "";
        this.town = "";
        this.parish = "";
    }

    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }

    public String getTown() {
        return town;
    }
    public void setCity(String city) {
        this.town = city;
    }

    public String getParish() {
        return parish;
    }
    public void setParish(String parish) {
        this.parish = parish;
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", city='" + town + '\'' +
                ", parish='" + parish + '\'' +
                '}';
    }
}
