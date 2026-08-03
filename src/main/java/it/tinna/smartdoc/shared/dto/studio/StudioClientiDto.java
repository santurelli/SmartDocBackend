package it.tinna.smartdoc.shared.dto.studio;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class StudioClientiDto implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long   idDelega;
    private Long   idStudio;
    private Long   idCliente;
    private String ragioneSociale;
    private String partitaIva;
    private String email;
    private String regimeFiscale;
    private Integer tipoAccount;
    private String statoDelega; // PENDING, ACTIVE, REVOKED
    private Date   dtRichiesta;
    private Date   dtApprovazione;
    private Integer countDocMese;
    private Integer countDocPendenti;
}
