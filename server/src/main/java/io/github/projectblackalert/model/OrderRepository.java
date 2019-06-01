package io.github.projectblackalert.model;

import io.github.projectblackalert.enums.State;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {

    List<Order> findByState(State state);

    List<Order> findByUserId(String userId);
}