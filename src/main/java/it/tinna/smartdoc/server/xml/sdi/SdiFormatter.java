package it.tinna.smartdoc.server.xml.sdi;

import it.tinna.smartdoc.server.constants.CausalePagamentoEnum;
import it.tinna.smartdoc.server.constants.EsigibilitaIvaEnum;
import it.tinna.smartdoc.server.constants.EsitoTrasferimentoFtpEnum;
import it.tinna.smartdoc.server.constants.FormatoTrasmissioneEnum;
import it.tinna.smartdoc.server.constants.ModalitaPagamentoEnum;
import it.tinna.smartdoc.server.constants.NaturaEsenzioneEnum;
import it.tinna.smartdoc.server.constants.RegimeFiscaleEnum;
import it.tinna.smartdoc.server.constants.SoggettoEmittenteEnum;
import it.tinna.smartdoc.server.constants.TipoCassaEnum;
import it.tinna.smartdoc.server.constants.TipoCessazionePrestazioneEnum;
import it.tinna.smartdoc.server.constants.TipoDocumentoEnum;
import it.tinna.smartdoc.server.constants.TipoPagamentoEnum;
import it.tinna.smartdoc.server.constants.TipoResaEnum;
import it.tinna.smartdoc.server.constants.TipoRitenutaEnum;
import it.tinna.smartdoc.server.constants.TipoScontoDocumentoEnum;
import it.tinna.smartdoc.server.util.XmlFieldFormatterBase;

public class SdiFormatter extends XmlFieldFormatterBase
{
    /*
     * Formato trasmissione
     */
    public static String formatFormatoTrasmissione(FormatoTrasmissioneEnum value)
    {
        if ( value == null )
            return null;

        switch ( value )
        {
            case FATTURA_PA:
                return "FPA12";

            case FATTURA_PRIVATI:
                return "FPR12";
        }

        return null;

    }

    public static FormatoTrasmissioneEnum parseFormatoTrasmissione(String value)
    {
        if ( value == null )
            return null;

        value = value.toUpperCase().trim();

        if ( "FPA12".equals(value) )
            return FormatoTrasmissioneEnum.FATTURA_PA;

        if ( "FPR12".equals(value) )
            return FormatoTrasmissioneEnum.FATTURA_PRIVATI;

        return null;
    }

    /*
     * Causale pagamento
     */
    public static String formatCausalePagamento(CausalePagamentoEnum value)
    {
        if ( value == null )
            return null;

        switch ( value )
        {
            case A:
                return "A";
            case B:
                return "B";
            case C:
                return "C";
            case D:
                return "D";
            case E:
                return "E";
            case G:
                return "G";
            case H:
                return "H";
            case I:
                return "I";
            case L:
                return "L";
            case M:
                return "M";
            case N:
                return "N";
            case O:
                return "O";
            case P:
                return "P";
            case Q:
                return "Q";
            case R:
                return "R";
            case S:
                return "S";
            case T:
                return "T";
            case U:
                return "U";
            case V:
                return "V";
            case W:
                return "W";
            case X:
                return "X";
            case Y:
                return "Y";
            case Z:
                return "Z";
            case L1:
                return "L1";
            case M1:
                return "M1";
            case O1:
                return "O1";
            case V1:
                return "V1";

        }

        return null;

    }

