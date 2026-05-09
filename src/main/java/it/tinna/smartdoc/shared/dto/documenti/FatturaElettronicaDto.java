package it.tinna.smartdoc.shared.dto.documenti;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@Setter
@Getter
@NoArgsConstructor
@ToString(callSuper = false, onlyExplicitlyIncluded = true, includeFieldNames = true)
public class FatturaElettronicaDto extends DocumentoDto
{
    // All fields previously here are already defined in DocumentoDto.
    // FatturaElettronicaDto is kept as a marker class or for future specific fields.
}

