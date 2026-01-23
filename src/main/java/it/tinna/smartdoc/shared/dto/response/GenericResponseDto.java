package it.tinna.smartdoc.shared.dto.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericResponseDto<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private T payload;
    private String errorText;

}
