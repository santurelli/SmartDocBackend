package it.tinna.smartdoc.shared.dto.response;

import java.util.List;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class Select2OptGroupDto<T extends Object> extends BaseDto {

	@Expose
	private List<T> children;

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Select2OptGroupDto) {
			return ((Select2OptGroupDto) obj).getText().equals(this.getText());
		}
		return false;
	}

	public List<T> getChildren() {
		return children;
	}

	public void setChildren(List<T> children) {
		this.children = children;
	}

}

