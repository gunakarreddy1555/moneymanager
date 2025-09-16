package Gunachattu.moneymanager.service;

import Gunachattu.moneymanager.dto.AuthDto;
import Gunachattu.moneymanager.dto.ProfileDto;
import Gunachattu.moneymanager.entity.ProfileEntity;
import Gunachattu.moneymanager.repository.ProfileRepository;
import Gunachattu.moneymanager.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public ProfileDto registerProfile(ProfileDto profileDto) {
        ProfileEntity newprofile = toEntity(profileDto);
        newprofile.setActivationToken(UUID.randomUUID().toString());

        newprofile = profileRepository.save(newprofile);
        String activationLink = "http://localhost:8080/api/v1.0/activate?token=" + newprofile.getActivationToken();
        String subject = "Activate Your Money Manager Acount";
        String body = "Click on the following link to activate your Money Manager Acount: " + activationLink;
        emailService.sendEmail(newprofile.getEmail(), subject, body);
        return toDTO(newprofile);
    }

    public ProfileEntity toEntity(ProfileDto profileDto) {
        return ProfileEntity.builder().id(profileDto.getId()).fullName(profileDto.getFullName()).email(profileDto.getEmail()).password(passwordEncoder.encode(profileDto.getPassword())).profileImageUrl(profileDto.getProfileImageUrl()).crearedAt(profileDto.getCrearedAt()).updatedAt(profileDto.getUpdatedAt()).build();
    }

    public ProfileDto toDTO(ProfileEntity profileEntity) {
        return ProfileDto.builder().id(profileEntity.getId()).fullName(profileEntity.getFullName()).email(profileEntity.getEmail()).profileImageUrl(profileEntity.getProfileImageUrl()).crearedAt(profileEntity.getCrearedAt()).updatedAt(profileEntity.getUpdatedAt()).build();
    }

    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken).map(profile -> {
            profile.setIsActive(true);
            profileRepository.save(profile);
            return true;
        }).orElse(false);
    }

    public boolean isAccountActive(String email) {
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive).orElse(false);

    }

    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + authentication.getName()));

    }

    public ProfileDto getPublicProfile(String email) {

        ProfileEntity currentUser = null;
        if (email == null) {
            currentUser = getCurrentProfile();
        } else {
            currentUser = profileRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));
        }
        return ProfileDto.builder().id(currentUser.getId()).fullName(currentUser.getFullName()).email(currentUser.getEmail()).profileImageUrl(currentUser.getProfileImageUrl()).crearedAt(currentUser.getCrearedAt()).updatedAt(currentUser.getUpdatedAt()).build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDto authDto) {
        try {

            System.out.println("===============================================================================================================================");
            System.out.println("token generatino =============================");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(), authDto.getPassword()));
            //generate jwt token
            String token = jwtUtil.generateToken(authDto.getEmail());
            Claims claims = jwtUtil.extractAllClaims(token);

            // Log token times
            System.out.println("Token Created Time: " + claims.getIssuedAt());
            System.out.println("Token Expiry Time : " + claims.getExpiration());
            return Map.of("token", token, "user", getPublicProfile(authDto.getEmail()));
        } catch (Exception e) {
            throw new RuntimeException("Invalid username and password");
        }
    }
}
