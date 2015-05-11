package dtp.jade;

public enum MessageType {

    // ----------------------------------------------------------//
    // ---------- Komunikaty wysylane przez GUIAgent'a ----------//

    // graf sieci transportowej
    GRAPH,

    // graf sieci transportowej - aktualizacja
    GRAPH_UPDATE,

    // info dotyczace symulacji
    SIM_INFO,

    // kolejny timestamp
    TIME_CHANGED,

    // zlecenie transportowe
    COMMISSION,

    // prosba o kalendarz EUnit'a
    EUNIT_SHOW_CALENDAR,

    // prosba o statystyki EUnit'a
    EUNIT_SHOW_STATS,

    // prosba o statystyki EUnit'a do pozniejszego zapisu do pliku
    EUNIT_SHOW_STATS_TO_WRITE,

    // prosba o reset EUnit'a i DistributorAgent'a
    RESET,

    // prosba o wyslanie listy niezrealizowachy zlecen
    DISTRIBUTOR_SHOW_NOONE_LIST,

    // sytuacja kryzysowa
    CRISIS_EVENT,

    DRIVER_CREATION,

    TRUCK_CREATION,

    TRAILER_CREATION,

    SIM_END,

    //prosba o zaktualizowanie przez wszystkie holony obecnej lokacji na podstawie timestampu
    UPDATE_CURRENT_LOCATION,

    //wiadomosc wysylana do wszystkich eunitow z informacja aby wracali do depotu
    BACK_TO_DEPOT,

    // ------------------------------------------------------------------ //
    // ---------- Komunikaty wysylane przez DistributorAgent'a ---------- //

    // prosba o utworzenie nowego EUnit'a
    EXECUTION_UNIT_CREATION,

    GUI_MESSAGE,

    // prosba o oferte EUnit'a
    COMMISSION_OFFER_REQUEST,

    // odpowiedz na oferte - info czy EUnit wygral aukcje zlecen
    FEEDBACK,

    // liczba zlecen na liscie niezrealizowanych
    NOONE_LIST,

    // ----------------------------------------------------------- //
    // ---------- Komunikaty wysylane przez EUnitAgent'a ----------//

    // AID EUnit'a
    EXECUTION_UNIT_AID,

    // kalendarz EUnit'a
    EUNIT_MY_CALENDAR,

    // statystyki EUnit'a
    EUNIT_MY_STATS,

    // statystyki EUnit'a zapisywane do pliku
    EUNIT_MY_FILE_STATS,

    // oferta dla DistributorAgent'a
    COMMISSION_OFFER,

    // info o EUnitAgent
    EUNIT_INFO,


    // ----------------------------------------------------------- //
    // ------ Komunikaty wysylane przez TransportAgent'�w ------//

    // oferta elementu transportowego
    TRANSPORT_OFFER,

    TRANSPORT_COMMISSION,

    TRANSPORT_FEEDBACK,

    TRANSPORT_DRIVER_AID,

    TRANSPORT_INITIAL_DATA,

    TRANSPORT_TRUCK_AID,

    TRANSPORT_TRAILER_AID,

    TRANSPORT_REORGANIZE,

    TRANSPORT_REORGANIZE_OFFER,

    TRANSPORT_AGENT_CREATED,
    SIM_INFO_RECEIVED,
    TIME_STAMP_CONFIRM,

    // ----------------------------------------------------------- //
    // ----------------------------------------------------------- //

    /* WYSYLANE PRZEZ INFO AGENTA */
    EUNIT_INITIAL_DATA,

    // ----------------------------------------------------------- //
    /* Czesc odpowiedzialna za nowa koncepcje */
    AGENTS_DATA,
    AGENTS_DATA_FOR_TRANSPORTUNITS,
    TRANSPORT_AGENT_CONFIRMATION,
    TRANSPORT_AGENT_PREPARED_TO_NEGOTIATION,
    START_NEGOTIATION,
    TEAM_OFFER,
    TEAM_OFFER_RESPONSE,
    NEW_HOLON_OFFER,
    HOLON_FEEDBACK,
    COMMISSION_FOR_EUNIT,
    CONFIRMATIO_FROM_DISTRIBUTOR,

    COMMISSION_SEND_AGAIN,
    ST_BEGIN,

    /* ComplexST */

    HOLONS_CALENDAR,
    HOLONS_NEW_CALENDAR,

    WORST_COMMISSION_COST,
    CHANGE_SCHEDULE,

    SIMULATION_DATA,

    UNDELIVERED_COMMISSION,

    MEASURE_DATA,

    CONFIGURATION_CHANGE,

    MLTable,

    GRAPH_CHANGED,

    ASK_IF_GRAPH_LINK_CHANGED,

    GRAPH_LINK_CHANGED,

    VISUALISATION_MEASURE_SET_HOLONS,
    VISUALISATION_MEASURE_UPDATE,
    VISUALISATION_MEASURE_NAMES
}
