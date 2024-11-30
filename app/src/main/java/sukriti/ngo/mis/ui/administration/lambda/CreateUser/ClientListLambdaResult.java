package sukriti.ngo.mis.ui.administration.lambda.CreateUser;

import com.google.android.gms.common.api.Api;

import java.util.ArrayList;
import java.util.List;

public class ClientListLambdaResult {

    int status;
    List<ClientList> clientList;

    public ClientListLambdaResult(int status, List<ClientList> list) {
        this.status = status;
        this.clientList = list;
    }

    public List<ClientList> getList() {
        return clientList;
    }

    public void setList(List<ClientList> list) {
        this.clientList = list;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
