package com.nicha.identityservice.repository;

import com.nicha.identityservice.entity.Permission;
import com.nicha.identityservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
}
