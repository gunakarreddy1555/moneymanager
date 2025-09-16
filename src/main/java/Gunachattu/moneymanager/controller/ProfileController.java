package Gunachattu.moneymanager.controller;

import Gunachattu.moneymanager.dto.AuthDto;
import Gunachattu.moneymanager.dto.ProfileDto;
import Gunachattu.moneymanager.entity.ProfileEntity;
import Gunachattu.moneymanager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDto> registerProfile(@RequestBody ProfileDto profileDTO) {

        ProfileDto regisProfileDto = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(regisProfileDto);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token) {
        boolean isActivated = profileService.activateProfile(token);
        if (isActivated) {
            return ResponseEntity.ok("Profile activated Successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation Token Not Found or already used");
        }
    }
    @GetMapping("/login")
    public ResponseEntity<Map<String,Object>> Login(@RequestBody AuthDto authDto ) {

        System.out.println("------------------------------------------------------------------");
        System.out.println(authDto
        );
        try{
            if (!profileService.isAccountActive(authDto.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Account is not Active, Please activate your account First..."));
            }
            Map<String,Object> response = profileService.authenticateAndGenerateToken(authDto);
            System.out.println(response);
            return ResponseEntity.ok(response);
        }catch (Exception e){
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }
    @GetMapping("/test")
    public String test(){

        return "test Successfully";
    }
}