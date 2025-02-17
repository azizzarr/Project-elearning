package tn.siga.repositories;


import java.util.Optional;
import tn.siga.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RoleRepository extends JpaRepository<Role, Long> {

 ;

    Optional<Role> findByName(String name);

}
