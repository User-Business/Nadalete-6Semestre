package com.example.atlazdb.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.atlazdb.dto.UserRequestDTO;
import com.example.atlazdb.dto.UserResponseDTO;
import com.example.atlazdb.enums.UserStatus;
import com.example.atlazdb.model.User;
import com.example.atlazdb.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserResponseDTO> list() {
        return service.listAll().stream()
            .map(UserResponseDTO::new)
            .toList();
    }

    @GetMapping("/{id}")
    public UserResponseDTO findById(@PathVariable Long id) {
        return service.findById(id)
            .map(UserResponseDTO::new)
            .orElse(null);
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
    @GetMapping ("/user-status")
    public ResponseEntity<UserStatus[]> getUserStatuses(){
        return ResponseEntity.ok(UserStatus.values());
    }
}