package it.tinna.smartdoc.shared.dto.prodotti;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class MovimentoMagazzinoDto extends BaseDto
{

    public enum TipoMovimento
    {
     INGRESSO("I"),
     PRODUCI("PRODUCI"),
     USCITA("U");

        private String tipo;

        TipoMovimento(String tipo)
        {
            this.tipo = tipo;
        }

        public String getValore()
        {
            return this.tipo;
        }
    }

    @Expose
    private String  causale;

    @Expose
    private String  dataMovimento;

    private String  descrAliquotaIva;

    private String  descrCausale;

    private String  descrColore;

    private String  descrMagazzino;

    private String  descrScelta;

    private String  descrTaglia;

    private String  descrTono;

    private Integer idAliquotaIva;

    private Integer idCausale;

    private Long    idCliente;

    private Integer idColore;

    private Long    idFornitore;

    private Integer idMagazzino;

    private Long    idProdotto;

    private Long    idDocumento;

    private Integer idScelta;

    private Integer idTaglia;

    private Integer idTono;

    @Expose
    private Integer flDeleted;

    private Integer idUnitaMisura;

    private Double  prezzoUnitario;

    private Double  quantita;

    private String  tipoMovimento;

    // View Fields
    private String descrizioneProdotto;
    private String descrizioneProdottoExcel;
    private String clienteFornitore;
    private Double quantitaBase;
    private Double quantitaCarico;
    private Double quantitaScarico;
    private String numeroDocumento;
    @Expose
    private String codiceProdotto;
    @Expose
    private Integer sezione;
    @Expose
    private Integer serie;
    @Expose
    private Integer diametro;
    @Expose
    private String scultura;
    @Expose
    private String indiceVelocita;
    @Expose
    private String dot;
    @Expose
    private String descMarca;

    public void setId(Long id) {
        super.setId(id != null ? id : 0L);
    }

    public void setIdProdotto(Long idProdotto) {
        this.idProdotto = idProdotto;
    }

    public void setIdProdotto(long idProdotto) {
        this.idProdotto = idProdotto;
    }

    public void setIdProdotto(Integer idProdotto) {
        this.idProdotto = idProdotto != null ? idProdotto.longValue() : null;
    }
}

