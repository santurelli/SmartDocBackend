package it.tinna.smartdoc.shared.dto.template;

import java.math.BigDecimal;

public class UnitaMisuraTemplateDto {

	private String desc;
	private BigDecimal tot;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UnitaMisuraTemplateDto) {
			return ((UnitaMisuraTemplateDto) obj).getDesc().equalsIgnoreCase(getDesc());
		}
		return false;
	}

	public String getDesc() {
		return desc;
	}

	public BigDecimal getTot() {
		return tot;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setTot(BigDecimal tot) {
		this.tot = tot;
	}

}

