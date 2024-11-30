package sukriti.ngo.mis.ui.administration.interfaces;

import sukriti.ngo.mis.dataModel.dynamo_db.UserAccess;
import sukriti.ngo.mis.ui.administration.data.TreeEdge;

public interface TreeInteractionListener {
    public void onSelectionChange(int type, TreeEdge treeEdge, int selection);
}
