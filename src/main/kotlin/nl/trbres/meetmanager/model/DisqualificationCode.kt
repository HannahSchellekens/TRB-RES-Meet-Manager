package nl.trbres.meetmanager.model

/**
 * @author Ruben Schellekens
 */
enum class DisqualificationCode(val letter: String, val message: String, val type: Type) {

    // General
    AA("A", "Gestart voor het startsignaal (en wel gezwommen).", Type.GENERAL),
    AB("B", "Gestart voor het startsignaal en de start is afgefloten (uitsluiten voor dat progr.nr.).", Type.GENERAL),
    AC("C", "Na een officiële waarschuwing zich niet gehouden aan de startdiscipline (uitsluiten voor dat progr.nr.).", Type.GENERAL),
    AD("D", "Gestart nadat het vertrek heeft plaatsgehad.", Type.GENERAL),
    AE("E", "De aangegeven zwemslag niet uitgevoerd.", Type.GENERAL),
    AF("F", "De aangegeven afstand niet uitgezwommen.", Type.GENERAL),
    AH("H", "Gezwommen in een andere baan dan waarin is gestart.", Type.GENERAL),
    AI("I", "Gelopen over en/of afgezet van de bodem.", Type.GENERAL),
    AJ("J", "Te water gegaan tijdens het zwemmen van een nummer, waarop men niet is ingeschreven.", Type.GENERAL),
    AK("K", "Na het beëindigen van de race niet onmiddellijk het water verlaten.", Type.GENERAL),
    AL("L", "Tijdens het water verlaten een deelnemer gehinderd die de race nog niet heeft beëindigd.", Type.GENERAL),
    AM("M", "Gebruik maken van hulpmiddelen (het meelopen met de deelnemer - in het zwembad - wordt gelijkgesteld met gebruik van hulpmiddelen).", Type.GENERAL),
    AN("N", "Leeftijdsbepalingen overschreden.", Type.GENERAL),
    AO("O", "Niet gerechtigd gestart.", Type.GENERAL),
    AP("P", "Schuldig gemaakt aan ongepast gedrag. Bij ernstige vorm (wangedrag) uitsluiten van (verdere) deelneming aan en/of aanwezigheid bij de wedstrijd.", Type.GENERAL),
    AQ("Q", "Niet goedgekeurde zwemkleding gebruikt.", Type.GENERAL),

    // Schoolslag
    SA("A", "Na start en/of keerpunt niet met het hoofd het wateroppervlak doorbroken, voordat bij het wijdste gedeelte van de 2e armslag de handen naar binnen beginnen te draaien.", Type.BREASTSTROKE),
    SB("B", "Een deel van het hoofd heeft gedurende de gehele race niet tijdens iedere volledige of onvolledige cyclus het wateroppervlak daadwerkelijk doorbroken.", Type.BREASTSTROKE),
    SC("C", "De bewegingen van de armen niet te allen tijde gelijktijdig in hetzelfde horizontale vlak uitgevoerd.", Type.BREASTSTROKE),
    SD("D", "De bewegingen van de benen niet te allen tijde gelijktijdig in hetzelfde horizontale vlak uitgevoerd.", Type.BREASTSTROKE),
    SE("E", "De handen niet te zamen van de borst naar voren gebracht.", Type.BREASTSTROKE),
    SF("F", "Tijdens de race de ellebogen niet onder het wateroppervlak gehouden.", Type.BREASTSTROKE),
    SG("G", "Na de 1e armslag na start en/of keerpunt de handen voorbij de heuplijn gebracht.", Type.BREASTSTROKE),
    SH("H", "Bij de achterwaartse beweging van de beenslag de voeten niet naar buiten bewogen.", Type.BREASTSTROKE),
    SI("I", "Stond tijdens de race.", Type.BREASTSTROKE),
    SJ("J", "Na start en/of keerpunt een verkeerde arm- en/of beenslag gemaakt.", Type.BREASTSTROKE),
    SK("K", "Het keer- en/of eindpunt niet gelijktijdig met twee handen aangetikt.", Type.BREASTSTROKE),
    SL("L", "Het keer- en/of eindpunt met één hand aangetikt.", Type.BREASTSTROKE),
    SM("M", "Bij het keer- en/of eindpunt niet aangetikt met de handen (wel afgezet).", Type.BREASTSTROKE),
    SN("N", "Keer- of eindpunt in het geheel niet aangeraakt.", Type.BREASTSTROKE),
    SO("O", "Tijdens de race op de rug gedraaid.", Type.BREASTSTROKE),
    SP("P", "Eén of meerdere vlinderbeenslagen gemaakt na de eerste schoolslagbeenslag.", Type.BREASTSTROKE),
    SQ("Q", "Bij de laatste slag voor keer- of eindpunt een beenslag gemaakt, die niet vooraf is gegaan door een armslag.", Type.BREASTSTROKE),
    SR("R", "Meer dan één vlinderbeenslag na start en/of keerpunt.", Type.BREASTSTROKE),
    SS("S", "Met handen over elkaar keer- of eindpunt aangetikt.", Type.BREASTSTROKE),
    ST("T", "Bij het loslaten van de wand na het keerpunt de borstligging niet aangenomen.", Type.BREASTSTROKE),
    SU("U", "Niet gezwommen in de cyclus van eerst armslag dan beenslag of een onvolledige cyclus (behalve bij delaatste slag voor keer- of eindpunt).", Type.BREASTSTROKE),