    public static CausalePagamentoEnum parseCausalePagamento(String value)
    {
        if ( value == null )
            return null;

        value = value.toUpperCase().trim();

        if ( "A".equals(value) )
            return CausalePagamentoEnum.A;
        if ( "B".equals(value) )
            return CausalePagamentoEnum.B;
        if ( "C".equals(value) )
            return CausalePagamentoEnum.C;
        if ( "D".equals(value) )
            return CausalePagamentoEnum.D;
        if ( "E".equals(value) )
            return CausalePagamentoEnum.E;
        if ( "G".equals(value) )
            return CausalePagamentoEnum.G;
        if ( "H".equals(value) )
            return CausalePagamentoEnum.H;
        if ( "I".equals(value) )
            return CausalePagamentoEnum.I;
        if ( "L".equals(value) )
            return CausalePagamentoEnum.L;
        if ( "M".equals(value) )
            return CausalePagamentoEnum.M;
        if ( "N".equals(value) )
            return CausalePagamentoEnum.N;
        if ( "O".equals(value) )
            return CausalePagamentoEnum.O;
        if ( "P".equals(value) )
            return CausalePagamentoEnum.P;
        if ( "Q".equals(value) )
            return CausalePagamentoEnum.Q;
        if ( "R".equals(value) )
            return CausalePagamentoEnum.R;
        if ( "S".equals(value) )
            return CausalePagamentoEnum.S;
        if ( "T".equals(value) )
            return CausalePagamentoEnum.T;
        if ( "U".equals(value) )
            return CausalePagamentoEnum.U;
        if ( "V".equals(value) )
            return CausalePagamentoEnum.V;
        if ( "W".equals(value) )
            return CausalePagamentoEnum.W;
        if ( "X".equals(value) )
            return CausalePagamentoEnum.X;
        if ( "Y".equals(value) )
            return CausalePagamentoEnum.Y;
        if ( "Z".equals(value) )
            return CausalePagamentoEnum.Z;
        if ( "L1".equals(value) )
            return CausalePagamentoEnum.L1;
        if ( "M1".equals(value) )
            return CausalePagamentoEnum.M1;
        if ( "O1".equals(value) )
            return CausalePagamentoEnum.O1;
        if ( "V1".equals(value) )
            return CausalePagamentoEnum.L1;

        return null;
    }

    /*
     * Tipo sconto / maggiorazione
     */
    @SuppressWarnings("incomplete-switch")
    public static String formatTipoSconto(TipoScontoDocumentoEnum tipoSconto)
    {
        if ( tipoSconto == null )
            return null;

        switch ( tipoSconto )
        {
            case SCONTO:
                return "SC";

            case MAGGIORAZIONE:
                return "MG";

        }

        return null;
    }

    public static TipoScontoDocumentoEnum parseTipoSconto(String value)
    {
        if ( value == null )
            return null;

        value = value.toUpperCase().trim();

        if ( "SC".equals(value) )
            return TipoScontoDocumentoEnum.SCONTO;

        if ( "MG".equals(value) )
            return TipoScontoDocumentoEnum.MAGGIORAZIONE;

        return null;
    }

    /*
     * Boolenao solo SI
     */
    public static String formatBooleanSi(boolean value)
    {
        return "SI";
        // TODO: da sistemare capendo perchè ritornando nullo scoppia in fase di encoding dell'Art73Type
        // return value ? "SI" : null;
    }

    public static Boolean parseBooleanSi(String value)
    {
        if ( value == null )
            return false;

        value = value.toUpperCase().trim();

        if ( "SI".equals(value) )
            return true;
        else
            return false;
    }

    /*
     * Tipo cassa
     */
    public static String formatTipoCassa(TipoCassaEnum tipoCassa)
    {
        if ( tipoCassa == null )
            return null;

        switch ( tipoCassa )
        {
            case AVVOCATI_PROCURATORI:
                return "TC01";

            case DOTTORI_COMMERCIALISTI:
                return "TC02";

            case GEOMETRI:
                return "TC03";

            case INGENIERI_ARCHITETTI_LP:
                return "TC04";

            case NOTARIATO:
                return "TC05";

            case RAGIONERI_PERITI_COMMERCIALISTI:
                return "TC06";

            case ENASARCO:
                return "TC07";

            case ENPACL:
                return "TC08";

            case ENPAM:
                return "TC09";

            case ENPAF:
                return "TC10";

            case ENPAV:
                return "TC11";

            case ENPAIA:
                return "TC12";

            case MARITTIME:
                return "TC13";

            case INPGI:
                return "TC14";

            case ONAOSI:
                return "TC15";

            case CASAGIT:
                return "TC16";

            case EPPI:
                return "TC17";

            case EPAP:
                return "TC18";

            case ENPAB:
                return "TC19";

            case ENPAPI:
                return "TC20";

            case ENPAP:
                return "TC21";

            case INPS:
                return "TC22";

        }

        return null;
    }

