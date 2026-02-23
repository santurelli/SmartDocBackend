package it.tinna.smartdoc.batch.service.scarti;

import java.sql.SQLException;
import java.util.List;

import it.tinna.smartdoc.batch.dto.NotificaFatturaDto;

public interface ScartiExtractor
{

    List<NotificaFatturaDto> getNotifiche() throws SQLException;

}

