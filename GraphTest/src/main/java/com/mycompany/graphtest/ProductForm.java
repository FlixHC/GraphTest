/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.graphtest;

/**
 *
 * @author User
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import com.google.gson.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import java.util.List;
import java.util.ArrayList;

public class ProductForm extends JFrame{
    private JTextField tfName = new JTextField();
    private JTextField tfPrice = new JTextField();
    private JTextField tfCategory = new JTextField();
    private JTextArea outputArea = new JTextArea(10, 30);
    
    public ProductForm() {
        setTitle("GraphQL Product Form");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(tfName);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(tfPrice);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(tfCategory);
        JButton btnAdd = new JButton("Add Product");
        JButton btnFetch = new JButton("Show All");
        inputPanel.add(btnAdd);
        inputPanel.add(btnFetch);
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
        btnAdd.addActionListener(e -> tambahProduk());
        btnFetch.addActionListener(e -> ambilSemuaProduk());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        }
    
    private void tambahProduk() {
        try {
            String query = String.format(
            "mutation { addProduct(name: \"%s\", price: %s, category: \"%s\") { id name } }",
            tfName.getText(),
            tfPrice.getText(),
            tfCategory.getText()
            );
        String jsonRequest = new Gson().toJson(new GraphQLQuery(query));
        String response = sendGraphQLRequest(jsonRequest);
        outputArea.setText("Product added!\n\n" + response);
        } catch (Exception e) {
        outputArea.setText("Error: " + e.getMessage());
        }
    }
    
    private void ambilSemuaProduk() {
    try {
        String query = "query { allProducts { id name price category } }";
        String jsonRequest = new Gson().toJson(new GraphQLQuery(query));
        String response = sendGraphQLRequest(jsonRequest);

        JsonObject data = JsonParser.parseString(response).getAsJsonObject().getAsJsonObject("data");
        JsonArray arr = data.getAsJsonArray("allProducts");

        List<Product> products = new ArrayList<>();
        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            Product p = new Product(
                obj.get("id").getAsLong(),
                obj.get("name").getAsString(),
                obj.get("price").getAsDouble(),
                obj.get("category").getAsString()
            );
            products.add(p);
        }

        showProductTable(products); // ➡️ buka panel baru
    } catch (Exception e) {
        outputArea.setText("Error: " + e.getMessage());
    }
}
    
    private void showProductTable(List<Product> products) {
    JFrame frame = new JFrame("Daftar Produk");
    frame.setSize(800, 400);
    frame.setLocationRelativeTo(null);
    
    DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Kategori", "Harga"}, 0);
    JTable table = new JTable(tableModel);
    for (Product p : products) {
        tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), p.getPrice()});
    }

    JTextField nameField = new JTextField(10);
    JTextField priceField = new JTextField(6);
    JTextField categoryField = new JTextField(8);
    JButton addBtn = new JButton("Tambah");
    JButton editBtn = new JButton("Edit");
    JButton deleteBtn = new JButton("Hapus");

    JPanel formPanel = new JPanel();
    formPanel.add(new JLabel("Nama:"));
    formPanel.add(nameField);
    formPanel.add(new JLabel("Kategori:"));
    formPanel.add(categoryField);
    formPanel.add(new JLabel("Harga:"));
    formPanel.add(priceField);

    JPanel btnPanel = new JPanel();
    btnPanel.add(addBtn);
    btnPanel.add(editBtn);
    btnPanel.add(deleteBtn);

    table.getSelectionModel().addListSelectionListener(e -> {
        int i = table.getSelectedRow();
        if (i != -1) {
            nameField.setText(table.getValueAt(i, 1).toString());
            categoryField.setText(table.getValueAt(i, 2).toString());
            priceField.setText(table.getValueAt(i, 3).toString());
        }
    });

    addBtn.addActionListener(e -> {
        try {
            String name = nameField.getText();
            String category = categoryField.getText();
            double price = Double.parseDouble(priceField.getText());

            String query = String.format("mutation { addProduct(name: \"%s\", price: %s, category: \"%s\") { id name } }",
                name, price, category);
            String json = new Gson().toJson(new GraphQLQuery(query));
            String response = sendGraphQLRequest(json);
            outputArea.setText("Produk ditambahkan!\n" + response);
            frame.dispose();
            ambilSemuaProduk();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
        }
    });

    editBtn.addActionListener(e -> {
        int row = table.getSelectedRow();
        if (row != -1) {
            try {
                String id = table.getValueAt(row, 0).toString();
                String name = nameField.getText();
                String category = categoryField.getText();
                double price = Double.parseDouble(priceField.getText());

                String query = String.format("mutation { updateProduct(id: \"%s\", name: \"%s\", price: %s, category: \"%s\") { id } }",
                    id, name, price, category);
                String json = new Gson().toJson(new GraphQLQuery(query));
                String response = sendGraphQLRequest(json);
                outputArea.setText("Produk diubah!\n" + response);
                frame.dispose();
                ambilSemuaProduk();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        }
    });

    deleteBtn.addActionListener(e -> {
        int row = table.getSelectedRow();
        if (row != -1) {
            try {
                String id = table.getValueAt(row, 0).toString();
                String query = String.format("mutation { deleteProduct(id: %s) }", id);
                String json = new Gson().toJson(new GraphQLQuery(query));
                String response = sendGraphQLRequest(json);
                outputArea.setText("Produk dihapus!\n" + response);
                frame.dispose();
                ambilSemuaProduk();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        }
    });

    frame.setLayout(new BorderLayout());
    frame.add(new JScrollPane(table), BorderLayout.CENTER);
    frame.add(formPanel, BorderLayout.NORTH);
    frame.add(btnPanel, BorderLayout.SOUTH);
    frame.setVisible(true);
}
    
        
    private String sendGraphQLRequest(String json) throws Exception {
            URL url = new URL("http://localhost:4567/graphql");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
                }
            try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(conn.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line).append("\n");
                return sb.toString();
            }
        }
    public static void main(String[] args) {
    SwingUtilities.invokeLater(ProductForm::new);
    }

    class GraphQLQuery {
        String query;
        GraphQLQuery(String query) {
        this.query = query;
        }
    }
}
