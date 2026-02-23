package it.tinna.smartdoc.shared.dto.response;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class Select2ResponseDto<T extends Object> extends BaseDto {
	
	@Expose
	@SerializedName("incomplete_results")
	private boolean incomplete;
	@Expose
	@SerializedName("items")
	private List<T> list;
	@Expose
	@SerializedName("total_count")
	private Integer totalCount;
	
	public List<T> getList() {
		return list;
	}
	
	public Integer getTotalCount() {
		return totalCount;
	}
	
	public boolean isIncomplete() {
		return incomplete;
	}
	
	public void setIncomplete(boolean incomplete) {
		this.incomplete = incomplete;
	}
	
	public void setList(List<T> list) {
		this.list = list;
	}
	
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
}

