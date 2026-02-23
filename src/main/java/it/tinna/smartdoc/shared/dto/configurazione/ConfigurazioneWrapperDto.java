package it.tinna.smartdoc.shared.dto.configurazione;

import java.util.List;

import it.tinna.smartdoc.shared.dto.BaseDto;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;

@SuppressWarnings("serial")
public class ConfigurazioneWrapperDto extends BaseDto {
	
	private List<ConfigurazioneDto> configurazioneDto;
	private DatiAziendaDto datiAziendaDto;

	public List<ConfigurazioneDto> getConfigurazioneDto() {
		return configurazioneDto;
	}

	public DatiAziendaDto getDatiAziendaDto() {
		return datiAziendaDto;
	}

	public void setConfigurazioneDto(List<ConfigurazioneDto> configurazioneDto) {
		this.configurazioneDto = configurazioneDto;
	}

	public void setDatiAziendaDto(DatiAziendaDto datiAziendaDto) {
		this.datiAziendaDto = datiAziendaDto;
	}

}