    public static TipoCassaEnum parseTipoCassa(String value)
    {
        if ( value == null )
            return null;

        value = value.toUpperCase().trim();

        if ( "TC01".equals(value) )
            return TipoCassaEnum.AVVOCATI_PROCURATORI;

        if ( "TC02".equals(value) )
            return TipoCassaEnum.DOTTORI_COMMERCIALISTI;

        if ( "TC03".equals(value) )
            return TipoCassaEnum.GEOMETRI;

        if ( "TC04".equals(value) )
            return TipoCassaEnum.INGENIERI_ARCHITETTI_LP;

        if ( "TC05".equals(value) )
            return TipoCassaEnum.NOTARIATO;

        if ( "TC06".equals(value) )
            return TipoCassaEnum.RAGIONERI_PERITI_COMMERCIALISTI;

        if ( "TC07".equals(value) )
            return TipoCassaEnum.ENASARCO;

        if ( "TC08".equals(value) )
            return TipoCassaEnum.ENPACL;

        if ( "TC09".equals(value) )
            return TipoCassaEnum.ENPAM;

        if ( "TC10".equals(value) )
            return TipoCassaEnum.ENPAF;

        if ( "TC11".equals(value) )
            return TipoCassaEnum.ENPAV;

        if ( "TC12".equals(value) )
            return TipoCassaEnum.ENPAIA;

        if ( "TC13".equals(value) )
            return TipoCassaEnum.MARITTIME;

        if ( "TC14".equals(value) )
            return TipoCassaEnum.INPGI;

        if ( "TC15".equals(value) )
            return TipoCassaEnum.ONAOSI;

        if ( "TC16".equals(value) )
            return TipoCassaEnum.CASAGIT;

        if ( "TC17".equals(value) )
            return TipoCassaEnum.EPPI;

        if ( "TC18".equals(value) )
            return TipoCassaEnum.EPAP;

        if ( "TC19".equals(value) )
            return TipoCassaEnum.ENPAB;

        if ( "TC20".equals(value) )
            return TipoCassaEnum.ENPAPI;

        if ( "TC21".equals(value) )
            return TipoCassaEnum.ENPAP;

        if ( "TC22".equals(value) )
            return TipoCassaEnum.INPGI;

        return null;
    }

    /*
     * Tipo documento
     */
    @SuppressWarnings("incomplete-switch")
    public static String formatTipoDocumento(TipoDocumentoEnum tipoDocumento)
    {
        if ( tipoDocumento == null )
            return null;

        switch ( tipoDocumento )
        {
            case FATTURA:
                return "TD01";

            case NOTA_CREDITO:
                return "TD04";

            case NOTA_DEBITO:
                return "TD05";

            case PARCELLA:
                return "TD06";

            case ACCONTO_ANTICIPO_FATTURA:
                return "TD02";

            case ACCONTO_ANTICIPO_PARCELLA:
                return "TD03";

            case FATTURA_DIFFERITA:
                return "TD24";

        }

        return null;
    }

    public static TipoDocumentoEnum parseTipoDocumento(String value)
    {
        if ( value == null )
            return null;

        value = value.toUpperCase().trim();

        if ( "TD01".equals(value) )
            return TipoDocumentoEnum.FATTURA;

        if ( "TD04".equals(value) )
            return TipoDocumentoEnum.NOTA_CREDITO;

        if ( "TD05".equals(value) )
            return TipoDocumentoEnum.NOTA_DEBITO;

        if ( "TD06".equals(value) )
            return TipoDocumentoEnum.PARCELLA;

        if ( "TD02".equals(value) )
            return TipoDocumentoEnum.ACCONTO_ANTICIPO_FATTURA;

        if ( "TD03".equals(value) )
            return TipoDocumentoEnum.ACCONTO_ANTICIPO_PARCELLA;

        if ( "TD24".equals(value) )
            return TipoDocumentoEnum.FATTURA_DIFFERITA;

        return null;
    }

    /*
     * Tipo ritenuta
     */
    public static String formatTipoRitenuta(TipoRitenutaEnum value)
    {
        if ( value == null )
            return null;

        switch ( value )
        {
            case PERSONE_FISICHE:
                return "RT01";

            case PERSONE_GIURIDICHE:
                return "RT02";

        }

        return null;

    }

