package sukriti.ngo.mis.dataModel.dynamo_db;

import java.util.ArrayList;
import java.util.List;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIgnore;

@DynamoDBDocument
public class City {
    private String name;
    private String code;
    private int recursive = 0;
    private List<Complex> complexes = new ArrayList<>();

    public City(){

    }

    public City(City city) {
        this.setRecursive(city.recursive);
        this.setName(city.name);
        this.setCode(city.getCode());
        ArrayList<Complex> cloneList = new ArrayList<>();
        for(Complex complex : city.getComplexes())
            cloneList.add(new Complex(complex));
        this.setComplexes(cloneList);
    }

    public City(String name,String code,int recursive, List<Complex> complexes){
        setName(name);
        setCode(code);
        setRecursive(recursive);
        setComplexes(complexes);
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

    public List<Complex> getComplexes() { return complexes; }
    public void setComplexes(List<Complex> complexes) { this.complexes = complexes; }

}
