package it.tinna.smartdoc.server;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordGeneratorTest {

    @Test
    public void generatePasswords() {
        // Spring Security nel progetto usa BCryptPasswordEncoder
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Sostituisci "admin" con la tua password in chiaro
        String rawPassword = "Supp0rt@";
        
        String encodedPassword = encoder.encode(rawPassword);
        
        System.out.println("\n=========================================");
        System.out.println("Password in chiaro: " + rawPassword);
        System.out.println("Password cifrata  : " + encodedPassword);
        System.out.println("=========================================\n");
        
        // Se hai bisogno di codificarne più di una contemporaneamente, copia questa riga:
        // System.out.println("Segreta123! -> " + encoder.encode("Segreta123!"));
    }
}
