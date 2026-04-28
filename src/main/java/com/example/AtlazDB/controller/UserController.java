package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.UserRequestDTO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.AtlazDB.model.User;
import com.example.AtlazDB.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping
    public User create(@RequestBody UserRequestDTO dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody UserRequestDTO dto) {
        return service.update(id,dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}