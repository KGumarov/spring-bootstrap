package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.model.Role;
import web.model.User;
import web.service.RoleService;
import web.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String adminPage(Model model) {
        List<User> list = userService.getAllUsers();
        model.addAttribute("users", list);
        model.addAttribute("user", new User());
        model.addAttribute("all_roles", roleService.getAllRoles());
        return "admin";
    }

    @PostMapping("/del")
    public String delUser(@RequestParam("user_id") long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @PostMapping
    public String saveUser(@ModelAttribute("user") User user,
                           @RequestParam("role_select") Long[] roleIds,
                           @RequestParam("password") String password) {
        for (Long id : roleIds) {
            user.addRole(roleService.getRole(id));
        }

        String bCryptPassword = password.isEmpty() ?
                userService.getUser(user.getId()).getPassword() :
                passwordEncoder.encode(password);

        user.setPassword(bCryptPassword);
        userService.saveUser(user);
        return "redirect:/admin";
    }

}