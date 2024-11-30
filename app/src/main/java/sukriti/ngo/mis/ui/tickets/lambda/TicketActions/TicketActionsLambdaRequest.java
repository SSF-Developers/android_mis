package sukriti.ngo.mis.ui.tickets.lambda.TicketActions;

import sukriti.ngo.mis.repository.entity.Ticket;
import sukriti.ngo.mis.ui.login.data.UserProfile;
import sukriti.ngo.mis.ui.tickets.data.TicketAction;
import sukriti.ngo.mis.ui.tickets.data.TicketDetailsData;

public class TicketActionsLambdaRequest {
    String userId;
    String userRole;
    String ticketId;
    String currentTicketStatus;
    String actionCode;
    String actionName;
    String comment;
    String assigneeId;
    String assigneeRole;

    public TicketActionsLambdaRequest(String userId, String userRole, String ticketId,
                                      String currentTicketStatus, String actionCode, String actionName,
                                      String comment, String assigneeId, String assigneeRole) {
        this.userId = userId;
        this.userRole = userRole;
        this.ticketId = ticketId;
        this.currentTicketStatus = currentTicketStatus;
        this.actionCode = actionCode;
        this.actionName = actionName;
        this.comment = comment;
        this.assigneeId = assigneeId;
        this.assigneeRole = assigneeRole;
    }

    public TicketActionsLambdaRequest() {

    }

    public TicketActionsLambdaRequest(UserProfile user, Ticket ticket, TicketAction action, String comment) {
        this.userId = user.getUser().getUserName();
        this.userRole = user.getRole().name();
        this.ticketId = ticket.getTicket_id();
        this.currentTicketStatus = ticket.getTicket_status();
        this.actionCode = action.getActionId() + "";
        this.actionName = action.getActionName();
        this.comment = comment;
        this.assigneeId = "";
        this.assigneeRole = "";
    }

    public TicketActionsLambdaRequest(UserProfile user, Ticket ticket, TicketAction action,
                                      String comment, String assigneeId,String assigneeRole) {
        this.userId = user.getUser().getUserName();
        this.userRole = user.getRole().name();
        this.ticketId = ticket.getTicket_id();
        this.currentTicketStatus = ticket.getTicket_status();
        this.actionCode = action.getActionId() + "";
        this.actionName = action.getActionName();
        this.comment = comment;
        this.assigneeId = assigneeId;
        this.assigneeRole = assigneeRole;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }


    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCurrentTicketStatus() {
        return currentTicketStatus;
    }

    public void setCurrentTicketStatus(String currentTicketStatus) {
        this.currentTicketStatus = currentTicketStatus;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getAssigneeRole() {
        return assigneeRole;
    }

    public void setAssigneeRole(String assigneeRole) {
        this.assigneeRole = assigneeRole;
    }
}
