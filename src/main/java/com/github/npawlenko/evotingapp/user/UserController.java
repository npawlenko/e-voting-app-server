package com.github.npawlenko.evotingapp.user;

import com.github.npawlenko.evotingapp.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @QueryMapping("users")
    public List<UserResponse> getUsers() {
        List<UserResponse> users = userService.getUsers();
        return userService.getUsers();
    }
}
