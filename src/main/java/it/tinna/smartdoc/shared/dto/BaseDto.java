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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

