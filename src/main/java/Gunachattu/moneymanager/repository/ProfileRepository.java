package Gunachattu.moneymanager.repository;

import Gunachattu.moneymanager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository <ProfileEntity,Long>{



    Optional<ProfileEntity> findByEmail(String email);
    
    Optional<ProfileEntity> findByActivationToken(String activationToken);
}
