package it.tinna.smartdoc.shared.dto.contabilita;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

/**
 * Anteprima di una chiusura esercizio, da mostrare all'utente prima di confermare.
 */
@SuppressWarnings("serial")
public class ChiusuraEsercizioPreviewDto extends BaseDto {

    @Expose
    private int                              anno;

    @Expose
    private boolean                          giaChiuso;

    @Expose
    private BigDecimal                       utilePerdita;

    @Expose
    private List<ChiusuraEsercizioRigaDto>   righeEconomiche;

    @Expose
    private List<ChiusuraEsercizioRigaDto>   righePatrimoniali;

    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public boolean isGiaChiuso() {
        return giaChiuso;
    }

    public void setGiaChiuso(boolean giaChiuso) {
        this.giaChiuso = giaChiuso;
    }

    public BigDecimal getUtilePerdita() {
        return utilePerdita;
    }

    public void setUtilePerdita(BigDecimal utilePerdita) {
        this.utilePerdita = utilePerdita;
    }

    public List<ChiusuraEsercizioRigaDto> getRigheEconomiche() {
        return righeEconomiche;
    }

    public void setRigheEconomiche(List<ChiusuraEsercizioRigaDto> righeEconomiche) {
        this.righeEconomiche = righeEconomiche;
    }

    public List<ChiusuraEsercizioRigaDto> getRighePatrimoniali() {
        return righePatrimoniali;
    }

    public void setRighePatrimoniali(List<ChiusuraEsercizioRigaDto> righePatrimoniali) {
        this.righePatrimoniali = righePatrimoniali;
    }
}
