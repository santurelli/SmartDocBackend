package it.tinna.smartdoc.batch.readers.invio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;

import com.google.gson.Gson;

import it.tinna.smartdoc.batch.dto.justdesign.documenti.fatturescontrino.FatturaScontrinoDto;
import it.tinna.smartdoc.batch.dto.justdesign.magazzino.ArticoloInDocumentoDto;
import it.tinna.smartdoc.server.constants.MimeTypeConstants;
import it.tinna.smartdoc.server.constants.ModalitaPagamentoEnum;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.delegate.documenti.FatturaElettronicaDelegate;
import it.tinna.smartdoc.shared.dto.clienti.ClienteDto;
import it.tinna.smartdoc.shared.dto.clienti.TipologiaClienteFornitore;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.TipoFattura;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;
import lombok.Setter;

public class InvioFattureElettronicheJustDesignReader implements ItemStreamReader<FatturaElettronicaWrapperDto>
{

    private Logger                             logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FatturaElettronicaDelegate         fatturaelettronicaDelegate;

    @Setter
    private String                             pathFattura;                                      // fattura inviata in formato base64

    @Setter
    private String                             nomeStore;

    @Setter
    private String                             dbKey;

    private List<FatturaElettronicaWrapperDto> elencoFatture;

    @Override
    public void close() throws ItemStreamException
    {
        // TODO Auto-generated method stub

    }

    private FatturaElettronicaDto creaFatturaElettronica(FatturaScontrinoDto dto) throws Exception
    {
        FatturaDto feDto = new FatturaDto();
        feDto.setId(Long.parseLong(dto.getId()));
        feDto.setTipoFattura(TipoFattura.FATTURA);
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setTipologia(TipologiaClienteFornitore.PRIVATO);
        clienteDto.setPartitaIva(dto.getPartitaIva());
        clienteDto.setCodiceFiscale(dto.getCodiceFiscale());
        clienteDto.setDenominazione(dto.getCliente());
        feDto.setClienteDto(clienteDto);
        feDto.setIndirizzoIntestazione(dto.getIndirizzo());
        feDto.setCittaIntestazione(dto.getCitta());
        feDto.setCapIntestazione(dto.getCap());
        feDto.setProvinciaIntestazione(dto.getProvincia());

        feDto.setCodiceUfficioDestinazione(dto.getCodiceDestinatario());
        feDto.setPec(dto.getPec());

        feDto.setDataDocumento(dto.getDataDocumento());
        feDto.setNumDocumento(Integer.parseInt(dto.getNumDoc()));
        feDto.setParticella("/" + StringUtils.left(nomeStore.replaceAll(" ", "").toUpperCase(), 20));

        feDto.setTotale(Double.parseDouble(dto.getImponibile()));
        List<ProdottoDocumentoDto> articoli = new ArrayList<ProdottoDocumentoDto>();
        feDto.setProdotti(articoli);
        for ( ArticoloInDocumentoDto adDto : dto.getArticoli() )
        {
            ProdottoDocumentoDto pdDto = new ProdottoDocumentoDto();
            pdDto.setCodiceProdotto(adDto.getCodPerFornitore());
            pdDto.setDescProdotto(adDto.getDescrIta());
            pdDto.setQuantita(Double.parseDouble(adDto.getQuantitaInDocumento()));
            pdDto.setPrezzo(Double.parseDouble(adDto.getPrezzo()));
            BigDecimal totaleCompresoSconto = new BigDecimal(adDto.getPrezzo());
            if ( StringUtils.isNotBlank(adDto.getSconto()) && (Double.parseDouble(adDto.getSconto()) != 0d) )
            {
                pdDto.setSconto(adDto.getSconto() + "%");
                BigDecimal sconto = new BigDecimal(adDto.getSconto()).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
                totaleCompresoSconto = totaleCompresoSconto.subtract(totaleCompresoSconto.multiply(sconto, new MathContext(4, RoundingMode.HALF_UP)));
            }
            totaleCompresoSconto = totaleCompresoSconto.multiply(new BigDecimal(adDto.getQuantitaInDocumento(), new MathContext(2, RoundingMode.HALF_UP)));
            pdDto.setTotaleSenzaIva(totaleCompresoSconto.doubleValue());

            if ( adDto.getIva().equals(4d) )
            {
                pdDto.setIdAliquotaIva(11);
            }
            else if ( adDto.getIva().equals(22d) )
            {
                pdDto.setIdAliquotaIva(6);
            }

            articoli.add(pdDto);
        }
        List<ScadenzaPagamentoDocumentoDto> scadenze = new ArrayList<>();
        feDto.setListaScadenzePagamentiDocumento(scadenze);
        ScadenzaPagamentoDocumentoDto scadenzaDto = new ScadenzaPagamentoDocumentoDto();
        scadenze.add(scadenzaDto);
        scadenzaDto.setModalitaPagamento(ModalitaPagamentoEnum.CONTANTI.name());
        scadenzaDto.setDtScadenza(dto.getDataDocumento());
        scadenzaDto.setImporto(new BigDecimal(dto.getImponibile()).subtract(new BigDecimal(dto.getSconto())).doubleValue());

        if ( StringUtils.isNotBlank(dto.getSconto()) && Double.parseDouble(dto.getSconto()) != 0d )
        {
            feDto.setSconto(dto.getSconto());
        }
        feDto.setEsigibilitaDifferita(0);
        try
        {
            fatturaelettronicaDelegate.getFatturaElettronica(feDto, nomeStore);
        }
        catch ( Exception e )
        {
            logger.error("Errore nella generazione della fattura elettronica per lo store {}", nomeStore, e);
            throw e;
        }
        return feDto;
    }

