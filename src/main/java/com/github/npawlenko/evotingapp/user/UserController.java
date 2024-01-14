package com.github.npawlenko.evotingapp.user;

import com.github.npawlenko.evotingapp.user.dto.UserRequest;
import com.github.npawlenko.evotingapp.user.dto.UserResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @QueryMapping("users")
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }

    @QueryMapping("users_page")
    public List<UserResponse> getUsersPage(
            @Min(1) @Argument("page_size") int pageSize,
            @Min(0) @Argument("page_number") int pageNumber
    ) {
        return userService.getUsersPage(pageSize, pageNumber);
    }

    @MutationMapping("edit_user")
    public UserResponse editUser(@Argument Long id, @Argument UserRequest data) {
        return userService.editUser(id, data);
    }

    @MutationMapping("delete_user")
    public void deleteUser(@Argument Long id) {
        userService.deleteUser(id);
    }
}
