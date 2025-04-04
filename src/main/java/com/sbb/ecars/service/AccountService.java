package com.sbb.ecars.service;

import com.sbb.ecars.domain.Account;
import com.sbb.ecars.dto.AccountDto;
import com.sbb.ecars.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    // 회원 가입
    @Transactional
    public int registerAccount(AccountDto dto) {
        if (accountRepository.findByEmail(dto.getEmail()).isPresent()) {
            return 1;  // 이미 존재하는 이메일
        }

        if (!isValidPassword(dto.getPassword())) {
            return 0;  // 비밀번호 유효성 실패
        }

        Account newAccount = Account.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .isAdmin(false)
                .build();
        accountRepository.save(newAccount);
        return 2;  // 성공
    }

    public Optional<Account> findAccountById(String id) {
        return accountRepository.findById(id);
    }

    // ID 중복 확인
    public boolean isIdAvailable(String id) {
        return !accountRepository.existsById(id);
    }

    // Email 중복 확인
    public boolean isEmailAvailable(String email) {
        return !accountRepository.existsByEmail(email);
    }

    // Email로 ID 찾기
    public String findIdByEmail(String email) {
        Optional<Account> account = accountRepository.findByEmail(email);
        return account.map(Account::getId).orElse("가입된 아이디가 없습니다.");
    }

    public boolean validateUserByIdAndEmail(String id, String email) {
        Optional<Account> account = accountRepository.findById(id);
        return account.map(acc -> acc.getEmail().equals(email)).orElse(false);
    }

    public boolean updatePassword(String id, String newPassword) {
        if (!isValidPassword(newPassword)) return false;

        Optional<Account> accountOpt = accountRepository.findById(id);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.setPassword(passwordEncoder.encode(newPassword));
            accountRepository.save(account);
            return true;
        }
        return false;
    }

    // 비밀번호 유효성 검사 메서드
    private boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
