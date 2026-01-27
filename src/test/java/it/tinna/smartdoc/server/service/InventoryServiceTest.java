package it.tinna.smartdoc.server.service;

import it.tinna.smartdoc.shared.dto.prodotti.MovimentoMagazzinoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InventoryServiceTest {

    private InventoryService service;

    @BeforeEach
    public void setUp() {
        service = new InventoryService();
    }

    @Test
    public void testCalculateExistence_Basic() {
        List<MovimentoMagazzinoDto> movements = new ArrayList<>();
        
        // Carico (Increase)
        MovimentoMagazzinoDto m1 = new MovimentoMagazzinoDto();
        m1.setTipoMovimento("I");
        m1.setQuantita(100.0);
        movements.add(m1);
        
        // Scarico (Decrease)
        MovimentoMagazzinoDto m2 = new MovimentoMagazzinoDto();
        m2.setTipoMovimento("U");
        m2.setQuantita(30.0);
        movements.add(m2);
        
        double result = service.calculateExistence(movements);
        assertEquals(70.0, result, "Existence should be 70.0 (100 - 30)");
    }

    @Test
    public void testCalculateExistence_Empty() {
        double result = service.calculateExistence(new ArrayList<>());
        assertEquals(0.0, result);
    }

    @Test
    public void testCalculateExistence_Null() {
        double result = service.calculateExistence(null);
        assertEquals(0.0, result);
    }

    @Test
    public void testCalculateAvailability_Basic() {
        double existence = 100.0;
        double committed = 20.0;
        double result = service.calculateAvailability(existence, committed);
        assertEquals(80.0, result, "Availability should be 80.0 (100 - 20)");
    }

    @Test
    public void testComplexScenario() {
        List<MovimentoMagazzinoDto> movements = new ArrayList<>();
        
        // Initial stock load
        movements.add(createMovement("I", 500.0));
        // Sale
        movements.add(createMovement("U", 150.0));
        // Purchase return (Decrease)
        movements.add(createMovement("U", 20.0));
        // New arrivals
        movements.add(createMovement("I", 100.0));
        
        double existence = service.calculateExistence(movements);
        assertEquals(430.0, existence, "Existence should be 430.0");
        
        // 50 items already sold but not shipped (Committed)
        double committed = 50.0;
        double availability = service.calculateAvailability(existence, committed);
        assertEquals(380.0, availability, "Availability should be 380.0");
    }

    private MovimentoMagazzinoDto createMovement(String type, double qty) {
        MovimentoMagazzinoDto m = new MovimentoMagazzinoDto();
        m.setTipoMovimento(type);
        m.setQuantita(qty);
        return m;
    }
}
