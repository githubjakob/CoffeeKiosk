package io.github.projectblackalert;

import io.github.projectblackalert.model.Order;
import io.github.projectblackalert.model.OrderRepository;
import io.github.projectblackalert.model.Product;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackageClasses = OrderRepository.class)
public class MongoDBConfig {

    @Bean
    CommandLineRunner commandLineRunner(final OrderRepository orderRepository) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                orderRepository.save(new Order("1", "Max Mustermann", new Product("Coffee", 1.2),
                        new Product("Latte", 2.2)));
            }
        };
    }
}