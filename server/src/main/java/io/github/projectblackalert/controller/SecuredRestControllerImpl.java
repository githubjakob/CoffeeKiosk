package io.github.projectblackalert.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import io.github.projectblackalert.RestClient;
import io.github.projectblackalert.dto.LoginDto;
import io.github.projectblackalert.dto.OrderDto;
import io.github.projectblackalert.dto.UserDetailsDto;
import io.github.projectblackalert.enums.State;
import io.github.projectblackalert.model.Order;
import io.github.projectblackalert.model.OrderRepository;
import io.github.projectblackalert.model.User;
import io.github.projectblackalert.model.UserRepository;
import io.github.projectblackalert.security.FirebaseUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.RestController
public class SecuredRestControllerImpl implements SecuredRestController {

    @Autowired
    private FirebaseAuth firebaseAuth;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<OrderDto> getAllOrders(@RequestParam(value = "orderId", required = false) String orderId,
                                       @RequestParam(value = "state", required = false) State state,
                                       @RequestParam(value = "userId", required = false) String userId) {
        if (orderId != null) {
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            if (orderOptional.isPresent()) {
                return Arrays.asList(orderOptional.get()).stream().map(order ->
                        new OrderDto(order)).collect(Collectors.toList());
            }
        } else if (state != null) {
            return orderRepository.findByState(state).stream().map(order ->
                    new OrderDto(order)).collect(Collectors.toList());
        } else if (userId != null) {
            return orderRepository.findByUserId(userId).stream().map(order ->
                    new OrderDto(order)).collect(Collectors.toList());
        } else {
            return orderRepository.findAll().stream().map(order ->
                    new OrderDto(order)).collect(Collectors.toList());
        }
        return Arrays.asList();
    }

    @Override
    public boolean updateOrder(@PathVariable("orderId") String orderId, @PathVariable("newState") State newState) {
        System.out.println("RestController: Updating order " + orderId + " with state " + newState);
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setState(newState);
            if (newState.equals(State.SOLD)) {
                order.setWaitingTimeWhenSold(Instant.now());
            }
            orderRepository.save(order);
        }
        return false;
    }

    @Override
    public ResponseEntity addOrder(@RequestBody OrderDto orderDto) throws MissingServletRequestParameterException {
        System.out.println("Received new order by " + orderDto.getUserName());

        Order order = new Order(orderDto);
        orderRepository.save(order);

        List<User> dealers = userRepository.findByDealerTrue();

        for (User dealer : dealers) {
            System.out.println("Sending push notification to dealer: " + dealer.getUid());
            RestClient.sendPushNotification(dealer.getMessagingToken());
        }

        return ResponseEntity.ok().build();
    }

    @Override
    public UserDetailsDto login(@RequestBody LoginDto loginDto) {
        System.out.println("user logged in");
        System.out.println(loginDto.getUid());
        System.out.println(loginDto.getMessagingToken());

        // first check if user already exists, then update
        List<User> userWithSameUid = userRepository.findByUid(loginDto.getUid());

        if (userWithSameUid.size() == 0) {
            System.out.println("creating new user in db/user logged in for the first time");
            User user = new User(loginDto.getUid(), loginDto.getMessagingToken());
            user.setDealer(isUserDealer(loginDto.getEmail()));
            userRepository.save(user);
        } else {
            System.out.println("found user with this uid in db, updating");
            if (userWithSameUid.size() > 1) {
                System.out.println("Es gibt mehrere user mit der gleichen uid in der db");
            }

            for (User oneUserWithSameUid : userWithSameUid ) {
                oneUserWithSameUid.setMessagingToken(loginDto.getMessagingToken());
                userRepository.save(oneUserWithSameUid);
            }
        }

        return new UserDetailsDto(loginDto.getUid(), loginDto.getMessagingToken(),
                loginDto.getEmail(), isUserDealer(loginDto.getEmail()));
    }

    private boolean isUserDealer(String email) {
        if (email.equals("dealer@gmail.com")) return true;
        return false;
    }

    private void getUserDetails() {
        FirebaseUserDetails userDetails =
                (FirebaseUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserRecord user = null;
        try {
            user = firebaseAuth.getUser(userDetails.getId());
            System.out.println();
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
        }

        System.out.println("Granted access for username: " + userDetails.getUsername() + ", UserId: " + userDetails.getId() + "\n"
                + "calling google server to obtain some data: \n"
                + "user created " + Instant.ofEpochMilli(user.getUserMetadata().getCreationTimestamp()).toString() + " \n"
                + "display name " + user.getDisplayName());
    }
}
