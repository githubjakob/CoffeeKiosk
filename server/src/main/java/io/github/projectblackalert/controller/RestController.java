package io.github.projectblackalert.controller;

import io.github.projectblackalert.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/rest/")
public interface RestController {

    @GetMapping("product")
    List<Product> getAllProducts();

    @GetMapping("health")
    ResponseEntity healthcheck();
}
