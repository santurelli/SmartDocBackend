package it.tinna.smartdoc.shared.dto.datiazienda;

import it.tinna.smartdoc.shared.dto.BaseDto;

import java.io.File;
import java.io.InputStream;

@SuppressWarnings("serial")
public class DatiAziendaServerDto extends BaseDto {
	  
private DatiAziendaDto datiEnte;
	
	private File logo;
	private InputStream logoFile;

	public DatiAziendaDto getDatiEnte() {
		return datiEnte;
	}

	public File getLogo() {
		return logo;
	}

	public InputStream getLogoFile() {
		return logoFile;
	}

	public void setDatiEnte(DatiAziendaDto datiEnte) {
		this.datiEnte = datiEnte;
	}

	public void setLogo(File logo) {
		this.logo = logo;
	}

	public void setLogoFile(InputStream logoFile) {
		this.logoFile = logoFile;
	}

}

