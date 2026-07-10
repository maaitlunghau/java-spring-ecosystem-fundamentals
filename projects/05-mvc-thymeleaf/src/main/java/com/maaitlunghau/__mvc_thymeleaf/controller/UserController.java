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

// @Controller (không phải @RestController) — trả về tên template, Spring + Thymeleaf
// tự resolve thành file HTML tương ứng trong resources/templates/.
// (@Controller returns view name; Thymeleaf resolves it to templates/<name>.html)
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /users — hiển thị danh sách tất cả users
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users/index";
    }

    // GET /users/create — hiển thị form tạo mới
    // Truyền object rỗng vào model để Thymeleaf bind vào form fields.
    // (Pass empty object so Thymeleaf can bind form fields to it)
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createUserRequest", new CreateUserRequest("", "", "", 0));
        return "users/create";
    }

    // POST /users/create — xử lý form submit tạo mới
    // BindingResult phải đứng ngay sau @Valid — Spring truyền kết quả validation vào đây.
    // Nếu có lỗi thì render lại form thay vì throw exception.
    // (BindingResult must follow @Valid; errors → re-render form instead of throwing)
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
            // Username hoặc email đã tồn tại — thêm lỗi vào model để hiển thị trên form
            // (Duplicate username/email: add error to model, re-render form)
            model.addAttribute("errorMessage", ex.getMessage());
            return "users/create";
        }
        // Redirect sau khi tạo thành công — tránh form resubmit khi user refresh trang.
        // (Redirect after success: prevents form resubmission on page refresh — PRG pattern)
        return "redirect:/users";
    }

    // GET /users/{id}/edit — hiển thị form edit
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getUserEntityById(id);
        // Truyền UpdateUserRequest rỗng (password trống) và thông tin user hiện tại cho form
        // (Pass current user data and empty password field for the edit form)
        model.addAttribute("updateUserRequest", new UpdateUserRequest(user.getUsername(), user.getEmail(), "", user.getAge()));
        model.addAttribute("userId", id);
        return "users/edit";
    }

    // POST /users/{id}/edit — xử lý form submit update
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

    // POST /users/{id}/delete — dùng POST vì HTML form không hỗ trợ DELETE method.
    // (POST for delete: HTML forms only support GET/POST, no DELETE)
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
}
