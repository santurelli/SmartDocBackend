package it.tinna.smartdoc.shared.dto.login;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;
import it.tinna.smartdoc.shared.dto.configurazione.ConfigurazioneDto;
import it.tinna.smartdoc.shared.dto.municipality.MunicipalityDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UtenteDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Expose
    private String cognome;

    private List<ConfigurazioneDto> configurazione;

    private String email;

    @Expose
    private boolean erroreGenerico;

    @Expose
    private boolean erroreUtenteNonTrovato;

    private int fatturaElettronica;

    private Integer gruppo;

    private String idGruppo;

    private String lastLogin;

    private MunicipalityDto aziendaDto;

    @Expose
    private String nome;

    private String nomeAzienda;

    private String nomeGruppo;

    private String password;

    private Map<String, Integer> permissions;

    private Integer tipoAccount;

    private String username;

    @Expose
    private String tipoStore;

    public String getNomeCognome() {
        return getNome() + " " + getCognome();
    }
}

