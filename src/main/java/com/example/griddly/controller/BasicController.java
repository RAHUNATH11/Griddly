//package com.example.griddly.controller;
//
//
//import com.example.griddly.entity.Product;
//import com.example.griddly.repository.ProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.Scanner;
//
//@RestController
//@RequestMapping("/api/products")
//public class BasicController {
//
//    @Autowired
//    ProductRepository productRepository;
//    @GetMapping
//    public List<Product> getAllProducts() {
//        return productRepository.findAll();
//    }
//}
