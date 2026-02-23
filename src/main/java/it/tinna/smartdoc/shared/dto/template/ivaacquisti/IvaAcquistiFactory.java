package it.tinna.smartdoc.shared.dto.template.ivaacquisti;

import it.tinna.smartdoc.shared.dto.documenti.IvaDocumentoDto;
import it.tinna.smartdoc.shared.dto.template.RiepilogoIvaDto;

import java.util.ArrayList;
import java.util.List;

public class IvaAcquistiFactory {
	
	public static List<IvaDocumentoDto> create() {
		List<IvaDocumentoDto> result = new ArrayList<IvaDocumentoDto>();
		IvaDocumentoDto dto = new IvaDocumentoDto();
		dto.setnProgr(1);
		dto.setDescrTipoDocumento("Fattura d'acquisto 56 del 18/01/2011");
		List<RiepilogoIvaDto> list = new ArrayList<RiepilogoIvaDto>();
		RiepilogoIvaDto riep = new RiepilogoIvaDto();
		riep.setAliquotaIvaFormattata("22");
		riep.setTotaleImponibileFormattato("100,00");
		riep.setImportoIvaFormattato("15,12");
		list.add(riep);
		riep = new RiepilogoIvaDto();
		riep.setAliquotaIvaFormattata("23");
		riep.setTotaleImponibileFormattato("100,00");
		riep.setImportoIvaFormattato("15,12");
		list.add(riep);
		riep = new RiepilogoIvaDto();
		riep.setAliquotaIvaFormattata("24");
		riep.setTotaleImponibileFormattato("100,00");
		riep.setImportoIvaFormattato("15,12");
		list.add(riep);
//		riep = new RiepilogoIvaDto();
//		riep.setAliquotaIvaFormattata("25");
//		riep.setTotaleImponibileFormattato("100,00");
//		riep.setImportoIvaFormattato("15,12");
//		list.add(riep);
//		riep = new RiepilogoIvaDto();
//		riep.setAliquotaIvaFormattata("26");
//		riep.setTotaleImponibileFormattato("100,00");
//		riep.setImportoIvaFormattato("15,12");
//		list.add(riep);
		dto.setRiepilogoIva(list);
		result.add(dto);
		dto = new IvaDocumentoDto();
		dto.setnProgr(2);
		dto.setDescrTipoDocumento("Fattura d'acquisto 15768944/D del 04/01/2011");
		result.add(dto);
		
		return result;
	}

}

