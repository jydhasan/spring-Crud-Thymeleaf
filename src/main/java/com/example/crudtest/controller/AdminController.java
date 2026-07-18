package com.example.crudtest.controller;

import com.example.crudtest.model.Permission;
import com.example.crudtest.model.Role;
import com.example.crudtest.model.User;
import com.example.crudtest.repository.PermissionRepository;
import com.example.crudtest.repository.RoleRepository;
import com.example.crudtest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PermissionRepository permissionRepository;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("roleCount", roleRepository.count());
        model.addAttribute("permissionCount", permissionRepository.count());
        return "admin/dashboard";
    }

    // ---------------- USERS ----------------

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/user_list";
    }

    @GetMapping("/users/{id}/roles")
    public String editUserRoles(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id: " + id));
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleRepository.findAll());
        return "admin/edit_user_roles";
    }

    @PostMapping("/users/{id}/roles")
    public String updateUserRoles(@PathVariable Long id,
                                  @RequestParam(value = "roleIds", required = false) List<Long> roleIds) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id: " + id));

        Set<Role> newRoles = new HashSet<>();
        if (roleIds != null) {
            newRoles.addAll(roleRepository.findAllById(roleIds));
        }
        user.setRoles(newRoles);
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    // ---------------- ROLES ----------------

    @GetMapping("/roles")
    public String listRoles(Model model) {
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/role_list";
    }

    @GetMapping("/roles/new")
    public String newRoleForm(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("allPermissions", permissionRepository.findAll());
        return "admin/role_form";
    }

    @PostMapping("/roles")
    public String saveRole(@RequestParam String name,
                           @RequestParam(value = "permissionIds", required = false) List<Long> permissionIds) {
        Role role = new Role();
        role.setName(name.toUpperCase());
        Set<Permission> perms = new HashSet<>();
        if (permissionIds != null) {
            perms.addAll(permissionRepository.findAllById(permissionIds));
        }
        role.setPermissions(perms);
        roleRepository.save(role);
        return "redirect:/admin/roles";
    }

    @GetMapping("/roles/{id}/edit")
    public String editRoleForm(@PathVariable Long id, Model model) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role id: " + id));
        model.addAttribute("role", role);
        model.addAttribute("allPermissions", permissionRepository.findAll());
        return "admin/role_form";
    }

    @PostMapping("/roles/{id}")
    public String updateRole(@PathVariable Long id,
                             @RequestParam String name,
                             @RequestParam(value = "permissionIds", required = false) List<Long> permissionIds) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role id: " + id));
        role.setName(name.toUpperCase());
        Set<Permission> perms = new HashSet<>();
        if (permissionIds != null) {
            perms.addAll(permissionRepository.findAllById(permissionIds));
        }
        role.setPermissions(perms);
        roleRepository.save(role);
        return "redirect:/admin/roles";
    }

    @PostMapping("/roles/{id}/delete")
    public String deleteRole(@PathVariable Long id) {
        // age eta shob user theke sorate hobe, na hole FK error dibe
        List<User> usersWithRole = userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getId().equals(id)))
                .toList();
        usersWithRole.forEach(u -> {
            u.getRoles().removeIf(r -> r.getId().equals(id));
            userRepository.save(u);
        });
        roleRepository.deleteById(id);
        return "redirect:/admin/roles";
    }

    // ---------------- PERMISSIONS ----------------

    @GetMapping("/permissions")
    public String listPermissions(Model model) {
        model.addAttribute("permissions", permissionRepository.findAll());
        return "admin/permission_list";
    }

    @PostMapping("/permissions")
    public String createPermission(@RequestParam String name) {
        String upperName = name.toUpperCase();
        if (permissionRepository.findByName(upperName).isEmpty()) {
            Permission p = new Permission();
            p.setName(upperName);
            permissionRepository.save(p);
        }
        return "redirect:/admin/permissions";
    }

    @PostMapping("/permissions/{id}/delete")
    public String deletePermission(@PathVariable Long id) {
        permissionRepository.deleteById(id);
        return "redirect:/admin/permissions";
    }
}