package com.example.griddly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GriddlyApplication {

    public static void main(String[] args) {
        SpringApplication.run(GriddlyApplication.class, args);
        System.out.println(
                "Hello World!"
        );
    }

}
