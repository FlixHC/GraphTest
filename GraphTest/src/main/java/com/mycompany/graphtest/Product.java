/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.graphtest;

/**
 *
 * @author User
 */
public class Product {
    public long id;
    public String name;
    public double price;
    public String category;
    
    public Product(long id, String name, double price, String category){
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }
    public long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
}
