package com.restu.fintech.role.services;

import com.restu.fintech.res.Response;
import com.restu.fintech.role.entity.Role;

import java.util.List;

public interface RoleService {

    Response<Role> createRole(Role role);

    Response<Role> updateRole(Role role);

    Response<List<Role>> getAllRoles();

    Response<?> deleteRole(Long id);
}
