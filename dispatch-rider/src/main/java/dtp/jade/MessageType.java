package dtp.jade;

public enum MessageType {

    // info dotyczace symulacji
    SIM_INFO,

    // kolejny timestamp
    TIME_CHANGED,

    // zlecenie transportowe
    COMMISSION,

    // prosba o statystyki EUnit'a do pozniejszego zapisu do pliku
    EUNIT_SHOW_STATS_TO_WRITE,

    // sytuacja kryzysowa
    CRISIS_EVENT,

    DRIVER_CREATION,

    TRUCK_CREATION,

    TRAILER_CREATION,

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

    // ----------------------------------------------------------- //
    // ---------- Komunikaty wysylane przez EUnitAgent'a ----------//

    // AID EUnit'a
    EXECUTION_UNIT_AID,

    // statystyki EUnit'a zapisywane do pliku
    EUNIT_MY_FILE_STATS,

    // oferta dla DistributorAgent'a
    COMMISSION_OFFER,


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
    CONFIRMATION_FROM_DISTRIBUTOR,

    COMMISSION_SEND_AGAIN,
    ST_BEGIN,

    /* ComplexST */

    HOLONS_CALENDAR,
    HOLONS_NEW_CALENDAR,

    WORST_COMMISSION_COST,
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
    VISUALISATION_MEASURE_NAMES,

    DRIVERS_DATA,
    TRAILERS_DATA,
    TRUCKS_DATA,
    GUI_SIMULATION_PARAMS,
    SIMULATION_START,
    SIM_TIME,
    STATS_DATA,
    CONFIGURATION
}
