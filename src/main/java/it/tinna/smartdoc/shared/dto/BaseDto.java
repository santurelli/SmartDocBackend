package it.tinna.smartdoc.shared.dto;

import java.io.Serializable;
import java.util.List;

// import com.google.gson.annotations.Expose; // Requires Gson

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(of = "id")
public class BaseDto implements Serializable {

    private boolean checked;

    private String dtCreated;

    private String dtLastUpdate;

    private String errorNum;

    // @Expose
    private String errorText;

    // @Expose
    private long id;

    private String nomeUserCreated;

    private String nomeUserLastUpdate;

    // @Expose
    private String text;

    private Long total;

    // @Expose
    private List<String> tokens; // per la risposta per typeahead

    private Long userCreated;

    private Long userLastUpdate;

    // @Expose
    private String value; // per la risposta per typeahead

}
