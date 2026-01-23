package it.tinna.smartdoc.shared.dto.clienti;

import java.util.List;

// import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;
import it.tinna.smartdoc.shared.dto.contatti.ContattoDto;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Setter
@Getter
@NoArgsConstructor
public class BaseClienteDto extends BaseDto {

    // @Expose
    private String abi;

    private String banca;

    public void setBanca(String banca) {
        this.banca = banca;
        this.descrizioneBanca = banca;
    }

    // @Expose
    private String bic;

    // @Expose
    private String cab;

    // @Expose
    private String cin;

    // @Expose
    private String citta;

    // @Expose
    private String codice;

    // @Expose
    private String codiceFiscale;

    // @Expose
    private String codSia;

    // @Expose
    private String conto;

    // @Expose
    private String denominazione;

    // @Expose
    private String descrizioneAvviso;

    // @Expose
    private String descrizioneBanca;

    private Integer documentiMail;

    // @Expose
    private List<ContattoDto> elencoContatti;

    // @Expose
    private List<IndirizzoDto> elencoIndirizzi;

    // @Expose
    private String iban;

    // private Integer id;
    private Integer idAliquotaIva;

    // @Expose
    private Integer idAvviso;

    // @Expose
    private Integer idNota;

    // @Expose
    private Integer idRisorsa;

    private Integer idTipoPagamento;

    // @Expose
    private Integer idTipoPorto;

    // @Expose
    private Integer idVettore;

    // @Expose
    private String note;

    // @Expose
    private String partitaIva;

    private String pecPrincipale; // memorizza la pec della sede operativa;
                                  // usata nell'esportazione excel dell'elenco
                                  // clienti/fornitori

    // @Expose
    private String referente;

    // @Expose
    private TipologiaClienteFornitore tipologia;

    // @Expose
    private String type; // C oppure F oppure D

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BaseClienteDto) {
            return ((BaseClienteDto) obj).getId() == this.getId();
        }
        return false;
    }

}
