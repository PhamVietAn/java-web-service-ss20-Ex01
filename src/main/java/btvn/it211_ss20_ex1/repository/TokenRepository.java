package btvn.it211_ss20_ex1.repository;

import btvn.it211_ss20_ex1.entity.Employee;
import btvn.it211_ss20_ex1.entity.Token;
import btvn.it211_ss20_ex1.entity.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByTokenValue(String tokenValue);

    List<Token> findAllByEmployeeAndRevokedFalseAndExpiredFalse(Employee employee);

    Optional<Token> findByTokenValueAndTokenTypeAndRevokedFalseAndExpiredFalse(
            String tokenValue,
            TokenType tokenType
    );
}