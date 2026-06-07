package btvn.it211_ss20_ex1.security;

import btvn.it211_ss20_ex1.entity.Employee;
import btvn.it211_ss20_ex1.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhân viên"));

        return User.builder()
                .username(employee.getUsername())
                .password(employee.getPassword())
                .disabled(!employee.getActive())
                .authorities(
                        employee.getRoles()
                                .stream()
                                .map(role -> new SimpleGrantedAuthority(role.getName()))
                                .toList()
                )
                .build();
    }
}