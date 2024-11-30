package sukriti.ngo.mis.dataModel.dynamo_db;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;

@DynamoDBDocument
public class Country {
    private String name;
    private int recursive = 0;
    private List<State> states= new ArrayList<>();

    public Country() {

    }

    public Country(Country country) {
        this.setRecursive(country.recursive);
        this.setName(country.getName());
        ArrayList<State> cloneList = new ArrayList<>();
        for(State state: country.getStates()){
            cloneList.add(new State(state));
        }
        this.setStates(cloneList);
    }

    public int getRecursive() { return recursive; }
    public void setRecursive(int recursive) {
        this.recursive = recursive;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<State> getStates() { return states; }
    public void setStates(List<State> states) { this.states = states; }

}