    // Rugslag
    RA("A", "Na start en/of keerpunt niet bij of voor de 15 m met het hoofd het wateroppervlak doorbroken.", Type.BACKSTROKE),
    RB("B", "Stond tijdens de race.", Type.BACKSTROKE),
    RC("C", "Rugligging verlaten tijdens de race zonder het keerpunt in te zetten.", Type.BACKSTROKE),
    RD("D", "Rugligging verlaten en stuwbewegingen met armen en/of benen gemaakt zonder het keerpunt in te zetten.", Type.BACKSTROKE),
    RE("E", "Het keer- en/of eindpunt niet aangeraakt met enig lichaamsdeel.", Type.BACKSTROKE),
    RF("F", "Bij het loslaten van de wand na het keerpunt de rugligging niet aangenomen.", Type.BACKSTROKE),
    RH("H", "Het eindpunt niet in rugligging aangeraakt.", Type.BACKSTROKE),
    RI("I", "Tijdens de start de tenen over de overloopgoot of over de bovenkant van de aantikplaat geklemd.", Type.BACKSTROKE),
    RJ("J", "Lichaam geheel onder water gedurende de race of bij de finish (geldt niet voor keerpunt en eerste 15 m na start en keerpunt)", Type.BACKSTROKE),
    RK("K", "Keer- of eindpunt niet in eigen baan aangeraakt.", Type.BACKSTROKE),
    RL("L", "Niet met tenminste één teen van elke voet in contact met de wand of met de aantikplaat indien er bij de start een rugslagvoetsteun is gebruikt.", Type.BACKSTROKE),

    // Vlinderslag
    VA("A", "Na de start en/of keerpunt meer dan één arm-doortrekbeweging onder water gemaakt.", Type.BUTTERFLY),
    VB("B", "Beide armen niet te zamen naar voren en/of gelijktijdig achterwaarts gebracht.", Type.BUTTERFLY),
    VC("C", "Armen niet over het water naar voren gebracht.", Type.BUTTERFLY),
    VD("D", "Schoolslagbenen gezwommen of andere stuwende beweging in horizontale vlak.", Type.BUTTERFLY),
    VE("E", "De bewegingen van de voeten niet op gelijke wijze uitgevoerd.", Type.BUTTERFLY),
    VF("F", "Stond tijdens de race.", Type.BUTTERFLY),
    VG("G", "Bij de laatste armslag voor keer- en/of eindpunt de armen niet over het water naar voren gebracht.", Type.BUTTERFLY),
    VH("H", "Niet met twee handen gelijktijdig keer- en/of eindpunt aangetikt.", Type.BUTTERFLY),
    VI("I", "Met één hand keer- en/of eindpunt aangetikt.", Type.BUTTERFLY),
    VJ("J", "Niet aangetikt met de handen bij het keer- en/of eindpunt (wel afgezet).", Type.BUTTERFLY),
    VK("K", "Keerpunt of eindpunt in het geheel niet aangeraakt.", Type.BUTTERFLY),
    VL("L", "Tijdens de race op de rug gedraaid.", Type.BUTTERFLY),
    VM("M", "Na start en/of keerpunt niet bij of voor de 15 meter met het hoofd het wateroppervlak doorbroken.", Type.BUTTERFLY),
    VN("N", "Lichaam geheel onder water gedurende de race (geldt niet voor keerpunt en eerste 15 m na start en keerpunt).", Type.BUTTERFLY),
    VO("O", "De armen niet gedurende de gehele race over het water naar voren gebracht (meerdere beenslagen achter elkaar zonder armslag).", Type.BUTTERFLY),
    VP("P", "Met handen over elkaar keerpunt of eindpunt aangetikt.", Type.BUTTERFLY),
    VQ("Q", "Bij het loslaten van de wand na het keerpunt de borstligging niet aangenomen.", Type.BUTTERFLY),

    // Vrije slag
    VRA("A", "Het keer- en/of eindpunt niet aangeraakt met enig lichaamsdeel.", Type.FREESTYLE),
    VRB("B", "Na start en/of keerpunt niet bij of voor de 15 meter met het hoofd het wateroppervlak doorbroken.", Type.FREESTYLE),
    VRC("C", "Lichaam geheel onder water gedurende de race (geldt niet voor keerpunt en eerste 15m na start en keerpunt).", Type.FREESTYLE),
    VRD("D", "Tijdens de race gelopen over de bodem.", Type.FREESTYLE),

    // Estafettes
    EA("A", "Te vroeg overgenomen.", Type.RELAY),
    EB("B", "De wisselslag niet gezwommen in de juiste volgorde (rug-, school-, vlinder- en vrije slag).", Type.RELAY),
    EC("C", "De estafette niet gezwommen met de opgegeven deelnemers.", Type.RELAY),
    ED("D", "De estafette niet gezwommen conform de opgegeven startvolgorde.", Type.RELAY),
    EE("E", "Bij de estafette wisselslag de vrije slag gezwommen als school-, vlinder-, of rugslag.", Type.RELAY),
    EF("F", "Een ander ploeglid van de zwemmer die de afstand moet zwemmen, te water gegaan voordat alle ploegen de wedstrijd hebben beëindigd.", Type.RELAY),

    // Wisselslag
    WA("A", "De vrije slag gezwommen als school-, vlinder- of rugslag.", Type.MEDLEY),
    WB("B","De wisselslag niet gezwommen in de juiste volgorde.", Type.MEDLEY);

    /**
     * The full disqualification code (e.g. VB).
     */
    val code: String = type.prefix + letter

    override fun toString() = code + " - " + message

    /**
     * @author Ruben Schellekens
     */
    enum class Type(val typeName: String, val prefix: String) {

        GENERAL("Algemeen", "A"),
        BREASTSTROKE("Schoolslag", "S"),
        BACKSTROKE("Rugslag", "R"),
        BUTTERFLY("Vlinderslag", "V"),
        FREESTYLE("Vrije slag", "VR"),
        RELAY("Estafette", "E"),
        MEDLEY("Wisselslag", "W")
    }
}