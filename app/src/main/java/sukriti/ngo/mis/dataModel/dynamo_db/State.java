package sukriti.ngo.mis.dataModel.dynamo_db;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIgnore;

@DynamoDBDocument
public class State {

    private int recursive = 0;
    private String name;
    private String code;
    private List<District> districts = new ArrayList<>();
    public State() {

    }

    public State(State state) {
        this.setRecursive(state.recursive);
        this.setName(state.getName());
        this.setCode(state.getCode());
        ArrayList<District> cloneList = new ArrayList<>();
        for(District district: state.getDistricts()){
            cloneList.add(new District(district));
        }
        this.setDistricts(cloneList);
    }

    public State(String name, String code, int recursive, List<District> districts) {
        setName(name);
        setCode(code);
        setRecursive(recursive);
        setDistricts(districts);
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRecursive() {
        return recursive;
    }

    public void setRecursive(int recursive) {
        this.recursive = recursive;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }

}