    public static TipoRitenutaEnum parseTipoRitenuta(String value)
    {
        if ( value == null )
            return null;

        value = value.toUpperCase().trim();

        if ( "RT01".equals(value) )
            return TipoRitenutaEnum.PERSONE_FISICHE;

        if ( "RT02".equals(value) )
            return TipoRitenutaEnum.PERSONE_GIURIDICHE;

        return null;
    }

    /*
     * Soggetto emittente
     */

    public static String formatSoggettoEmittente(SoggettoEmittenteEnum value)
    {
        if ( value == null )
            return null;

        switch ( value )
        {
            case CESSIONARIO_COMMITTENTE:
                return "CC";

            case TERZO:
                return "TZ";

        }

        return null;
    }

    public static SoggettoEmittenteEnum parseSoggettoEmittente(String value)
    {
        if ( value == null )
            return null;

        value = value.toUpperCase().trim();

        if ( "CC".equals(value) )
            return SoggettoEmittenteEnum.CESSIONARIO_COMMITTENTE;

        if ( "TZ".equals(value) )
            return SoggettoEmittenteEnum.TERZO;

        return null;
    }

    /*
     * Regime fiscale
     */
    @SuppressWarnings("incomplete-switch")
    public static String formatRegimeFiscale(RegimeFiscaleEnum value)
    {
        if ( value == null )
            return null;

        switch ( value )
        {
            case ORDINARIO:
                return "RF01";

            case CONTRIBUENTI_MINIMI:
                return "RF02";

            case AGRICOLTURA_PESCA:
                return "RF04";

            case SALI_TABACCHI:
                return "RF05";

            case FIAMMIFERI:
                return "RF06";

            case EDITORIA:
                return "RF07";

            case TELEFONIA:
                return "RF08";

            case TRASPORTO_PUBBLICO:
                return "RF09";

            case INTRATTENIMENTI:
                return "RF10";

            case VIAGGI_TURISMO:
                return "RF11";

            case AGRITURISMO:
                return "RF12";

            case DOMICILIO:
                return "RF13";

            case RIVENDITA_ARTE_COLLEZIONE:
                return "RF14";

            case AGENZIE_ARTE_COLLEZIONE:
                return "RF15";

            case CASSA_PA:
                return "RF16";

            case CASSA:
                return "RF17";

            case REGIME_FORFETTARIO:
                return "RF19";

            case ALTRO:
                return "RF18";

        }

        return null;
    }

    public static RegimeFiscaleEnum parseRegimeFiscale(String value)
    {
        if ( value == null )
            return null;

        value = value.toUpperCase().trim();

        if ( "RF01".equals(value) )
            return RegimeFiscaleEnum.ORDINARIO;

        if ( "RF02".equals(value) )
            return RegimeFiscaleEnum.CONTRIBUENTI_MINIMI;

        if ( "RF04".equals(value) )
            return RegimeFiscaleEnum.AGRICOLTURA_PESCA;

        if ( "RF05".equals(value) )
            return RegimeFiscaleEnum.SALI_TABACCHI;

        if ( "RF06".equals(value) )
            return RegimeFiscaleEnum.FIAMMIFERI;

        if ( "RF07".equals(value) )
            return RegimeFiscaleEnum.EDITORIA;

        if ( "RF08".equals(value) )
            return RegimeFiscaleEnum.TELEFONIA;

        if ( "RF09".equals(value) )
            return RegimeFiscaleEnum.TRASPORTO_PUBBLICO;

        if ( "RF10".equals(value) )
            return RegimeFiscaleEnum.INTRATTENIMENTI;

        if ( "RF11".equals(value) )
            return RegimeFiscaleEnum.VIAGGI_TURISMO;

        if ( "RF12".equals(value) )
            return RegimeFiscaleEnum.AGRITURISMO;

        if ( "RF13".equals(value) )
            return RegimeFiscaleEnum.DOMICILIO;

        if ( "RF14".equals(value) )
            return RegimeFiscaleEnum.RIVENDITA_ARTE_COLLEZIONE;

        if ( "RF15".equals(value) )
            return RegimeFiscaleEnum.AGENZIE_ARTE_COLLEZIONE;

        if ( "RF16".equals(value) )
            return RegimeFiscaleEnum.CASSA_PA;

        if ( "RF17".equals(value) )
            return RegimeFiscaleEnum.CASSA;

        if ( "RF18".equals(value) )
            return RegimeFiscaleEnum.ALTRO;

        if ( "RF19".equals(value) )
            return RegimeFiscaleEnum.REGIME_FORFETTARIO;

        return null;
    }

