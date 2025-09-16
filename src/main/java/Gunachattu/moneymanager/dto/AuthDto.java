package Gunachattu.moneymanager.dto;

import lombok.*;

@Data

//@NoArgsConstructor
@Builder
@RequiredArgsConstructor
public class AuthDto {
    private String email;
    private String password;
    private String token;

    public AuthDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public AuthDto(String email, String password, String token) {
        this.email = email;
        this.password = password;
        this.token = token;
    }
}
