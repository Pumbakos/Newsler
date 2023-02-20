package pl.newsler.components.signup;

import lombok.RequiredArgsConstructor;
import pl.newsler.commons.model.NLId;
import pl.newsler.commons.model.NLToken;
import pl.newsler.commons.model.NLUuid;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
class ConfirmationTokenService {
    //! TODO: Use DB sequence
    private static final AtomicLong sequence = new AtomicLong(0);
    private final IConfirmationTokenRepository confirmationTokenRepository;

    public NLConfirmationToken save(NLConfirmationToken confirmationToken) {
        confirmationToken.setId(NLId.of(sequence.getAndAdd(1)));
        return confirmationTokenRepository.save(confirmationToken);
    }

    public Optional<NLConfirmationToken> getToken(NLToken token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public void setConfirmationDate(NLToken token) {
        confirmationTokenRepository.updateConfirmationDate(token, LocalDateTime.now());
    }

    public boolean setTokenExpired(NLUuid userId) {
        return confirmationTokenRepository.updateTokenExpired(userId);
    }
}