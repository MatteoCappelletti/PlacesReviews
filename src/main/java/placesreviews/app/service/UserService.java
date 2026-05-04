package placesreviews.app.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import placesreviews.app.persistence.entity.User;
import placesreviews.app.persistence.repository.UserRepository;
import placesreviews.app.service.model.Result;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.listAll();
    }

    public User getByUsername(String username) {
        return userRepository.getByUsername(username);
    }

    @Transactional
    public Result insert(String username, String password, String verifiedPassword) {
        if (username == null || username.length() < 8) {
            return Result.error("Username must be at least 8 characters.");
        }

        if (password == null || password.length() < 8) {
            return Result.error("Password must be at least 8 characters.");
        }

        if (verifiedPassword == null || verifiedPassword.isBlank()) {
            return Result.error("Verify password is required.");
        }

        boolean passwordsMatches = password.equals(verifiedPassword);
        if (!passwordsMatches) {
            return Result.error("Passwords must match.");
        }

        String trimmed = username.strip();
        if (userRepository.existsByUsername(trimmed)) {
            return Result.error("Username already taken.");
        }

        String passwordHash = BcryptUtil.bcryptHash(password);

        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(passwordHash);
        u.setRole("user");
        u.setCreatedAt(OffsetDateTime.now());
        userRepository.persist(u);

        return Result.success();
    }

    @Transactional
    public Result setRoleToModeratorFromId(int id) {
        User user = userRepository.findById(id);

        user.setRole("moderator");

        return Result.success();
    }
}
