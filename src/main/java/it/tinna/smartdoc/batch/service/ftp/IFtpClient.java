package it.tinna.smartdoc.batch.service.ftp;

import java.io.File;
import java.io.OutputStream;
import java.util.Date;

import it.tinna.smartdoc.batch.exception.FtpException;

public interface IFtpClient
{

    void deleteFile(String dir,
                    String fileName) throws FtpException;

    void downloadFile(String dir,
                      String fileName,
                      OutputStream os) throws FtpException;

    String[] listFiles(String dir) throws FtpException;

    void upload(File file,
                String location) throws FtpException;

    public Date getFileInfo(String dir,
                            String fileName) throws FtpException;
}

