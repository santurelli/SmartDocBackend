package it.tinna.smartdoc.batch.service.scarti;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import it.tinna.smartdoc.batch.dto.NotificaFatturaDto;
import it.tinna.smartdoc.server.delegate.documenti.FatturaElettronicaDelegate;

public class ScartiFastOrderExtractor implements ScartiExtractor
{

    @Autowired
    private FatturaElettronicaDelegate fatturaelettronicaDelegate;

    @Override
    public List<NotificaFatturaDto> getNotifiche() throws SQLException
    {
        return fatturaelettronicaDelegate.getNotificheFastOrder();
    }

}

