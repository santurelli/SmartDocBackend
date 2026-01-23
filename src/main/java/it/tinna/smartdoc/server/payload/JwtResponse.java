package it.tinna.smartdoc.server.payload;

import java.util.List;

import it.tinna.smartdoc.shared.dto.login.UtenteDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UtenteDto user;

    public JwtResponse(String accessToken, UtenteDto user) {
        this.token = accessToken;
        this.user = user;
    }
}
