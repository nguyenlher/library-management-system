package com.library.auth_service.dto;

import com.library.auth_service.entity.Role;
import com.library.auth_service.entity.Status;

public class UpdateUserDto {

    private Role role;
    private Status status;

    // Constructors
    public UpdateUserDto() {}

    public UpdateUserDto(Role role, Status status) {
        this.role = role;
        this.status = status;
    }

    // Getters and Setters
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}