package it.tinna.smartdoc.shared.dto.response;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private String message;
    private Long id;
    private String errorCode;
}
