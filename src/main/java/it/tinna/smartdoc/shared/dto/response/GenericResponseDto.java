package it.tinna.smartdoc.shared.dto.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponseDto<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private T payload;
    private String errorText;

}

