package it.tinna.smartdoc.shared.dto.mail;

import java.io.File;

public class MailAttachmentDto
{

    private File   file;

    private String name;

    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

}