    /*
     * Tipo pagamento
     */
    public static String formatTipoPagamento(TipoPagamentoEnum tipoPagamento)
    {
        if ( tipoPagamento == null )
            return null;

        switch ( tipoPagamento )
        {
            case COMPLETO:
                return "TP02";

            case RATE:
                return "TP01";

            case ANTICIPO:
                return "TP03";
        }

        return null;
    }

    public static TipoPagamentoEnum parseTipoPagamento(String value)
    {
        if ( value == null )
            return null;

        value = value.toUpperCase().trim();

        if ( "TP02".equals(value) )
            return TipoPagamentoEnum.COMPLETO;

        if ( "TP01".equals(value) )
            return TipoPagamentoEnum.RATE;

        if ( "TP03".equals(value) )
            return TipoPagamentoEnum.ANTICIPO;

        return null;
    }

    /*
     * Modalita pagamento
     */
    public static String formatModalitaPagamento(ModalitaPagamentoEnum tipoPagamento)
    {
        if ( tipoPagamento == null )
            return null;

        switch ( tipoPagamento )
        {
            case CONTANTI:
                return "MP01";

            case ASSEGNO_CIRCOLARE:
                return "MP03";

            case ASSEGNO:
                return "MP02";

            case BOLLETTINO_BANCA:
                return "MP07";

            case BONIFICO:
                return "MP05";

            case CARTA_CREDITO:
                return "MP08";

            case RID:
                return "MP09";

            case RID_UTENZE:
                return "MP10";

            case RID_VELOCE:
                return "MP11";

            case RIBA:
                return "MP12";

            case MAV:
                return "MP13";

            case CONTANTI_TEROSERIA:
                return "MP04";

            case VAGLIA:
                return "MP06";

            case ERARIO:
                return "MP14";

            case DOMICILIAZIONE_BANCA:
                return "MP16";

            case DOMICILIAZIONE_POSTA:
                return "MP17";

            case GIROCONTO:
                return "MP15";

            case BOLLETTINO_POSTA:
                return "MP18";

            case SEPA_DD:
                return "MP19";

            case SEPA_DDC:
                return "MP20";

            case SEPA_B2B:
                return "MP21";

            case TRATTENUTA_RISCOSSE:
                return "MP22";

        }

        return null;
    }

    public static ModalitaPagamentoEnum parseModalitaPagamento(String value)
    {
        if ( value == null )
            return null;

        value = value.toUpperCase().trim();

        if ( "MP01".equals(value) )
            return ModalitaPagamentoEnum.CONTANTI;

        if ( "MP03".equals(value) )
            return ModalitaPagamentoEnum.ASSEGNO_CIRCOLARE;

        if ( "MP02".equals(value) )
            return ModalitaPagamentoEnum.ASSEGNO;

        if ( "MP07".equals(value) )
            return ModalitaPagamentoEnum.BOLLETTINO_BANCA;

        if ( "MP05".equals(value) )
            return ModalitaPagamentoEnum.BONIFICO;

        if ( "MP08".equals(value) )
            return ModalitaPagamentoEnum.CARTA_CREDITO;

        if ( "MP09".equals(value) )
            return ModalitaPagamentoEnum.RID;

        if ( "MP10".equals(value) )
            return ModalitaPagamentoEnum.RID_UTENZE;

        if ( "MP11".equals(value) )
            return ModalitaPagamentoEnum.RID_VELOCE;

        if ( "MP12".equals(value) )
            return ModalitaPagamentoEnum.RIBA;

        if ( "MP13".equals(value) )
            return ModalitaPagamentoEnum.MAV;

        if ( "MP04".equals(value) )
            return ModalitaPagamentoEnum.CONTANTI_TEROSERIA;

        if ( "MP06".equals(value) )
            return ModalitaPagamentoEnum.VAGLIA;

        if ( "MP14".equals(value) )
            return ModalitaPagamentoEnum.ERARIO;

        if ( "MP16".equals(value) )
            return ModalitaPagamentoEnum.DOMICILIAZIONE_BANCA;

        if ( "MP17".equals(value) )
            return ModalitaPagamentoEnum.DOMICILIAZIONE_POSTA;

        if ( "MP15".equals(value) )
            return ModalitaPagamentoEnum.GIROCONTO;

        if ( "MP18".equals(value) )
            return ModalitaPagamentoEnum.BOLLETTINO_POSTA;

        if ( "MP19".equals(value) )
            return ModalitaPagamentoEnum.SEPA_DD;

        if ( "MP20".equals(value) )
            return ModalitaPagamentoEnum.SEPA_DDC;

        if ( "MP21".equals(value) )
            return ModalitaPagamentoEnum.SEPA_B2B;

        if ( "MP22".equals(value) )
            return ModalitaPagamentoEnum.TRATTENUTA_RISCOSSE;

        return null;
    }

