package com.fdan.jwtteste.service;

import org.springframework.stereotype.Service;

import com.fdan.jwtteste.domain.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    public List<User> listUsers(){
        User u1 = new User("Fulano", "Brazil");
        User u2 = new User("Siclano", "Mexico");

        return new ArrayList<>(Arrays.asList(u1, u2));
    }
}
