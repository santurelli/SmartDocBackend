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

}
