package it.tinna.smartdoc.batch.dto.fastorder;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BaseDto implements Serializable
{

    private Integer id;

    private boolean checked;

    private String  dtCreated;

    private String  dtLastUpdate;

    private String  errorNum;

    private String  errorText;

    private String  nomeUserCreated;

    private String  nomeUserLastUpdate;

    private Integer userCreated;

    private Integer userLastUpdate;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    public String getDtCreated()
    {
        return dtCreated;
    }

    public String getDtLastUpdate()
    {
        return dtLastUpdate;
    }

    public String getErrorNum()
    {
        return errorNum;
    }

    public String getErrorText()
    {
        return errorText;
    }

    public String getNomeUserCreated()
    {
        return nomeUserCreated;
    }

    public String getNomeUserLastUpdate()
    {
        return nomeUserLastUpdate;
    }

    public Integer getUserCreated()
    {
        return userCreated;
    }

    public Integer getUserLastUpdate()
    {
        return userLastUpdate;
    }

    public void setDtCreated(String dtCreated)
    {
        this.dtCreated = dtCreated;
    }

    public void setDtLastUpdate(String dtLastUpdate)
    {
        this.dtLastUpdate = dtLastUpdate;
    }

    public void setErrorNum(String errorNum)
    {
        this.errorNum = errorNum;
    }

    public void setErrorText(String errorText)
    {
        this.errorText = errorText;
    }

    public void setNomeUserCreated(String nomeUserCreated)
    {
        this.nomeUserCreated = nomeUserCreated;
    }

    public void setNomeUserLastUpdate(String nomeUserLastUpdate)
    {
        this.nomeUserLastUpdate = nomeUserLastUpdate;
    }

    public void setUserCreated(Integer userCreated)
    {
        this.userCreated = userCreated;
    }

    public void setUserLastUpdate(Integer userLastUpdate)
    {
        this.userLastUpdate = userLastUpdate;
    }

}

