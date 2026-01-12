package mongcai1.thanhniensomongcai1.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import mongcai1.thanhniensomongcai1.model.User;
import mongcai1.thanhniensomongcai1.model.UserRole;
import mongcai1.thanhniensomongcai1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Đăng nhập
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        Optional<User> userOpt = userService.authenticate(request.getUsername(), request.getPassword());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Lưu thông tin user vào session
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userRole", user.getRole().name());
            
            // Trả về thông tin user (không bao gồm password)
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đăng nhập thành công");
            response.put("user", new UserInfo(user));
            
            return ResponseEntity.ok(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Tên đăng nhập hoặc mật khẩu không chính xác");
        return ResponseEntity.status(401).body(response);
    }
    
    /**
     * Đăng xuất
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đăng xuất thành công");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Kiểm tra trạng thái đăng nhập
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkAuth(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("authenticated", true);
                response.put("user", new UserInfo(userOpt.get()));
                return ResponseEntity.ok(response);
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", false);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Lấy thông tin user hiện tại
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                return ResponseEntity.ok(new UserInfo(userOpt.get()));
            }
        }
        
        return ResponseEntity.status(401).body(Map.of("message", "Chưa đăng nhập"));
    }
    
    // DTO cho request đăng nhập
    public static class LoginRequest {
        private String username;
        private String password;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    // DTO để trả về thông tin user (không bao gồm password)
    public static class UserInfo {
        private Long id;
        private String username;
        private String fullName;
        private String email;
        private String role;
        private Boolean isActive;
        
        public UserInfo(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullName = user.getFullName();
            this.email = user.getEmail();
            this.role = user.getRole().name();
            this.isActive = user.getIsActive();
        }
        
        // Getters
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public Boolean getIsActive() { return isActive; }
    }
}
