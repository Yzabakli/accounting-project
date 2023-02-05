package com.service.implementation;

import com.dto.UserDTO;
import com.entity.User;
import com.mapper.MapperUtil;
import com.repository.UserRepository;
import com.service.SecurityService;
import com.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final PasswordEncoder passwordEncoder;
    private final MapperUtil mapper;

    public UserServiceImpl(UserRepository userRepository, MapperUtil mapper, @Lazy SecurityService securityService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO findByUsername(String username) {

        return mapper.convert(userRepository.findByUsername(username), UserDTO.class);
    }

    @Override
    public UserDTO findById(Long id) {

        return mapper.convert(userRepository.findById(id).orElseThrow(), UserDTO.class);
    }


    @Override
    public List<UserDTO> listAllAdminUsers() {

        List<UserDTO> users = userRepository.findAllAdmins()
                .stream()
                .map(user -> mapper.convert(user, UserDTO.class))
                .collect(Collectors.toList());

        for (UserDTO userDTO : users) {

            userDTO.setOnlyAdmin(userRepository.countAdminsByCompany(userDTO.getCompany()
                    .getId()) <= 1);
        }
        return users;
    }

    @Override
    public List<UserDTO> listAllByLoggedInCompany() {

        List<UserDTO> users = userRepository.findByCompany_Id(getCompanyId())
                .stream()
                .map(user -> mapper.convert(user, UserDTO.class))
                .collect(Collectors.toList());

        for (UserDTO userDTO : users) {

            if (userDTO.getRole().getDescription().equals("Admin"))

                userDTO.setOnlyAdmin(userRepository.countAdminsByCompany(userDTO.getCompany().getId()) <= 1);

            else userDTO.setOnlyAdmin(false);
        }

        return users;
    }

    @Override
    public void save(UserDTO newUser) {

        User user = mapper.convert(newUser, User.class);

        user.setUsername(user.getUsername().trim());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setEnabled(true);

        userRepository.save(user);
    }

    @Override
    public void update(UserDTO userDTO) {

        userDTO.setUsername(userDTO.getUsername().trim());
        userDTO.setEnabled(true);
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        userRepository.save(mapper.convert(userDTO, User.class));
    }

    @Override
    public boolean isUsernameAlreadyInUse(String username) {

        return userRepository.existsByUsernameIgnoreCase(username.trim());
    }

    @Override
    public boolean isUsernameNotPrevious(Long id, String username) {

        return !userRepository.findById(id).orElseThrow().getUsername().equals(username.trim());
    }

    @Override
    public void deleteById(Long id) {

        User user = userRepository.findById(id).orElseThrow();

        user.setIsDeleted(true);

        userRepository.save(user);
    }

    private Long getCompanyId() {

        return securityService.getLoggedInUser().getCompany().getId();
    }
}
