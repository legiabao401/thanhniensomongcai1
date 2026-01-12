package mongcai1.thanhniensomongcai1.repository;

import mongcai1.thanhniensomongcai1.model.User;
import mongcai1.thanhniensomongcai1.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByUsernameAndIsActiveTrue(String username);
    
    boolean existsByUsername(String username);
    
    List<User> findByRole(UserRole role);
    
    List<User> findByIsActiveTrue();
    
    List<User> findAllByOrderByCreatedAtDesc();
}
