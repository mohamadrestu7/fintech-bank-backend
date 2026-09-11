package com.restu.fintech.role.services;

import com.restu.fintech.exceptions.BadRequestException;
import com.restu.fintech.exceptions.NotFoundException;
import com.restu.fintech.res.Response;
import com.restu.fintech.role.entity.Role;
import com.restu.fintech.role.repo.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{

    private final RoleRepo roleRepo;

    @Override
    public Response<Role> createRole(Role role) {

        if(roleRepo.findByName(role.getName()).isPresent()) {
            throw new BadRequestException("Role already exists");
        }

        Role savedRole = roleRepo.save(role);

        return Response.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role saved successfully")
                .data(savedRole)
                .build();
    }

    @Override
    public Response<Role> updateRole(Role role) {

        Role existedRole = roleRepo.findById(role.getId())
                .orElseThrow(() -> new NotFoundException("Role not found"));

        existedRole.setName(role.getName());

        Role updatedRole = roleRepo.save(existedRole);

        return Response.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role updated successfully")
                .data(updatedRole)
                .build();
    }

    @Override
    public Response<List<Role>> getAllRoles() {

        List<Role> roles = roleRepo.findAll();

        return Response.<List<Role>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Roles retrieved successfully")
                .data(roles)
                .build();
    }

    @Override
    public Response<?> deleteRole(Long id) {

        if (!roleRepo.existsById(id)){
            throw new NotFoundException("Role Not Found");
        }

        roleRepo.deleteById(id);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role deleted successfully")
                .build();
    }
}
