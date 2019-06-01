package io.github.projectblackalert.controller;

import com.google.firebase.auth.FirebaseAuth;
import io.github.projectblackalert.dto.LoginDto;
import io.github.projectblackalert.dto.OrderDto;
import io.github.projectblackalert.dto.UserDetailsDto;
import io.github.projectblackalert.enums.State;
import io.github.projectblackalert.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static io.github.projectblackalert.enums.State.SOLD;

@org.springframework.web.bind.annotation.RestController

public class RestControllerImpl implements RestController {

    @Override
    public List<Product> getAllProducts() {
        Product cappuccino = new Product("Cappuccino", 1.70);
        Product cappuccinoSpezial = new Product("Cappuccino spezial", 1.70);
        Product trinkschokolade = new Product("Trinkschokolade", 1.70);
        Product kaffeeSchwarz = new Product("Kaffee Schwarz", 1.70);
        Product caffeeLatte = new Product("Kaffee mit Milch", 1.70);
        Product latteMacchiato = new Product("Latte Macchiato", 1.90);
        Product espresso = new Product("Espresso", 1.20);
        Product doppio = new Product("Doppio", 1.70);
        Product espressoMacchiato = new Product("Espresso Macchiato", 1.30);
        Product tee = new Product("Heißes Wasser/Tee", 1.20);

        return Arrays.asList(cappuccino, cappuccinoSpezial, trinkschokolade, kaffeeSchwarz,caffeeLatte,
        latteMacchiato, espresso, doppio, espressoMacchiato, tee);
    }

    @GetMapping("health")
    public ResponseEntity healthcheck() {
        return new ResponseEntity(HttpStatus.OK);
    }
}