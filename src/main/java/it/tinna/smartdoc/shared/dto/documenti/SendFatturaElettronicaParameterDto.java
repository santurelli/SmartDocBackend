package it.tinna.smartdoc.shared.dto.documenti;

import java.util.List;

import it.tinna.smartdoc.shared.dto.BaseDto;
import it.tinna.smartdoc.shared.dto.configurazione.ConfigurazioneDto;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;

@SuppressWarnings("serial")
public class SendFatturaElettronicaParameterDto extends BaseDto {
	
	private List<ConfigurazioneDto> configurazioni;
	private DatiAziendaDto datiAziendaDto;
	private String destinatario;

	public List<ConfigurazioneDto> getConfigurazioni() {
		return configurazioni;
	}

	public DatiAziendaDto getDatiAziendaDto() {
		return datiAziendaDto;
	}

	public String getDestinatario() {
		return destinatario;
	}

	public void setConfigurazioni(List<ConfigurazioneDto> configurazioni) {
		this.configurazioni = configurazioni;
	}

	public void setDatiAziendaDto(DatiAziendaDto datiAziendaDto) {
		this.datiAziendaDto = datiAziendaDto;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

}

