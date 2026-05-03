package com.teamtaskmanager.controller;

import com.teamtaskmanager.dto.UserDto;
import com.teamtaskmanager.model.Role;
import com.teamtaskmanager.model.User;
import com.teamtaskmanager.repository.UserRepository;
import com.teamtaskmanager.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class MainConroller {

    private static final Logger logger = LoggerFactory.getLogger(MainConroller.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    // HOME PAGE
    @GetMapping("/home")
    public String home() {
        return "home";
    }

    // SIGNUP PAGE
    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    // REGISTER USER
    @PostMapping("/register")
    public String registerUser(@Validated @ModelAttribute UserDto userDto,
                               Model model,
                               BindingResult result) {

        try {

            if (result.hasErrors()) {
                return "register";
            }

            User exist = userRepository.findByEmail(userDto.getEmail());

            if (exist != null) {
                model.addAttribute("error", "Email already registered!");
                return "signup";
            }

            User user = new User();
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setRole(userDto.getRole());

            userRepository.save(user);
            emailService.sendSignupMail(user.getEmail(), user.getName());

            logger.info("User registered successfully: {}", user.getEmail());

            model.addAttribute("success", "Registration Successful!");
            return "redirect:/signup";

        } catch (Exception e) {

            logger.error("Register Error: {}", e.getMessage(), e);

            model.addAttribute("error", "Something went wrong!");
            return "signup";
        }
    }

    // LOGIN PAGE
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // LOGIN USER
    @PostMapping("/login")
    public String loginUser(@ModelAttribute UserDto userDto, Model model, HttpSession session) {

        try {

            User user = userRepository.findByEmail(userDto.getEmail());

            // EMAIL CHECK
            if (user == null) {
                model.addAttribute("error", "Email does not exist!");
                return "login";
            }

            // PASSWORD CHECK
            if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
                model.addAttribute("error", "Invalid Password!");
                return "login";
            }

            // SESSION
            session.setAttribute("loggedInUser", user);

            logger.info("User logged in: {}, Role: {}", user.getEmail(), user.getRole());

            // ROLE CHECK
            if (user.getRole() == Role.ADMIN) {
                return "redirect:/admin/admin_dashboard";
            }

            if (user.getRole() == Role.MEMBER) {
                return "redirect:/user/user_dashboard";
            }

            model.addAttribute("error", "Invalid role!");
            return "login";

        } catch (Exception e) {

            logger.error("Login Error: {}", e.getMessage(), e);

            model.addAttribute("error", "Something went wrong!");
            return "login";
        }
    }
}