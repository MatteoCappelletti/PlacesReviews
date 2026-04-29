package placesreviews.app.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import placesreviews.app.persistence.entity.User;
import placesreviews.app.persistence.repository.UserRepository;

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
    public Result modify(String oldUsername, String usernameFromForm, String roleFromForm) {
        if (oldUsername == null || oldUsername.isBlank()) {
            return Result.error("Username is required");
        }
        if (usernameFromForm == null || usernameFromForm.isBlank()) {
            return Result.error("Username from form is required");
        }
        if (!"admin".equalsIgnoreCase(roleFromForm) && !"user".equalsIgnoreCase(roleFromForm)) {
            return Result.error("Role must be either user or admin");
        }
        if (!oldUsername.equals(usernameFromForm)) {
            if (userRepository.existsByUsername(usernameFromForm)) {
                return Result.error("Username already exists");
            }
        }

        Optional<User> oldByUsername = userRepository.findByUsername(oldUsername);
        User user = oldByUsername.get();
        user.setUsername(usernameFromForm);
        user.setRole(roleFromForm);

        return Result.success();
    }

    @Transactional
    public Result insert(String username, String password, String verifiedPassword) {
        if (username == null || username.length() < 8) {
            return Result.error("Username must be at least 8 characters.");
        }

        if (password == null || password.length() < 6) {
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

    public record Result(boolean ok, String errorMessage) {
        public static Result success() {
            return new Result(true, null);
        }

        public static Result error(String message) {
            return new Result(false, message);
        }
    }
}
