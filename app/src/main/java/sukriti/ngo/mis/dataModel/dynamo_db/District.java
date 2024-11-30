package sukriti.ngo.mis.dataModel.dynamo_db;

import java.util.ArrayList;
import java.util.List;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIgnore;

@DynamoDBDocument
public class District {
    private String name;
    private String code;
    private int recursive = 0;
    private List<City> cities = new ArrayList<>();

    public District(){

    }

    public District(District district) {
        this.setRecursive(district.recursive);
        this.setName(district.getName());
        this.setCode(district.getCode());
        ArrayList<City> cloneList = new ArrayList<>();
        for(City city: district.getCities()){
            cloneList.add(new City(city));
        }
        this.setCities(cloneList);
    }

    public District(String name,String code,int recursive, List<City> cities){
        setName(name);
        setCode(code);
        setRecursive(recursive);
        setCities(cities);
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getRecursive() { return recursive; }
    public void setRecursive(int recursive) { this.recursive = recursive; }

    public List<City> getCities() { return cities; }
    public void setCities(List<City> cities) { this.cities = cities; }

}
