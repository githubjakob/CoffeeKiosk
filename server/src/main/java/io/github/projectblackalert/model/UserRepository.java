package io.github.projectblackalert.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by jakob on 18.06.18.
 */
public interface UserRepository extends MongoRepository<User, String> {

    List<User> findByDealerTrue();

    User findFirstByUid(String uid);
}
