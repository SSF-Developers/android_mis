package sukriti.ngo.mis.ui.tickets.interfaces;

import sukriti.ngo.mis.ui.tickets.data.TicketAction;

public interface TicketActionDialogHandler {
    void onSubmit(TicketAction action, String comment);
}
