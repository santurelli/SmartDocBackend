package it.tinna.smartdoc.server.constants;

public enum TipoResaEnum
{

 CFR("CFR. Cost and Freight"),
 CIF("CIF. Cost, Insurance and Freight"),
 CIP("CIP. Carriage and Insurance Paid to"),
 CPT("CPT. Carriage Paid To"),
 DAF("DAF. Delivered At Frontier"),
 DDP("DDP. Delivered Duty Paid"),
 DDU("DDU. Delivered Duty Unpaid"),
 DEQ("DEQ. Delivered Ex Quay"),
 DES("DES. Delivered Ex Ship"),
 EXW("EXW. Ex Works"),
 FAS("FAS. Free Alongside Ship"),
 FCA("FCA. Free Carrier"),
 FOB("FOB. Free On Board");

    private String descrizione;

    private TipoResaEnum(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }

}

