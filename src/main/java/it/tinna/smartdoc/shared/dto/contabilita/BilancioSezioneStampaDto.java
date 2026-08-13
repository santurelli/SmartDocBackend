package it.tinna.smartdoc.shared.dto.contabilita;

/**
 * Bean "guida" usato solo per far ripetere il subreport di sezione (ATTIVO/PASSIVO/COSTI/RICAVI)
 * nella stampa PDF del bilancio - vedi BilancioDelegate.esportaPdf() e report_stampa/bilancio.jrxml.
 * Non e' esposto via REST, serve solo come JRBeanCollectionDataSource per Jasper.
 */
public class BilancioSezioneStampaDto {

    private String sezione;

    public BilancioSezioneStampaDto(String sezione) {
        this.sezione = sezione;
    }

    public String getSezione() {
        return sezione;
    }

    public void setSezione(String sezione) {
        this.sezione = sezione;
    }
}
