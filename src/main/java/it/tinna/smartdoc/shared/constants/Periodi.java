package it.tinna.smartdoc.shared.constants;

public enum Periodi
{
 ANNUALE("Annuale", 0),
 PRIMO_TIMESTRE("1° Trimestre", 0),
 SECONDO_TIMESTRE("2° Trimestre", 0),
 TERZO_TRIMESTRE("3° Trimestre", 0),
 QUARTO_TRIMESTRE("4° Trimestre", 0),
 GENNAIO("Gennaio", 1),
 FEBBRAIO("Febbraio", 2),
 MARZO("Marzo", 3),
 APRILE("Aprile", 4),
 MAGGIO("Maggio", 5),
 GIUGNO("Giugno", 6),
 LUGLIO("Luglio", 7),
 AGOSTO("Agosto", 8),
 SETTEMBRE("Settembre", 9),
 OTTOBRE("Ottobre", 10),
 NOVEMBRE("Novembre", 11),
 DICEMBRE("Dicembre", 12);

    private String descrizione;

    private int    mese;

    Periodi(String descrizione,
            int mese)
    {
        this.descrizione = descrizione;
        this.mese = mese;
    }

    public String getDescrizione()
    {
        return descrizione;
    }

    public int getMese()
    {
        return mese;
    }

}

