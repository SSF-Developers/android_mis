package sukriti.ngo.mis.ui.administration.data;

import sukriti.ngo.mis.dataModel.CognitoUser;
import sukriti.ngo.mis.dataModel.dynamo_db.Country;
import sukriti.ngo.mis.dataModel.dynamo_db.Team;

public class MemberDetailsData {
    public Team team;
    public CognitoUser cognitoUser;
    public Country userAccess;
}
