package it.tinna.smartdoc.batch.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = "id")
public class NotificaFatturaDto
{

    private String dataDocumento;

    private String dbKey;

    private String erroreSdi;

    private String erroreValidazioneXml;

    private long   id;

    private long   idFattura;

    private String numeroDocumento;

    private String soggetto;

    private String tipoEsito;

}

