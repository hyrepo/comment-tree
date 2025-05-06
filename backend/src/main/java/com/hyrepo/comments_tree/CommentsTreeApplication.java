package com.hyrepo.comments_tree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class CommentsTreeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommentsTreeApplication.class, args);
    }

}
