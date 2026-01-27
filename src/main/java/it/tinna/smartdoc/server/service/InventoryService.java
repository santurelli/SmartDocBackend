package it.tinna.smartdoc.server.service;

import it.tinna.smartdoc.shared.dto.prodotti.MovimentoMagazzinoDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    /**
     * Calculates existence (giacenza) based on a list of movements.
     * Increase for 'I' (Ingresso), decrease for 'U' (Uscita).
     */
    public double calculateExistence(List<MovimentoMagazzinoDto> movements) {
        if (movements == null) return 0.0;
        
        double existence = 0.0;
        for (MovimentoMagazzinoDto m : movements) {
            if (m == null || m.getQuantita() == null) continue;
            
            if ("I".equals(m.getTipoMovimento())) {
                existence += m.getQuantita();
            } else if ("U".equals(m.getTipoMovimento())) {
                existence -= m.getQuantita();
            }
        }
        return existence;
    }

    /**
     * Calculates availability: Existence - Committed (Impegnato).
     */
    public double calculateAvailability(double existence, double committed) {
        return existence - committed;
    }
}
