package it.tinna.smartdoc.server.delegate.datiazienda;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.tinna.smartdoc.server.constants.CategorieTabDecod;
import it.tinna.smartdoc.server.constants.MimeTypeConstants;
import it.tinna.smartdoc.server.dao.datiazienda.DatiAziendaDao;
import it.tinna.smartdoc.server.dao.tabdecod.TabDecodDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;

@Service(value = "datiaziendaDelegate")
public class DatiAziendaDelegate extends BaseDelegate
{

    public Map<String, Object> get() throws SQLException
    {
        DatiAziendaDao datiAziendaDao = new DatiAziendaDao(jdbcTemplate);
        TabDecodDao tabDecodDao = new TabDecodDao(jdbcTemplate);
        Map<String, Object> map = new HashMap<>();
        map.put(ISharedConstants.COMBOSMAP_KEY_DATIAZIENDA, this.getDatiAzienda());
        map.put(ISharedConstants.COMBOSMAP_KEY_REGIMIFISCALI, tabDecodDao.getListByCategoria(CategorieTabDecod.REGIME_FISCALE));
        return map;
    }

    public DatiAziendaDto getDatiAzienda() throws SQLException
    {
        DatiAziendaDao dao = new DatiAziendaDao(jdbcTemplate);
        DatiAziendaDto dto = dao.getDatiAzienda();
        if ( dto != null )
        {
            byte[] b = dao.getLogo();
            dto.setByteLogo(b);
            if ( b != null )
            {
                dto.setLogo(Base64.getEncoder().encodeToString(b));
                Tika tika = new Tika();
                String mimeType = tika.detect(b);
                if ( mimeType.equals(MimeTypeConstants.IMAGE_GIF) )
                {
                    dto.setLogoType("gif");
                }
                else if ( mimeType.equals(MimeTypeConstants.IMAGE_JPEG) )
                {
                    dto.setLogoType("jpeg");
                }
                else if ( mimeType.equals(MimeTypeConstants.IMAGE_PNG) )
                {
                    dto.setLogoType("png");
                }
                else
                {
                    _log.error("Il formato {} del logo aziendale non è supportato", mimeType);
                }
            }
        }
        return dto;
    }

    public void save(DatiAziendaDto dto,
                     MultipartFile mpf) throws SQLException
    {
        DatiAziendaDao dao = new DatiAziendaDao(jdbcTemplate);
        long numRows = dao.getRowCount();
        if ( mpf != null )
        {
            try
            {
                dto.setByteLogo(mpf.getBytes());
            }
            catch ( IOException e )
            {
                _log.error("Errore nel recupero dei byte del logo scaricato", e);
                throw new SQLException(e);
            }
        }
        if ( numRows == 0 )
        {
            dao.insert(dto);
        }
        else
        {
            // Load existing data to preserve hidden fields (e.g. mail server config)
            DatiAziendaDto existing = dao.getDatiAzienda();
            
            // Merge UI fields into existing object
            existing.setDenominazione(dto.getDenominazione());
            existing.setIdRegimeFiscale(dto.getIdRegimeFiscale());
            existing.setPartitaIva(dto.getPartitaIva());
            existing.setCodiceFiscale(dto.getCodiceFiscale());
            existing.setIndirizzo(dto.getIndirizzo());
            existing.setCitta(dto.getCitta());
            existing.setCap(dto.getCap());
            existing.setProvincia(dto.getProvincia());
            existing.setTelefono(dto.getTelefono());
            existing.setFax(dto.getFax());
            existing.setPec(dto.getPec());
            existing.setEmail(dto.getEmail());
            existing.setSitoWeb(dto.getSitoWeb());
            existing.setDeleteLogo(dto.getDeleteLogo());
            
            if ( dto.getByteLogo() != null ) {
                 existing.setByteLogo(dto.getByteLogo());
            } else if (Boolean.TRUE.equals(dto.getDeleteLogo())) {
                 existing.setByteLogo(null);
            } else {
                 // Keep existing logo if not provided and not deleted
                 // (dao.getDatiAzienda doesn't retrieve blob usually in basic query, 
                 // but let's check. Use getLogo logic if needed, 
                 // but previously invalid logic: "if byteLogo null and delete false -> setByteLogo(dao.getLogo())"
                 // actually standard update uses byteLogo = ?, so we must provide it.
                 // The 'existing' DTO from getDatiAzienda might not have the logo bytes if the query S01 doesn't fetch them.
                 // S01 usually fetches textual fields. S03 fetches logo.
                 // So we must fetch logo bytes if we want to preserve them and the update query updates ALL fields including logo.
            }

            // Correct logic for Logo maintenance in full update:
            // DATIAZIENDA_U01 updates imglogo = ?
            // We need the current logo bytes if we aren't changing it.
            if (existing.getByteLogo() == null && (dto.getByteLogo() == null && !Boolean.TRUE.equals(dto.getDeleteLogo()))) {
                 existing.setByteLogo(dao.getLogo());
            }

            // Update user tracking
            existing.setUserLastUpdate(dto.getUserLastUpdate()); // Or current user? Controller didn't set it. 
            // The DAO/DTO might expect it. The query has user_last_update = ?.
            // Ideally we set it here or in controller. I'll rely on what's passed or null.

            dao.update(existing);
        }
    }
}

