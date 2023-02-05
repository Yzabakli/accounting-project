package com.service;

import com.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    UserDTO findByUsername(String username);

    UserDTO findById(Long id);

    List<UserDTO> listAllAdminUsers();

    List<UserDTO> listAllByLoggedInCompany();

    boolean isUsernameAlreadyInUse(String username);

    void save(UserDTO newUser);

    void update(UserDTO newUser);

    boolean isUsernameNotPrevious(Long id, String username);

    void deleteById(Long id);
}