    @Override
    public void open(ExecutionContext arg0) throws ItemStreamException
    {
        elencoFatture = new ArrayList<FatturaElettronicaWrapperDto>();
        String base64Fattura = null;
        File fileFattura = new File(pathFattura);
        try
        {
            base64Fattura = FileUtils.readFileToString(fileFattura, StandardCharsets.UTF_8.name());
        }
        catch ( IOException e1 )
        {
            logger.error("Errore nella lettura del file temporaneo della fattura inviata dallo store {}", nomeStore, e1);
            throw new ItemStreamException(ExceptionUtils.getMessage(e1));
        }
        finally
        {
            FileUtils.deleteQuietly(fileFattura);
        }
        byte[] b = Base64Utils.decodeFromString(base64Fattura);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(b);
        Tika tika = new Tika();
        String mimeType = null;
        try
        {
            mimeType = tika.detect(inputStream);
            String str = null;
            if ( mimeType.equals(MimeTypeConstants.APPLICATION_GZIP) )
            {
                logger.info("Inviata fattura compressa in formato {}", MimeTypeConstants.APPLICATION_GZIP);
                GZIPInputStream gis = new GZIPInputStream(inputStream);
                StringWriter writer = new StringWriter();
                IOUtils.copy(gis, writer, StandardCharsets.UTF_8);
                str = writer.toString();
            }
            else if ( mimeType.equals(MimeTypeConstants.TEXT_PLAIN) )
            {
                logger.info("Inviata fattura non compressa");
                str = new String(b, StandardCharsets.UTF_8.name());
            }
            else
            {
                logger.error("Formato della fattura inviata non supportato. Formato risposta: {}", mimeType);
                throw new IOException("Formato della fattura inviata non supportato. Formato risposta: " + mimeType);
            }
            Gson gson = new Gson();
            FatturaScontrinoDto dto = gson.fromJson(str, FatturaScontrinoDto.class);
            boolean isInviabile = false;
            try
            {
                isInviabile = fatturaelettronicaDelegate.isFatturaInviabile(nomeStore, Long.parseLong(dto.getId()));
            }
            catch ( NumberFormatException e1 )
            {
                logger.error("Errore nella verifica della inviabilità della fattura {} per lo store {}", dto.getId(), nomeStore, e1);
                throw new ItemStreamException(ExceptionUtils.getRootCause(e1).getMessage());
            }
            catch ( SQLException e2 )
            {
                throw new ItemStreamException(ExceptionUtils.getRootCause(e2).getMessage());
            }
            if ( isInviabile )
            {
                DatabaseContextHolder.set(dbKey);
                // creo la fattura elettronica
                FatturaElettronicaDto feDto;
                try
                {
                    feDto = creaFatturaElettronica(dto);
                }
                catch ( Exception e )
                {
                    throw new ItemStreamException(ExceptionUtils.getMessage(e));
                }
                FatturaElettronicaWrapperDto result = new FatturaElettronicaWrapperDto();
                result.setFattura(feDto);
                if ( StringUtils.isEmpty(feDto.getErroreValidazioneXml()) )
                {
                    result.setFlussoFatturaElettronica(feDto.getXmlFattura().getBytes());
                }
                elencoFatture.add(result);
            }
            else
            {
                logger.info("La fattura {} per lo store {} non è inviabile", dto.getId(), nomeStore);
            }
        }
        catch ( IOException e )
        {
            logger.error("Errore nella determinazione del formato della fattura inviata", e);
            throw new ItemStreamException(ExceptionUtils.getMessage(e));
        }
        finally
        {
            DatabaseContextHolder.clear();
        }
    }

    @Override
    public void update(ExecutionContext arg0) throws ItemStreamException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public FatturaElettronicaWrapperDto read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException
    {
        if ( elencoFatture.size() > 0 )
        {
            return elencoFatture.remove(0);
        }
        else
        {
            return null;
        }
    }

}

