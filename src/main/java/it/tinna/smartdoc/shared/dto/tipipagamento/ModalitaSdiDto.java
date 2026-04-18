package it.tinna.smartdoc.shared.dto.tipipagamento;

import com.google.gson.annotations.Expose;
import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModalitaSdiDto extends BaseDto {
    @Expose
    private String nome; // Enum name (e.g. CARTA_CREDITO)
    @Expose
    private String descrizione; // Enum description (e.g. Carta di credito)
    @Expose
    private String codiceSdi; // SDI code (e.g. MP08)
}
