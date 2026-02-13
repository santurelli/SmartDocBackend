package it.tinna.smartdoc.shared.dto.documenti;

import java.util.List;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class DdtDto extends DocumentoDto {

    @Expose
    private String dataTrasporto;
    private List<Integer> idConfOrdine;
    private List<Integer> idPreventivi;
    @Expose
    private String oraTrasporto;

}
