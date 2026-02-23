package it.tinna.smartdoc.batch.dto.justdesign;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class BaseDto
{

    private String dtCreated;

    private String dtLastUpdate;

    private String errorNum;

    @Expose
    private String errorText;

    private String nomeUserCreated;

    private String nomeUserLastUpdate;

    private String userCreated;

    private String userLastUpdate;

}

