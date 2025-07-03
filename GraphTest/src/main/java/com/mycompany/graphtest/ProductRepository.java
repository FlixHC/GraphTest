/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.graphtest;

/**
 *
 * @author User
 */

import java.util.*;

public class ProductRepository {
   private static List<Product> productList = new ArrayList<>();
   private static long idCounter = 1;

   public static Product add(String name, Double price, String category) {
       Product product = new Product(idCounter++, name, price, category);
       productList.add(product);
       return product;
   }

   public static List<Product> findAll() {
       return productList;
   }

   public static Product findById(Long id) {
       return productList.stream().filter(p -> p.id==(id)).findFirst().orElse(null);
   }

   public static boolean delete(Long id) {
       return productList.removeIf(p -> p.id==(id));
   }

}
