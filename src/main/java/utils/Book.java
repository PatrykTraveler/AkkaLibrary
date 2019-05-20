package utils;

import java.io.Serializable;

public class Book implements Serializable {
    public Double price;
    public String title;
    public String db;

    public Book(String title, Double price, String db){
        this.price = price;
        this.title = title;
        this.db = db;
    }
}
