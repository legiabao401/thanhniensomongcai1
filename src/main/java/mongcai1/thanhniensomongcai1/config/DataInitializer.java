package mongcai1.thanhniensomongcai1.config;

import mongcai1.thanhniensomongcai1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Khởi tạo dữ liệu mặc định khi ứng dụng khởi động
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserService userService;
    
    @Override
    public void run(String... args) throws Exception {
        // Khởi tạo tài khoản admin mặc định nếu chưa có
        userService.initDefaultAdmin();
        System.out.println("✅ Default admin account initialized (admin/admin123)");
    }
}
