package com.joongomarket.account;

import com.joongomarket.domain.Account;
import com.joongomarket.domain.Tag;
import com.joongomarket.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .auctionCreatedByWeb(true)
                .auctionUpdatedByWeb(true)
                .build();

        return accountRepository.save(account);
    }

    private void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("[????????????] ?????? ?????? ??????");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email);

        if(account == null){
            throw new UsernameNotFoundException(email);
        }

        return new UserAccount(account);
    }

    public void addTag(Account account, Tag tag) {
        Optional<Account> id = accountRepository.findById(account.getId());
        id.ifPresent(a->a.getTags().add(tag));
    }

    public Set<Tag> getTags(Account account) {
        Optional<Account> id = accountRepository.findById(account.getId());
        return id.orElseThrow().getTags();
    }

    public void removeTag(Account account, Tag tag) {
        Optional<Account> id = accountRepository.findById(account.getId());
        id.ifPresent(a->a.getTags().remove(tag));
    }

    public Set<Zone> getZones(Account account) {
        Optional<Account> id = accountRepository.findById(account.getId());
        return id.orElseThrow().getZones();
    }

    public void addZone(Account account, Zone zone) {
        Optional<Account> id = accountRepository.findById(account.getId());
        id.ifPresent(a->a.getZones().add(zone));
    }

    public void removeZone(Account account, Zone zone) {
        Optional<Account> id = accountRepository.findById(account.getId());
        id.ifPresent(a->a.getZones().remove(zone));
    }
}
