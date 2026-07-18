package com.example.crudtest.config;

import com.example.crudtest.model.Permission;
import com.example.crudtest.model.Role;
import com.example.crudtest.model.User;
import com.example.crudtest.repository.PermissionRepository;
import com.example.crudtest.repository.RoleRepository;
import com.example.crudtest.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepo;
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public DataSeeder(PermissionRepository permissionRepo, RoleRepository roleRepo,
                      UserRepository userRepo, PasswordEncoder encoder) {
        this.permissionRepo = permissionRepo;
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        // ---- Permissions ----
        Permission create = getOrCreatePermission("STUDENT_CREATE");
        Permission read = getOrCreatePermission("STUDENT_READ");
        Permission update = getOrCreatePermission("STUDENT_UPDATE");
        Permission delete = getOrCreatePermission("STUDENT_DELETE");

        // ---- Roles ----
        Role adminRole = getOrCreateRole("ADMIN", Set.of(create, read, update, delete));
        Role managerRole = getOrCreateRole("MANAGER", Set.of(create, read, update));
        Role userRole = getOrCreateRole("USER", Set.of(read));

        // ---- Default users ----
        createUserIfNotExists("admin", "admin123", Set.of(adminRole));
        createUserIfNotExists("manager", "manager123", Set.of(managerRole));
        createUserIfNotExists("user", "user123", Set.of(userRole));
    }

    private Permission getOrCreatePermission(String name) {
        return permissionRepo.findByName(name).orElseGet(() -> {
            Permission p = new Permission();
            p.setName(name);
            return permissionRepo.save(p);
        });
    }

    private Role getOrCreateRole(String name, Set<Permission> permissions) {
        return roleRepo.findByName(name).orElseGet(() -> {
            Role r = new Role();
            r.setName(name);
            r.setPermissions(new HashSet<>(permissions));
            return roleRepo.save(r);
        });
    }

    private void createUserIfNotExists(String username, String rawPassword, Set<Role> roles) {
        if (userRepo.findByUsername(username).isEmpty()) {
            User u = new User();
            u.setUsername(username);
            u.setPassword(encoder.encode(rawPassword));
            u.setRoles(roles);
            userRepo.save(u);
        }
    }
}