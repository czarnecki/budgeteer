package org.wickedsource.budgeteer.service.user;

import de.adesso.budgeteer.core.user.MailNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wickedsource.budgeteer.persistence.user.UserEntity;
import org.wickedsource.budgeteer.persistence.user.UserRepository;
import org.wickedsource.budgeteer.persistence.user.VerificationTokenEntity;
import org.wickedsource.budgeteer.persistence.user.VerificationTokenRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    /**
     * Looks for a token in the database that matches the passing token.
     * <p>
     * If none is available, INVALID (-1) is returned.
     * If it is, EXPIRED (-2) is returned.
     * If it is valid, the mail address of the corresponding user is validated and the token is deleted.
     * Then VALID (0) is returned.
     *
     * @param token the token to look for
     * @return returns a status code (INVALID, EXPIRED, VALID)
     */
    public int validateVerificationToken(String token) {
        VerificationTokenEntity verificationTokenEntity = verificationTokenRepository.findByToken(token);

        if (verificationTokenEntity == null)
            return TokenStatus.INVALID.statusCode();

        UserEntity userEntity = verificationTokenEntity.getUserEntity();

        if (verificationTokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            return TokenStatus.EXPIRED.statusCode();
        }

        userEntity.setMailVerified(true);
        userRepository.save(userEntity);
        verificationTokenRepository.delete(verificationTokenEntity);
        return TokenStatus.VALID.statusCode();
    }

    /**
     * Searches for a user using a mail address.
     * If not found, a MailNotFoundException is thrown, otherwise the user is returned.
     *
     * @param mail the mail address to look for
     * @return returns the user found with the mail address
     * @throws MailNotFoundException
     */
    public UserEntity getUserByMail(String mail) throws MailNotFoundException {
        UserEntity userEntity = userRepository.findByMail(mail);

        if (userEntity == null)
            throw new MailNotFoundException();
        else
            return userEntity;
    }

    /**
     * Searches for a user using the ID.
     * If one is found, the user is returned, otherwise a UserIdNotFoundException is thrown.
     *
     * @param id the ID to look for
     * @return returns the user found with the ID
     * @throws UserIdNotFoundException
     */
    public UserEntity getUserById(long id) throws UserIdNotFoundException {
        return userRepository.findById(id).orElseThrow(UserIdNotFoundException::new);
    }
}
