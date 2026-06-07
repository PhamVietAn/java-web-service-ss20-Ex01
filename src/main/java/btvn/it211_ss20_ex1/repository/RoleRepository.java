package btvn.it211_ss20_ex1.repository;

import btvn.it211_ss20_ex1.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}