package com.controller;

import com.dto.UserDTO;
import com.service.CompanyService;
import com.service.RoleService;
import com.service.SecurityService;
import com.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final SecurityService securityService;
    private final RoleService roleService;
    private final CompanyService companyService;

    public UserController(UserService userService, SecurityService securityService, RoleService roleService, CompanyService companyService) {
        this.userService = userService;
        this.securityService = securityService;
        this.roleService = roleService;
        this.companyService = companyService;
    }

    @GetMapping("/list")
    public String listAllUsers(Model model) {

        if (securityService.getLoggedInUser().getRole().getDescription().equals("Root User")) {

            model.addAttribute("users", userService.listAllAdminUsers());

        } else {

            model.addAttribute("users", userService.listAllByLoggedInCompany());
        }

        return "user/user-list";
    }

    @GetMapping("/create")
    public String createUser(Model model) {

        model.addAttribute("newUser", new UserDTO());

        getOptionByUser(model);

        return "user/user-create";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("newUser") UserDTO newUser, BindingResult bindingResult, Model model) {

        if (!newUser.getPassword().equals(newUser.getConfirmPassword())) {

            bindingResult.rejectValue("confirmPassword", "", "Passwords should match.");
        }

        if (userService.isUsernameAlreadyInUse(newUser.getUsername())) {

            bindingResult.rejectValue("username", "", "A user with this email already exists.");
        }

        if (bindingResult.hasErrors()) {

            getOptionByUser(model);

            return "user/user-create";
        }

        userService.save(newUser);

        return "redirect:/users/list";
    }

    @GetMapping("/update/{id}")
    public String editUser(@PathVariable Long id, Model model) {

        UserDTO userDTO = userService.findById(id);
        UserDTO loggedInUser = securityService.getLoggedInUser();

        if (!userDTO.getCompany().equals(loggedInUser.getCompany()) && !loggedInUser.getId().equals(1L)) return "redirect:/users/list";

        model.addAttribute("user", userDTO);

        getOptionByUser(model);

        return "user/user-update";
    }

    @PostMapping("/update/{id}")
    public String editUser(@Valid @ModelAttribute("user") UserDTO user, BindingResult bindingResult, Model model) {

        if (!user.getPassword().equals(user.getConfirmPassword())) {

            bindingResult.rejectValue("confirmPassword", "", "Passwords should match.");
        }

        if (userService.isUsernameAlreadyInUse(user.getUsername()) && userService.isUsernameNotPrevious(user.getId(), user.getUsername())) {

            bindingResult.rejectValue("username", "", "A user with this email already exists.");
        }

        if (bindingResult.hasErrors()) {

            getOptionByUser(model);
        }

        userService.update(user);

        return "redirect:/users/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {

        UserDTO userDTO = userService.findById(id);

        if (!userDTO.getCompany().equals(securityService.getLoggedInUser().getCompany())) return "redirect:/users/list";

        userService.deleteById(id);

        return "redirect:/users/list";
    }

    private void getOptionByUser(Model model) {
        if (securityService.getLoggedInUser().getRole().getDescription().equals("Root User")) {

            model.addAttribute("companies", companyService.listAllCompanies());
            model.addAttribute("userRoles", Set.of(roleService.findById(2L)));

        } else {

            model.addAttribute("companies", Set.of(securityService.getLoggedInUser().getCompany()));
            model.addAttribute("userRoles", roleService.getAllRoles());
        }
    }
}
