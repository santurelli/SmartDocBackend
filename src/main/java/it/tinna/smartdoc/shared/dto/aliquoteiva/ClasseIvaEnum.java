package it.tinna.smartdoc.shared.dto.aliquoteiva;

public enum ClasseIvaEnum
{

 IMPONIBILE("IM", "Imponibile"),
 NON_IMPONIBILI("NI", "Non imponibile"),
 NON_SOGGETTE("NS", "Non soggette"),
 ESENTE("EE", "Esente"),
 ESCLUSO("EC", "Escluso"),
 FUORI_CAMPO_IVA("FC", "Fuori campo"),
 REVERSE_CHARGE("RC", "Acquisto rev. charge"),
 INTRA_UE("IU", "Acquisto intra UE"),
 EXTRA_UE("EU", "Acquisto extra UE"),
 NON_ESPOSTA("NE", "Non esposta"),
 ALTRO("AA", "Altro");

    private final String classDescription;

    private final String classValue;

    ClasseIvaEnum(String classValue,
                  String classDescription)
    {
        this.classValue = classValue;
        this.classDescription = classDescription;
    }

    public String getDescrizione()
    {
        return classDescription;
    }

    public String getValore()
    {
        return classValue;
    }

}

