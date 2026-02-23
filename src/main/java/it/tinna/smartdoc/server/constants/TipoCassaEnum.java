package it.tinna.smartdoc.server.constants;

public enum TipoCassaEnum
{
 AVVOCATI_PROCURATORI("Cassa nazionale previdenza e assistenza avvocati e procuratori legali"),
 DOTTORI_COMMERCIALISTI("Cassa previdenza dottori commercialisti"),
 GEOMETRI("Cassa previdenza e assistenza geometri"),
 INGENIERI_ARCHITETTI_LP("Cassa nazionale previdenza e assistenza ingegneri e architetti liberi professionisti"),
 NOTARIATO("Cassa nazionale del notariato"),
 RAGIONERI_PERITI_COMMERCIALISTI("Cassa nazionale previdenza e assistenza ragionieri e periti commerciali"),
 ENASARCO("Ente nazionale assistenza agenti e rappresentanti di commercio (ENASARCO)"),
 ENPACL("Ente nazionale previdenza e assistenza consulenti del lavoro (ENPACL)"),
 ENPAM("Ente nazionale previdenza e assistenza medici (ENPAM)"),
 ENPAF("Ente nazionale previdenza e assistenza farmacisti (ENPAF)"),
 ENPAV("Ente nazionale previdenza e assistenza veterinari (ENPAV)"),
 ENPAIA("Ente nazionale previdenza e assistenza impiegati dell'agricoltura (ENPAIA)"),
 MARITTIME("Fondo previdenza impiegati imprese di spedizione e agenzie marittime"),
 INPGI("Istituto nazionale previdenza giornalisti italiani (INPGI)"),
 ONAOSI("Opera nazionale assistenza orfani sanitari italiani (ONAOSI)"),
 CASAGIT("Cassa autonoma assistenza integrativa giornalisti italiani (CASAGIT)"),
 EPPI("Ente previdenza periti industriali e periti industriali laureati (EPPI)"),
 EPAP("Ente previdenza e assistenza pluricategoriale (EPAP)"),
 ENPAB("Ente nazionale previdenza e assistenza biologi (ENPAB)"),
 ENPAPI("Ente nazionale previdenza e assistenza professione infermieristica (ENPAPI)"),
 ENPAP("Ente nazionale previdenza e assistenza psicologi (ENPAP)"),
 INPS("INPS");

    private String descrizione;

    private TipoCassaEnum(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }
}

