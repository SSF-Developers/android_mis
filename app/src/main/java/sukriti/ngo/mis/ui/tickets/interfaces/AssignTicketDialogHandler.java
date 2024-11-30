package sukriti.ngo.mis.ui.tickets.interfaces;

import sukriti.ngo.mis.ui.tickets.data.TicketAction;

public interface AssignTicketDialogHandler {
    void onSubmit(TicketAction action, String comment, String userId, String userRole);
}
