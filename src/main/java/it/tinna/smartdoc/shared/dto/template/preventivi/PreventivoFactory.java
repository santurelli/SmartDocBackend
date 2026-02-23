package it.tinna.smartdoc.shared.dto.template.preventivi;

import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;
import it.tinna.smartdoc.shared.dto.template.RiepilogoIvaDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreventivoFactory {
	
	public static List<PreventivoTemplate> create() {
		PreventivoTemplate template = new PreventivoTemplate();
		List<RiepilogoIvaDto> riep = new ArrayList<RiepilogoIvaDto>();
		RiepilogoIvaDto dto = new RiepilogoIvaDto();
		dto.setAliquotaIvaFormattata("022 - 22%");
		dto.setTotaleImponibileFormattato("iva 1a");
		dto.setImportoIvaFormattato("100,00");
		riep.add(dto);
		dto = new RiepilogoIvaDto();
		dto.setTotaleImponibileFormattato("iva 2");
		dto.setAliquotaIvaFormattata("023 - 23%");
		dto.setImportoIvaFormattato("100,00");
		riep.add(dto);
		template.setRiepilogoIva(riep);
		List<ProdottoDocumentoDto> prodotti = new ArrayList<ProdottoDocumentoDto>();
		ProdottoDocumentoDto pdDto = new ProdottoDocumentoDto();
		ProdottoDto pDto = new ProdottoDto();
		pDto.setCodice("100100BASTO3+");
		pdDto.setQuantitaFormattata("15,00");
		pDto.setDescrizione("100x100 BASIC TORTORA 3+");
		pdDto.setProdottoDto(pDto);
		prodotti.add(pdDto);
		pdDto = new ProdottoDocumentoDto();
		pDto = new ProdottoDto();
		pDto.setCodice("200100CORMO3+");
		pDto.setDescrizione("200x100 CORTEN MORO 3+");
		pdDto.setQuantitaFormattata("12,00");
		pdDto.setProdottoDto(pDto);
		prodotti.add(pdDto);
		//terzo prodotto
		pdDto = new ProdottoDocumentoDto();
		pDto = new ProdottoDto();
		pDto.setCodice("300100CORMO3+");
		pDto.setDescrizione("300x100 CORTEN MORO 3+");
		pdDto.setQuantitaFormattata("12,00");
		pdDto.setProdottoDto(pDto);
		prodotti.add(pdDto);
		//quarto prodotto
		pdDto = new ProdottoDocumentoDto();
		pDto = new ProdottoDto();
		pDto.setCodice("400100CORMO3+");
		pDto.setDescrizione("400x100 CORTEN MORO 3+");
		pdDto.setQuantitaFormattata("12,00");
		pdDto.setProdottoDto(pDto);
		prodotti.add(pdDto);
		//quinto prodotto
		pdDto = new ProdottoDocumentoDto();
		pDto = new ProdottoDto();
		pDto.setCodice("500100CORMO3+");
		pDto.setDescrizione("500x100 CORTEN MORO 3+");
		pdDto.setQuantitaFormattata("12,00");
		pdDto.setProdottoDto(pDto);
		prodotti.add(pdDto);
		//sesto prodotto
		pdDto = new ProdottoDocumentoDto();
		pDto = new ProdottoDto();
		pDto.setCodice("600100CORMO3+");
		pDto.setDescrizione("600x100 CORTEN MORO 3+");
		pdDto.setQuantitaFormattata("12,00");
		pdDto.setProdottoDto(pDto);
		prodotti.add(pdDto);
		//settimo prodotto
		pdDto = new ProdottoDocumentoDto();
		pDto = new ProdottoDto();
		pDto.setCodice("700100CORMO3+");
		pDto.setDescrizione("700x100 CORTEN MORO 3+");
		pdDto.setQuantitaFormattata("12,00");
		pdDto.setProdottoDto(pDto);
		prodotti.add(pdDto);
		//ottavo prodotto
		pdDto = new ProdottoDocumentoDto();
		pDto = new ProdottoDto();
		pDto.setCodice("800100CORMO3+");
		pDto.setDescrizione("800x100 CORTEN MORO 3+");
		pdDto.setQuantitaFormattata("12,00");
		pdDto.setProdottoDto(pDto);
		prodotti.add(pdDto);
		//nono prodotto
		pdDto = new ProdottoDocumentoDto();
		pDto = new ProdottoDto();
		pDto.setCodice("900100CORMO3+");
		pDto.setDescrizione("900x100 CORTEN MORO 3+");
		pdDto.setQuantitaFormattata("12,00");
		pdDto.setProdottoDto(pDto);
		prodotti.add(pdDto);
		//decimo prodotto
		pdDto = new ProdottoDocumentoDto();
		pDto = new ProdottoDto();
		pDto.setCodice("1000100CORMO3+");
		pDto.setDescrizione("1000x100 CORTEN MORO 3+");
		pdDto.setQuantitaFormattata("12,00");
		pdDto.setProdottoDto(pDto);
		prodotti.add(pdDto);
//		//undicesimo prodotto
		pdDto = new ProdottoDocumentoDto();
		pDto = new ProdottoDto();
		pDto.setCodice("1100100CORMO3+");
		pDto.setDescrizione("1100x100 CORTEN MORO 3+");
		pdDto.setQuantitaFormattata("12,00");
		pdDto.setProdottoDto(pDto);
		prodotti.add(pdDto);
//		//dodicesimo prodotto
		pdDto = new ProdottoDocumentoDto();
		pDto = new ProdottoDto();
		pDto.setCodice("1200100CORMO3+");
		pDto.setDescrizione("1200x100 CORTEN MORO 3+");
		pdDto.setQuantitaFormattata("12,00");
		pdDto.setProdottoDto(pDto);
		prodotti.add(pdDto);
//		//tredicesimo prodotto
		pdDto = new ProdottoDocumentoDto();
		pDto = new ProdottoDto();
		pDto.setCodice("1300100CORMO3+");
		pDto.setDescrizione("1300x100 CORTEN MORO 3+");
		pdDto.setQuantitaFormattata("12,00");
		pdDto.setProdottoDto(pDto);
		prodotti.add(pdDto);
//		//quattordicesimo prodotto
		pdDto = new ProdottoDocumentoDto();
		pDto = new ProdottoDto();
		pDto.setCodice("1400100CORMO3+");
		pDto.setDescrizione("1400x100 CORTEN MORO 3+");
		pdDto.setQuantitaFormattata("12,00");
		pdDto.setProdottoDto(pDto);
		prodotti.add(pdDto);
//		//quindicesimo prodotto
//		pdDto = new ProdottoDocumentoDto();
//		pDto = new ProdottoDto();
//		pDto.setCodice("1500100CORMO3+");
//		pDto.setDescrizione("1500x100 CORTEN MORO 3+");
//		pdDto.setQuantitaFormattata("12,00");
//		pdDto.setProdottoDto(pDto);
//		prodotti.add(pdDto);
//		//sedicesimo prodotto
//		pdDto = new ProdottoDocumentoDto();
//		pDto = new ProdottoDto();
//		pDto.setCodice("1600100CORMO3+");
//		pDto.setDescrizione("1600x100 CORTEN MORO 3+");
//		pdDto.setQuantitaFormattata("12,00");
//		pdDto.setProdottoDto(pDto);
//		prodotti.add(pdDto);
//		//diciassettesimo prodotto
//		pdDto = new ProdottoDocumentoDto();
//		pDto = new ProdottoDto();
//		pDto.setCodice("1700100CORMO3+");
//		pDto.setDescrizione("1700x100 CORTEN MORO 3+");
//		pdDto.setQuantitaFormattata("12,00");
//		pdDto.setProdottoDto(pDto);
//		prodotti.add(pdDto);
//		//diciottesimo prodotto
//		pdDto = new ProdottoDocumentoDto();
//		pDto = new ProdottoDto();
//		pDto.setCodice("1800100CORMO3+");
//		pDto.setDescrizione("1800x100 CORTEN MORO 3+");
//		pdDto.setQuantitaFormattata("12,00");
//		pdDto.setProdottoDto(pDto);
//		prodotti.add(pdDto);
//		//diciannovesimo prodotto
//		pdDto = new ProdottoDocumentoDto();
//		pDto = new ProdottoDto();
//		pDto.setCodice("1900100CORMO3+");
//		pDto.setDescrizione("1900x100 CORTEN MORO 3+");
//		pdDto.setQuantitaFormattata("12,00");
//		pdDto.setProdottoDto(pDto);
//		prodotti.add(pdDto);
//		//ventesimo prodootto
//		pdDto = new ProdottoDocumentoDto();
//		pDto = new ProdottoDto();
//		pDto.setCodice("2000100CORMO3+-lunghissimoooooooooooooooooooooooo");
//		pDto.setDescrizione("2000x100 CORTEN MORO 3+");
//		pdDto.setQuantitaFormattata("12,00");
//		pdDto.setProdottoDto(pDto);
//		prodotti.add(pdDto);
//		//ventunesimo prodotto
//		pdDto = new ProdottoDocumentoDto();
//		pDto = new ProdottoDto();
//		pDto.setCodice("2100100CORMO3+");
//		pDto.setDescrizione("2100x100 CORTEN MORO 3+");
//		pdDto.setQuantitaFormattata("12,00");
//		pdDto.setProdottoDto(pDto);
//		prodotti.add(pdDto);
//		//ventiduesimo prodotto
//		pdDto = new ProdottoDocumentoDto();
//		pDto = new ProdottoDto();
//		pDto.setCodice("2200100CORMO3+");
//		pDto.setDescrizione("2200x100 CORTEN MORO 3+");
//		pdDto.setQuantitaFormattata("12,00");
//		pdDto.setProdottoDto(pDto);
//		prodotti.add(pdDto);
//		//ventitreesimo prodotto
//		pdDto = new ProdottoDocumentoDto();
//		pDto = new ProdottoDto();
//		pDto.setCodice("2300100CORMO3+-lunghissimoooooooooooooooooooooooo");
//		pDto.setDescrizione("2300x100 CORTEN MORO 3+");
//		pdDto.setQuantitaFormattata("12,00");
//		pdDto.setProdottoDto(pDto);
//		prodotti.add(pdDto);
		template.setProdotti(prodotti);
		
		return Arrays.asList(template);
	}

}

