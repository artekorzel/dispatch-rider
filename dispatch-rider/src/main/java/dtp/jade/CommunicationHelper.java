package dtp.jade;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class CommunicationHelper {

    // ----------------------------------------------------------//
    // ---------- Komunikaty wysylane przez GUIAgent'a ----------//

    // graf sieci transportowej
    public static final int GRAPH = 1011;

    // graf sieci transportowej - aktualizacja
    public static final int GRAPH_UPDATE = 1012;

    // info dotyczace symulacji
    public static final int SIM_INFO = 102;

    // kolejny timestamp
    public static final int TIME_CHANGED = 103;

    // zlecenie transportowe
    public static final int COMMISSION = 104;

    // prosba o kalendarz EUnit'a
    public static final int EUNIT_SHOW_CALENDAR = 105;

    // prosba o statystyki EUnit'a
    public static final int EUNIT_SHOW_STATS = 106;

    // prosba o statystyki EUnit'a do pozniejszego zapisu do pliku
    public static final int EUNIT_SHOW_STATS_TO_WRITE = 113;

    // prosba o reset EUnit'a i DistributorAgent'a
    public static final int RESET = 107;

    // prosba o wyslanie listy niezrealizowachy zlecen
    public static final int DISTRIBUTOR_SHOW_NOONE_LIST = 108;

    // sytuacja kryzysowa
    public static final int CRISIS_EVENT = 109;

    public static final int DRIVER_CREATION = 110;

    public static final int TRUCK_CREATION = 111;

    public static final int TRAILER_CREATION = 112;

    public static final int SIM_END = 114;

    //prosba o zaktualizowanie przez wszystkie holony obecnej lokacji na podstawie timestampu
    public static final int UPDATE_CURRENT_LOCATION = 115;

    //wiadomosc wysylana do wszystkich eunitow z informacja aby wracali do depotu
    public static final int BACK_TO_DEPOT = 116;

    // ------------------------------------------------------------------ //
    // ---------- Komunikaty wysylane przez DistributorAgent'a ---------- //

    // AID DistributorAgent'a
    public static final int DISTRIBUTOR_AID = 201;

    // prosba o utworzenie nowego EUnit'a
    public static final int EXECUTION_UNIT_CREATION = 202;

    public static final int GUI_MESSAGE = 203;

    // prosba o oferte EUnit'a
    public static final int COMMISSION_OFFER_REQUEST = 204;

    // odpowiedz na oferte - info czy EUnit wygral aukcje zlecen
    public static final int FEEDBACK = 205;

    // prosba o najgorsze zlecenie EUnit'a (Simmulated Trading)
    public static final int EUNIT_SEND_WORST_COMMISSION = 206;

    // prosba o info na temat EUnit'a
    public static final int EUNIT_SEND_INFO = 207;

    // liczba zlecen na liscie niezrealizowanych
    public static final int NOONE_LIST = 208;

    // ----------------------------------------------------------- //
    // ---------- Komunikaty wysylane przez EUnitAgent'a ----------//

    // AID EUnit'a
    public static final int EXECUTION_UNIT_AID = 301;

    // kalendarz EUnit'a
    public static final int EUNIT_MY_CALENDAR = 302;

    // statystyki EUnit'a
    public static final int EUNIT_MY_STATS = 303;

    // statystyki EUnit'a zapisywane do pliku
    public static final int EUNIT_MY_FILE_STATS = 307;

    // oferta dla DistributorAgent'a
    public static final int COMMISSION_OFFER = 304;

    // info o EUnitAgent
    public static final int EUNIT_INFO = 306;

    // ostateczny feedback na temat sytuacji kryzysowej
    public static final int CRISIS_EVENT_FINAL_FEEDBACK = 330;

    // ----------------------------------------------------------- //
    // ------ Komunikaty wysylane przez TransportAgent'�w ------//

    // oferta elementu transportowego
    public static final int TRANSPORT_OFFER = 501;

    public static final int TRANSPORT_COMMISSION = 502;

    public static final int TRANSPORT_FEEDBACK = 503;

    public static final int TRANSPORT_DRIVER_AID = 504;

    public static final int TRANSPORT_INITIAL_DATA = 505;

    public static final int TRANSPORT_TRUCK_AID = 506;

    public static final int TRANSPORT_TRAILER_AID = 507;

    public static final int TRANSPORT_REORGANIZE = 508;

    public static final int TRANSPORT_REORGANIZE_OFFER = 509;

    public static final int TRANSPORT_AGENT_CREATED = 800;
    public static final int SIM_INFO_RECEIVED = 801;
    public static final int TIME_STAMP_CONFIRM = 802;

    // ----------------------------------------------------------- //
    // ----------------------------------------------------------- //

    /* WYSYLANE PRZEZ INFO AGENTA */
    public static final int EUNIT_INITIAL_DATA = 601;

    // ----------------------------------------------------------- //
    /* Czesc odpowiedzialna za nowa koncepcje */
    public static final int AGENTS_DATA = 1;
    public static final int AGENTS_DATA_FOR_TRANSPORTUNITS = 2;
    public static final int TRANSPORT_AGENT_CONFIRMATION = 3;
    public static final int TRANSPORT_AGENT_PREPARED_TO_NEGOTIATION = 4;
    public static final int START_NEGOTIATION = 5;
    public static final int TEAM_OFFER = 6;
    public static final int TEAM_OFFER_RESPONSE = 7;
    public static final int NEW_HOLON_OFFER = 8;
    public static final int NEW_HOLON_TEAM = 9;
    public static final int HOLON_FEEDBACK = 10;
    public static final int COMMISSION_FOR_EUNIT = 11;
    public static final int CONFIRMATIO_FROM_DISTRIBUTOR = 12;

    public static final int COMMISSION_SEND_AGAIN = 15;
    public static final int ST_BEGIN = 17;

    /* ComplexST */

    public static final int HOLONS_CALENDAR = 21;
    public static final int HOLONS_NEW_CALENDAR = 22;

    public static final int WORST_COMMISSION_COST = 23;
    public static final int CHANGE_SCHEDULE = 24;

    public static final int SIMULATION_DATA = 25;

    public static final int UNDELIVERIED_COMMISSION = 26;

    public static final int MEASURE_DATA = 27;

    public static final int CONFIGURATION_CHANGE = 28;

    public static final int MLTable = 29;

    public static final int GRAPH_CHANGED = 30;

    public static final int ASK_IF_GRAPH_LINK_CHANGED = 31;

    public static final int GRAPH_LINK_CHANGED = 32;

    public static AID[] findAgentByServiceName(Agent agent, String serviceName) {

        DFAgentDescription[] descriptions = null;
        AID[] aids;

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceName);
        template.addServices(sd);
        SearchConstraints constraints = new SearchConstraints();
        constraints.setMaxResults(1000000L);

        try {
            descriptions = DFService.search(agent, template, constraints);
        } catch (FIPAException fe) {
            fe.printStackTrace();       //FIXME
        }

        aids = new AID[descriptions.length];
        for (int i = 0; i < descriptions.length; i++) {
            aids[i] = descriptions[i].getName();
        }

        return aids;
    }
}
