/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.graphtest;

/**
 *
 * @author User
 */
import graphql.*;
import graphql.schema.idl.*;
import java.io.*;
import java.util.Objects;
import graphql.schema.GraphQLSchema;

public class GraphQLConfig {
   public static GraphQL init() throws IOException {
       InputStream schemaStream = GraphQLConfig.class.getClassLoader().getResourceAsStream("schema.graphqls");
       
       if (schemaStream == null) {
           throw new RuntimeException("schema.graphqls not found in classpath.");
       }
       
       String schema = new String(Objects.requireNonNull(schemaStream).readAllBytes());

       TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schema);
       RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
           .type("Query", builder -> builder
               .dataFetcher("allProducts", env -> ProductRepository.findAll())
               .dataFetcher("productById", env -> {
                   Long id = env.getArgument("id");
                   return ProductRepository.findById(id);
               })
           )
           .type("Mutation", builder -> builder
               .dataFetcher("addProduct", env -> ProductRepository.add(
                   env.getArgument("name"),
                   ((Number) env.getArgument("price")).doubleValue(),
                   env.getArgument("category")
               ))
               .dataFetcher("deleteProduct", env -> {
                    Long id = Long.parseLong(env.getArgument("id").toString());
                    return ProductRepository.delete(id);
                })
               .dataFetcher("updateProduct", env -> {
                    Long id = Long.parseLong(env.getArgument("id").toString());
                    String name = env.getArgument("name");
                    double price = ((Number) env.getArgument("price")).doubleValue();
                    String category = env.getArgument("category");

                    Product product = ProductRepository.findById(id);
                    if (product == null) return null;

                    product.setName(name);
                    product.setPrice(price);
                    product.setCategory(category);
                    return product;
                })
           )
           .build();

       GraphQLSchema schemaFinal = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
       return GraphQL.newGraphQL(schemaFinal).build();
   }
}
