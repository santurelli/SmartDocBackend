package it.tinna.smartdoc.shared.dto.cassa;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PuntoSerieCassaDto {

    @Expose
    private String data; // dd/MM/yyyy
    @Expose
    private BigDecimal saldo;
}
