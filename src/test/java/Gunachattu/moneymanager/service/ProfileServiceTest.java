package Gunachattu.moneymanager.service;

import Gunachattu.moneymanager.dto.AuthDto;
import Gunachattu.moneymanager.dto.ProfileDto;
import Gunachattu.moneymanager.entity.ProfileEntity;
import Gunachattu.moneymanager.repository.ProfileRepository;
import Gunachattu.moneymanager.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private ProfileService profileService;

    private ProfileDto profileDto;
    private ProfileEntity profileEntity;

    @BeforeEach
    void setUp() {
        profileDto = ProfileDto.builder()
                .id(1L)
                .fullName("Test User")
                .email("test@example.com")
                .password("password")
                .profileImageUrl("http://example.com/image.jpg")
                .build();

        profileEntity = ProfileEntity.builder()
                .id(1L)
                .fullName("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .profileImageUrl("http://example.com/image.jpg")
                .isActive(false)
                .activationToken(UUID.randomUUID().toString())
                .build();
    }

    @Test
    void registerProfile_ShouldReturnProfileDto() {
        when(profileRepository.save(any(ProfileEntity.class))).thenReturn(profileEntity);
        when(passwordEncoder.encode(profileDto.getPassword())).thenReturn("encodedPassword");

        ProfileDto result = profileService.registerProfile(profileDto);

        assertNotNull(result);
        assertEquals(profileDto.getFullName(), result.getFullName());
        verify(profileRepository, times(1)).save(any(ProfileEntity.class));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void toEntity_ShouldReturnProfileEntity() {
        when(passwordEncoder.encode(profileDto.getPassword())).thenReturn("encodedPassword");
        ProfileEntity result = profileService.toEntity(profileDto);

        assertNotNull(result);
        assertEquals(profileDto.getFullName(), result.getFullName());
        assertEquals("encodedPassword", result.getPassword());
    }

    @Test
    void toDTO_ShouldReturnProfileDto() {
        ProfileDto result = profileService.toDTO(profileEntity);

        assertNotNull(result);
        assertEquals(profileEntity.getFullName(), result.getFullName());
        assertEquals(profileEntity.getEmail(), result.getEmail());
    }

    @Test
    void activateProfile_ShouldActivateProfile() {
        String activationToken = UUID.randomUUID().toString();
        profileEntity.setActivationToken(activationToken);
        when(profileRepository.findByActivationToken(activationToken)).thenReturn(Optional.of(profileEntity));
        when(profileRepository.save(profileEntity)).thenReturn(profileEntity);

        boolean result = profileService.activateProfile(activationToken);

        assertTrue(result);
        assertTrue(profileEntity.getIsActive());
        verify(profileRepository, times(1)).save(profileEntity);
    }

    @Test
    void activateProfile_ShouldReturnFalseIfTokenNotFound() {
        String activationToken = "invalid_token";
        when(profileRepository.findByActivationToken(activationToken)).thenReturn(Optional.empty());

        boolean result = profileService.activateProfile(activationToken);

        assertFalse(result);
        verify(profileRepository, never()).save(any(ProfileEntity.class));
    }

    @Test
    void isAccountActive_ShouldReturnTrueIfActive() {
        String email = "test@example.com";
        profileEntity.setIsActive(true);
        when(profileRepository.findByEmail(email)).thenReturn(Optional.of(profileEntity));

        boolean result = profileService.isAccountActive(email);

        assertTrue(result);
    }

    @Test
    void isAccountActive_ShouldReturnFalseIfNotActive() {
        String email = "test@example.com";
        profileEntity.setIsActive(false);
        when(profileRepository.findByEmail(email)).thenReturn(Optional.of(profileEntity));

        boolean result = profileService.isAccountActive(email);

        assertFalse(result);
    }

    @Test
    void isAccountActive_ShouldReturnFalseIfProfileNotFound() {
        String email = "test@example.com";
        when(profileRepository.findByEmail(email)).thenReturn(Optional.empty());

        boolean result = profileService.isAccountActive(email);

        assertFalse(result);
    }

    @Test
    void getCurrentProfile_ShouldReturnProfileEntity() {
        String email = "test@example.com";
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(profileRepository.findByEmail(email)).thenReturn(Optional.of(profileEntity));

        ProfileEntity result = profileService.getCurrentProfile();

        assertNotNull(result);
        assertEquals(profileEntity.getEmail(), result.getEmail());
    }

    @Test
    void getCurrentProfile_ShouldThrowExceptionIfProfileNotFound() {
        String email = "test@example.com";
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(profileRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> profileService.getCurrentProfile());
    }

    @Test
    void getPublicProfile_ShouldReturnProfileDtoForCurrentUser() {
        String email = "test@example.com";
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(profileRepository.findByEmail(email)).thenReturn(Optional.of(profileEntity));

        ProfileDto result = profileService.getPublicProfile(null);

        assertNotNull(result);
        assertEquals(profileEntity.getFullName(), result.getFullName());
    }

    @Test
    void getPublicProfile_ShouldReturnProfileDtoForGivenEmail() {
        String email = "another@example.com";
        when(profileRepository.findByEmail(email)).thenReturn(Optional.of(profileEntity));

        ProfileDto result = profileService.getPublicProfile(email);

        assertNotNull(result);
        assertEquals(profileEntity.getFullName(), result.getFullName());
    }

    @Test
    void getPublicProfile_ShouldThrowExceptionIfProfileNotFound() {
        String email = "nonexistent@example.com";
        when(profileRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> profileService.getPublicProfile(email));
    }

    @Test
    void authenticateAndGenerateToken_ShouldReturnTokenAndProfile() {
        AuthDto authDto = new AuthDto("test@example.com", "password");

        // Mock Authentication
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(authDto.getEmail(), authDto.getPassword());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Mock JWT
        when(jwtUtil.generateToken(authDto.getEmail())).thenReturn("test_token");
        when(jwtUtil.extractAllClaims("test_token")).thenReturn(mock(Claims.class));

        // Mock Profile Repository
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setEmail("test@example.com");
        profileEntity.setFullName("Test User");

        when(profileRepository.findByEmail(eq(authDto.getEmail())))
                .thenReturn(Optional.of(profileEntity));

        // Call Service
        Map<String, Object> result = profileService.authenticateAndGenerateToken(authDto);

        // Assertions
        assertNotNull(result);
        assertTrue(result.containsKey("token"));
        assertTrue(result.containsKey("user"));
        assertEquals("test_token", result.get("token"));
        assertEquals(profileEntity, result.get("user"));
    }


    @Test
    void authenticateAndGenerateToken_ShouldThrowExceptionForInvalidCredentials() {
        AuthDto authDto = new AuthDto("test@example.com", "wrongpassword");
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Invalid username and password"));

        assertThrows(RuntimeException.class, () -> profileService.authenticateAndGenerateToken(authDto));
    }
}
