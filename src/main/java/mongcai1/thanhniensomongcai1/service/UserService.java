package mongcai1.thanhniensomongcai1.service;

import mongcai1.thanhniensomongcai1.model.User;
import mongcai1.thanhniensomongcai1.model.UserRole;
import mongcai1.thanhniensomongcai1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Tìm user theo username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Xác thực đăng nhập
     */
    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsernameAndIsActiveTrue(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // So sánh mật khẩu trực tiếp (trong thực tế nên dùng password encoder)
            if (user.getPassword().equals(password)) {
                // Cập nhật thời gian đăng nhập cuối
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
    
    /**
     * Lấy tất cả users
     */
    public List<User> findAll() {
        return userRepository.findAllByOrderByCreatedAtDesc();
    }
    
    /**
     * Tìm user theo id
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Kiểm tra username đã tồn tại
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Tạo user mới
     */
    public User createUser(User user) {
        if (existsByUsername(user.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        return userRepository.save(user);
    }
    
    /**
     * Cập nhật user
     */
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
            .map(user -> {
                if (updatedUser.getFullName() != null) {
                    user.setFullName(updatedUser.getFullName());
                }
                if (updatedUser.getEmail() != null) {
                    user.setEmail(updatedUser.getEmail());
                }
                if (updatedUser.getRole() != null) {
                    user.setRole(updatedUser.getRole());
                }
                if (updatedUser.getIsActive() != null) {
                    user.setIsActive(updatedUser.getIsActive());
                }
                return userRepository.save(user);
            })
            .orElseThrow(() -> new RuntimeException("Không tìm thấy user với id: " + id));
    }
    
    /**
     * Đổi mật khẩu
     */
    public User changePassword(Long id, String oldPassword, String newPassword) {
        return userRepository.findById(id)
            .map(user -> {
                if (!user.getPassword().equals(oldPassword)) {
                    throw new RuntimeException("Mật khẩu cũ không chính xác");
                }
                user.setPassword(newPassword);
                return userRepository.save(user);
            })
            .orElseThrow(() -> new RuntimeException("Không tìm thấy user với id: " + id));
    }
    
    /**
     * Vô hiệu hóa tài khoản
     */
    public User deactivateUser(Long id) {
        return userRepository.findById(id)
            .map(user -> {
                user.setIsActive(false);
                return userRepository.save(user);
            })
            .orElseThrow(() -> new RuntimeException("Không tìm thấy user với id: " + id));
    }
    
    /**
     * Kích hoạt tài khoản
     */
    public User activateUser(Long id) {
        return userRepository.findById(id)
            .map(user -> {
                user.setIsActive(true);
                return userRepository.save(user);
            })
            .orElseThrow(() -> new RuntimeException("Không tìm thấy user với id: " + id));
    }
    
    /**
     * Xóa user (soft delete thông qua deactivate)
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    /**
     * Lấy users theo role
     */
    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Đếm số lượng users active
     */
    public long countActiveUsers() {
        return userRepository.findByIsActiveTrue().size();
    }
    
    /**
     * Khởi tạo admin mặc định nếu chưa có
     */
    public void initDefaultAdmin() {
        if (!existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123"); // Trong thực tế nên hash password
            admin.setFullName("Quản trị viên");
            admin.setEmail("admin@mongcai.gov.vn");
            admin.setRole(UserRole.SUPER_ADMIN);
            admin.setIsActive(true);
            userRepository.save(admin);
        }
    }
}
