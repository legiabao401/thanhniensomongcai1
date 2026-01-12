package mongcai1.thanhniensomongcai1.controller;

import mongcai1.thanhniensomongcai1.model.User;
import mongcai1.thanhniensomongcai1.model.UserRole;
import mongcai1.thanhniensomongcai1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Lấy danh sách tất cả users (chỉ SUPER_ADMIN)
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (!"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(Map.of("message", "Không có quyền truy cập"));
        }
        
        List<UserResponse> users = userService.findAll().stream()
            .map(UserResponse::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
    
    /**
     * Lấy thông tin một user
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id, HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        Long currentUserId = (Long) session.getAttribute("userId");
        
        // Chỉ SUPER_ADMIN hoặc chính user đó mới được xem
        if (!"SUPER_ADMIN".equals(role) && !id.equals(currentUserId)) {
            return ResponseEntity.status(403).body(Map.of("message", "Không có quyền truy cập"));
        }
        
        return userService.findById(id)
            .map(user -> ResponseEntity.ok(new UserResponse(user)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Tạo user mới (chỉ SUPER_ADMIN)
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request, HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (!"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(Map.of("message", "Không có quyền tạo tài khoản"));
        }
        
        try {
            if (userService.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Tên đăng nhập đã tồn tại"));
            }
            
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setRole(UserRole.valueOf(request.getRole()));
            user.setIsActive(true);
            
            User savedUser = userService.createUser(user);
            return ResponseEntity.ok(new UserResponse(savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    /**
     * Cập nhật user
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request, HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        Long currentUserId = (Long) session.getAttribute("userId");
        
        // SUPER_ADMIN có thể sửa tất cả, người khác chỉ sửa được thông tin của mình
        if (!"SUPER_ADMIN".equals(role) && !id.equals(currentUserId)) {
            return ResponseEntity.status(403).body(Map.of("message", "Không có quyền sửa tài khoản này"));
        }
        
        try {
            User updatedUser = new User();
            updatedUser.setFullName(request.getFullName());
            updatedUser.setEmail(request.getEmail());
            
            // Chỉ SUPER_ADMIN mới được đổi role và trạng thái active
            if ("SUPER_ADMIN".equals(role)) {
                if (request.getRole() != null) {
                    updatedUser.setRole(UserRole.valueOf(request.getRole()));
                }
                if (request.getIsActive() != null) {
                    updatedUser.setIsActive(request.getIsActive());
                }
            }
            
            User saved = userService.updateUser(id, updatedUser);
            return ResponseEntity.ok(new UserResponse(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    /**
     * Đổi mật khẩu
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request, HttpSession session) {
        Long currentUserId = (Long) session.getAttribute("userId");
        String role = (String) session.getAttribute("userRole");
        
        // Chỉ user đó hoặc SUPER_ADMIN mới được đổi mật khẩu
        if (!id.equals(currentUserId) && !"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(Map.of("message", "Không có quyền đổi mật khẩu"));
        }
        
        try {
            // SUPER_ADMIN có thể reset password không cần mật khẩu cũ
            if ("SUPER_ADMIN".equals(role) && !id.equals(currentUserId)) {
                User user = userService.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
                user.setPassword(request.getNewPassword());
                userService.updateUser(id, user);
            } else {
                userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
            }
            return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    /**
     * Xóa user (chỉ SUPER_ADMIN)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        Long currentUserId = (Long) session.getAttribute("userId");
        
        if (!"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(Map.of("message", "Không có quyền xóa tài khoản"));
        }
        
        if (id.equals(currentUserId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không thể xóa chính tài khoản của bạn"));
        }
        
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "Xóa tài khoản thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    /**
     * Toggle trạng thái active
     */
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<?> toggleActive(@PathVariable Long id, HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        Long currentUserId = (Long) session.getAttribute("userId");
        
        if (!"SUPER_ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(Map.of("message", "Không có quyền thay đổi trạng thái tài khoản"));
        }
        
        if (id.equals(currentUserId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không thể vô hiệu hóa chính tài khoản của bạn"));
        }
        
        try {
            User user = userService.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
            if (user.getIsActive()) {
                userService.deactivateUser(id);
            } else {
                userService.activateUser(id);
            }
            User updated = userService.findById(id).get();
            return ResponseEntity.ok(new UserResponse(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    // DTOs
    public static class UserResponse {
        private Long id;
        private String username;
        private String fullName;
        private String email;
        private String role;
        private Boolean isActive;
        private String lastLogin;
        private String createdAt;
        
        public UserResponse(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullName = user.getFullName();
            this.email = user.getEmail();
            this.role = user.getRole().name();
            this.isActive = user.getIsActive();
            this.lastLogin = user.getLastLogin() != null ? user.getLastLogin().toString() : null;
            this.createdAt = user.getCreatedAt() != null ? user.getCreatedAt().toString() : null;
        }
        
        // Getters
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public Boolean getIsActive() { return isActive; }
        public String getLastLogin() { return lastLogin; }
        public String getCreatedAt() { return createdAt; }
    }
    
    public static class CreateUserRequest {
        private String username;
        private String password;
        private String fullName;
        private String email;
        private String role;
        
        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
    
    public static class UpdateUserRequest {
        private String fullName;
        private String email;
        private String role;
        private Boolean isActive;
        
        // Getters and Setters
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }
    
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
        
        // Getters and Setters
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