    /*
     * Tipo esigibilita iva
     */
    public static String formatTipoEsigibilitaIva(EsigibilitaIvaEnum value)
    {
        if ( value == null )
            return null;

        switch ( value )
        {
            case DIFFERITA:
                return "D";

            case IMMEDIATA:
                return "I";

            case SCISSIONE:
                return "S";

        }

        return null;

    }

    public static EsigibilitaIvaEnum parseTipoEsigibilitaIva(String value)
    {
        if ( value == null )
            return null;

        value = value.toUpperCase().trim();

        if ( "D".equals(value) )
            return EsigibilitaIvaEnum.DIFFERITA;

        if ( "I".equals(value) )
            return EsigibilitaIvaEnum.IMMEDIATA;

        if ( "S".equals(value) )
            return EsigibilitaIvaEnum.SCISSIONE;

        return null;
    }

    /*
     * Natura esenzione iva
     */
    public static String formatNaturaEsenzione(NaturaEsenzioneEnum value)
    {
        if ( value == null )
            return null;

        switch ( value )
        {
            case ESCLUSE_ART_15:
                return "N1";

            case NON_SOGGETTE:
                return "N2.1";

            case NON_IMPONIBILI:
                return "N3";

            case ESENTI:
                return "N4";

            case REGIME:
                return "N5";

            case INVERSIONE:
                return "N6";

            case IVA_UE:
                return "N7";

        }

        return null;

    }

    public static NaturaEsenzioneEnum parseNaturaEsenzione(String value)
    {
        if ( value == null )
            return null;

        value = value.toUpperCase().trim();

        if ( "N1".equals(value) )
            return NaturaEsenzioneEnum.ESCLUSE_ART_15;

        if ( "N2.1".equals(value) )
            return NaturaEsenzioneEnum.NON_SOGGETTE;

        if ( "N3".equals(value) )
            return NaturaEsenzioneEnum.NON_IMPONIBILI;

        if ( "N4".equals(value) )
            return NaturaEsenzioneEnum.ESENTI;

        if ( "N5".equals(value) )
            return NaturaEsenzioneEnum.REGIME;

        if ( "N6".equals(value) )
            return NaturaEsenzioneEnum.INVERSIONE;

        if ( "N7".equals(value) )
            return NaturaEsenzioneEnum.IVA_UE;

        return null;
    }

    /*
     * Boolean socio unico
     */
    public static String formatBooleanSocioUnico(boolean value)
    {
        return value ? "SU" : "SM";
    }

    public static Boolean parseBooleanSocioUnico(String value)
    {
        if ( value == null )
            return false;

        value = value.toUpperCase().trim();

        if ( "SU".equals(value) )
            return true;

        if ( "SM".equals(value) )
            return false;

        return false;
    }

    /*
     * Boolean liquidazione
     */
    public static String formatBooleanLiquidazione(boolean value)
    {
        return value ? "LS" : "LN";
    }

    public static Boolean parseBooleanLiquidazione(String value)
    {
        if ( value == null )
            return false;

        value = value.toUpperCase().trim();

        if ( "LS".equals(value) )
            return true;

        if ( "LN".equals(value) )
            return false;

        return false;
    }

