package it.tinna.smartdoc.shared.dto.response;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatatablesResponseDto<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<T> list;
    private Long totalCount;
    private Long totalFiltered;
    private String error;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getTotalFiltered() {
        return totalFiltered;
    }

    public void setTotalFiltered(Long totalFiltered) {
        this.totalFiltered = totalFiltered;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

