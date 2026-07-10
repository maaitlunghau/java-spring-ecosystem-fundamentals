package com.maaitlunghau.__mvc_thymeleaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.maaitlunghau.__mvc_thymeleaf.dto.CreateUserRequest;
import com.maaitlunghau.__mvc_thymeleaf.dto.UpdateUserRequest;
import com.maaitlunghau.__mvc_thymeleaf.model.User;
import com.maaitlunghau.__mvc_thymeleaf.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createUserRequest", new CreateUserRequest("", "", "", 0));
        return "users/create";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute CreateUserRequest createUserRequest,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "users/create";
        }
        try {
            userService.createUser(createUserRequest);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "users/create";
        }

        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getUserEntityById(id);

        model.addAttribute("updateUserRequest", new UpdateUserRequest(user.getUsername(), user.getEmail(), "", user.getAge()));
        model.addAttribute("userId", id);
        return "users/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute UpdateUserRequest updateUserRequest,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", id);
            return "users/edit";
        }
        try {
            userService.updateUser(id, updateUserRequest);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("userId", id);
            return "users/edit";
        }
        return "redirect:/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
}