    /*
     * Tipo cessazione prestazione
     */
    public static String formatTipoCessazionePrestazione(TipoCessazionePrestazioneEnum value)
    {
        if ( value == null )
            return null;

        switch ( value )
        {
            case SCONTO:
                return "SC";

            case PREMIO:
                return "PR";

            case ABBUONO:
                return "AB";

            case SPECSA_ACCESSORIA:
                return "AC";

        }

        return null;

    }

    public static TipoCessazionePrestazioneEnum parseTipoCessazionePrestazione(String value)
    {
        if ( value == null )
            return null;

        value = value.toUpperCase().trim();

        if ( "SC".equals(value) )
            return TipoCessazionePrestazioneEnum.SCONTO;

        if ( "PR".equals(value) )
            return TipoCessazionePrestazioneEnum.PREMIO;

        if ( "AB".equals(value) )
            return TipoCessazionePrestazioneEnum.ABBUONO;

        if ( "AC".equals(value) )
            return TipoCessazionePrestazioneEnum.SPECSA_ACCESSORIA;

        return null;
    }

    /*
     * Tipo resa
     */
    public static String formatTipoResa(TipoResaEnum tipoResa)
    {
        if ( tipoResa == null )
            return null;

        switch ( tipoResa )
        {
            case CFR:
                return "CFR";
            case CIF:
                return "CIF";
            case CIP:
                return "CIP";
            case CPT:
                return "CPT";
            case DAF:
                return "DAF";
            case DDP:
                return "DDP";
            case DDU:
                return "DDU";
            case DEQ:
                return "DEQ";
            case DES:
                return "DES";
            case EXW:
                return "EXW";
            case FAS:
                return "FAS";
            case FCA:
                return "FCA";
            case FOB:
                return "FOB";
        }

        return null;
    }

    public static TipoResaEnum parseTipoResa(String value)
    {
        if ( value == null )
            return null;

        value = value.toUpperCase().trim();

        if ( "CFR".equals(value) )
            return TipoResaEnum.CFR;
        if ( "CIF".equals(value) )
            return TipoResaEnum.CIF;
        if ( "CIP".equals(value) )
            return TipoResaEnum.CIP;
        if ( "CPT".equals(value) )
            return TipoResaEnum.CPT;
        if ( "DAF".equals(value) )
            return TipoResaEnum.DAF;
        if ( "DDP".equals(value) )
            return TipoResaEnum.DDP;
        if ( "DDU".equals(value) )
            return TipoResaEnum.DDU;
        if ( "DEQ".equals(value) )
            return TipoResaEnum.DEQ;
        if ( "DES".equals(value) )
            return TipoResaEnum.DES;
        if ( "EXW".equals(value) )
            return TipoResaEnum.EXW;
        if ( "FAS".equals(value) )
            return TipoResaEnum.FAS;
        if ( "FCA".equals(value) )
            return TipoResaEnum.FCA;
        if ( "FOB".equals(value) )
            return TipoResaEnum.FOB;

        return null;
    }

    /*
     * Esito trasferimento ftp
     */
    public static String formatEsitoTrasferimentoFtp(EsitoTrasferimentoFtpEnum esitoTrasferimento)
    {
        if ( esitoTrasferimento == null )
            return null;

        switch ( esitoTrasferimento )
        {
            case OK:
                return EsitoTrasferimentoFtpEnum.OK.getCodice();
            case ERRORE:
                return EsitoTrasferimentoFtpEnum.ERRORE.getCodice();
        }

        return null;
    }

    public static EsitoTrasferimentoFtpEnum parseEsitoTrasferimentoFtp(String value)
    {
        if ( value == null )
            return null;

        value = value.toUpperCase().trim();

        if ( EsitoTrasferimentoFtpEnum.OK.getCodice().equals(value) )
            return EsitoTrasferimentoFtpEnum.OK;
        if ( EsitoTrasferimentoFtpEnum.ERRORE.getCodice().equals(value) )
            return EsitoTrasferimentoFtpEnum.ERRORE;

        return null;
    }
}

