package io.github.projectblackalert.controller;

import io.github.projectblackalert.dto.LoginDto;
import io.github.projectblackalert.dto.OrderDto;
import io.github.projectblackalert.dto.UserDetailsDto;
import io.github.projectblackalert.enums.State;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/auth/")
public interface SecuredRestController {

    @GetMapping("/order")
    List<OrderDto> getAllOrders(@RequestParam(value = "orderId", required = false) String orderId,
                                       @RequestParam(value = "state", required = false) State state,
                                       @RequestParam(value = "userId", required = false) String userId);

    @PatchMapping("/order/{orderId}/{newState}")
    boolean updateOrder(@PathVariable("orderId") String orderId, @PathVariable("newState") State newState);

    @PostMapping("/order")
    ResponseEntity addOrder(@RequestBody OrderDto orderDto) throws MissingServletRequestParameterException;

    @PostMapping("login")
    UserDetailsDto login(@RequestBody LoginDto loginDto);
}
