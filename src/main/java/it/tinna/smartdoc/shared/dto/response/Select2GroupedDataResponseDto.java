package it.tinna.smartdoc.shared.dto.response;

import java.util.List;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class Select2GroupedDataResponseDto<T extends Object> extends BaseDto {

	@Expose
	private List<Select2OptGroupDto<T>> results;

	public List<Select2OptGroupDto<T>> getResults() {
		return results;
	}

	public void setResults(List<Select2OptGroupDto<T>> results) {
		this.results = results;
	}

}

