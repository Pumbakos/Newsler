package pl.palubiak.dawid.newsler.user.registration.token;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.palubiak.dawid.newsler.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void save(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public void setConfirmationDate(String token) {
        confirmationTokenRepository.updateConfirmationDate(token, LocalDateTime.now());
    }

    public boolean setTokenExpired(User user) {
        return confirmationTokenRepository.updateTokenExpired(user);
    }
}
