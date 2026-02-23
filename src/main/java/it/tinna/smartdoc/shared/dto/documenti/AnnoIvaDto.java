package it.tinna.smartdoc.shared.dto.documenti;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class AnnoIvaDto extends BaseDto
{

    @Expose
    private int anno;

    public int getAnno()
    {
        return anno;
    }

    public void setAnno(int anno)
    {
        this.anno = anno;
    }

}

