package com.codecool.fileshare;

import com.codecool.fileshare.repository.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileshareApplication {
    @Autowired
    private  Database db;

    public static void main(String[] args) {
        SpringApplication.run(FileshareApplication.class, args);
    }
}
