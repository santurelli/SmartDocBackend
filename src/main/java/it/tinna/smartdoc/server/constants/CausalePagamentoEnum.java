package it.tinna.smartdoc.server.constants;

public enum CausalePagamentoEnum
{
 A("Prestazioni di lavoro autonomo rientranti nell'esercizio di arte o professione abituale"),
 B("Utilizzazione economica, da parte dell'autore o dell'inventore, di opere dell'ingegno, di brevetti industriali e di processi, formule o informazioni relativi ad esperienze acquisite in campo industriale, commerciale o scientifico"),
 C("Utili derivanti da contratti di associazione in partecipazione e da contratti di cointeressenza, quando l'apporto è costituito esclusivamente dalla prestazione di lavoro"),
 D("Utili spettanti ai soci promotori ed ai soci fondatori delle società di capitali"),
 E("Levata di protesti cambiari da parte dei segretari comunali"),
 G("Indennità corrisposte per la cessazione di attività sportiva professionale"),
 H("Indennità corrisposte per la cessazione dei rapporti di agenzia delle persone fisiche e delle società di persone con esclusione delle somme maturate entro il 31 dicembre 2003, già imputate per competenza e tassate come reddito d'impresa"),
 I("Indennità corrisposte per la cessazione da funzioni notarili"),
 L("Utilizzazione economica, da parte di soggetto diverso dall'autore o dall'inventore, di opere dell'ingegno, di brevetti industriali e di processi, formule e informazioni relativi ad esperienze acquisite in campo industriale, commerciale o scientifico"),
 M("Prestazioni di lavoro autonomo non esercitate abitualmente, obblighi di fare, di non fare o permettere"),
 N("Indennità di trasferta, rimborso forfetario di spese, premi e compensi erogati: nell'esercizio diretto di attività sportive dilettantistiche; in relazione a rapporti di collaborazione coordinata e continuativa di carattere amministrativo-gestionale di natura non professionale resi a favore di società e associazioni sportive dilettantistiche e di cori, bande e filodrammatiche da parte del direttore e dei collaboratori tecnici"),
 O("Prestazioni di lavoro autonomo non esercitate abitualmente, obblighi di fare, di non fare o permettere, per le quali non sussiste l'obbligo di iscrizione alla gestione separata (Cir. INPS n. 104/2001)"),
 P("Compensi corrisposti a soggetti non residenti privi di stabile organizzazione per l'uso o la concessione in uso di attrezzature industriali, commerciali o scientifiche che si trovano nel territorio dello Stato ovvero a società svizzere o stabili organizzazioni di società svizzere che possiedono i requisiti di cui all'art. 15, comma 2 dell'Accordo tra la Comunità europea e la Confederazione svizzera del 26 ottobre 2004 (pubblicato in G.U.C.E. del 29 dicembre 2004 n. L385/30)"),
 Q("Provvigioni corrisposte ad agente o rappresentante di commercio monomandatario"),
 R("Provvigioni corrisposte ad agente o rappresentante di commercio plurimandatario"),
 S("Provvigioni corrisposte a commissionario"),
 T("Provvigioni corrisposte a mediatore"),
 U("Provvigioni corrisposte a procacciatore di affari"),
 V("Provvigioni corrisposte a incaricato per le vendite a domicilio; provvigioni corrisposte a incaricato per la vendita porta a porta e per la vendita ambulante di giornali quotidiani e periodici (L. 25 febbraio 1987, n. 67)"),
 W("Corrispettivi erogati nel 2008 per prestazioni relative a contratti d'appalto cui si sono resi applicabili le disposizioni contenute nell'art. 25-ter del D.P.R. n. 600 del 1973"),
 X("Canoni corrisposti nel 2004 da società o enti residenti ovvero da stabili organizzazioni di società estere di cui all'art. 26-quater, comma 1, lett. a) e b) del D.P.R. 600/73, a società o stabili organizzazioni di società, situate in altro stato membro dell'Unione Europea in presenza dei requisiti di cui al citato art. 26-quater, del D.P.R. 600/73, per i quali è stato effettuato, nell'anno 2006, il rimborso della ritenuta ai sensi dell'art. 4 del D.Lgs. 30 maggio 2005 n. 143"),
 Y("Canoni corrisposti dal 1° gennaio 2005 al 26 luglio 2005 da società o enti residenti ovvero da stabili organizzazioni di società estere di cui all'art. 26-quater, comma 1, lett. a) e b) del D.P.R. n. 600 del 1973, a società o stabili organizzazioni di società, situate in altro stato membro dell'Unione Europea in presenza dei requisiti di cui al citato art. 26-quater, del D.P.R. n. 600 del 1973, per i quali è stato effettuato, nell'anno 2006, il rimborso della ritenuta ai sensi dell'art. 4 del D.Lgs. 30 maggio 2005 n. 143"),
 Z("Altro titolo"),
 L1("Redditi derivanti dall'utilizzazione economica di opere dell'ingegno, di brevetti industriali e di processi, formule e informazioni relativi a esperienze acquisite in campo industriale, commerciale o scientifico, che sono percepiti da soggetti che abbiano acquistato a titolo oneroso i diritti alla loro utilizzazione"),
 M1("Redditi derivanti dall'assunzione di obblighi di fare, di non fare o permettere"),
 O1("Redditi derivanti dall'assunzione di obblighi di fare, di non fare o permettere, per le quali non sussiste l'obbligo di iscrizione alla gestione separata (Circ. INPS n. 104/2001)"),
 V1("Redditi derivanti da attivitaÃ  commerciali non esercitate abitualmente (ad esempio, provvigioni corrisposte per prestazioni occasionali ad agente o rappresentante di commercio, mediatore, procacciatore d'affari o incaricato per le vendite a domicilio)");

    private String descrizione;

    private CausalePagamentoEnum(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }
}

