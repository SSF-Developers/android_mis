package sukriti.ngo.mis.dataModel.dynamo_db;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIgnore;

@DynamoDBDocument
public class Complex {
    private String name;
    private String address;
    private String uuid;
    private String coco;
    private String lat;
    private String lon;
    private String client;
    @DynamoDBIgnore
    private int isSelected;

    public Complex(){
        Log.i("__ClientRequest","construcor 0");
    }

    public Complex(Complex complex) {
        this.setName(complex.name);
        this.setAddress(complex.address);
        this.setUuid(complex.uuid);
        this.setCoco(complex.coco);
        this.setLat(complex.lat);
        this.setLon(complex.lon);
        this.setClient(complex.client);
        Log.i("__ClientRequest","construcor 1");
    }

    public Complex(String name,String address){
        Log.i("__ClientRequest","construcor 2");
        setName(name);
        setAddress(address);
    }

    public Complex(String name,String address,String uuid, String coco,String lat, String lon){
        Log.i("__ClientRequest","construcor 3");
        setName(name);
        setAddress(address);
        setUuid(uuid);
        setCoco(coco);
        setLat(lat);
        setLon(lon);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getCoco() { return coco; }
    public void setCoco(String coco) { this.coco = coco; }

    public String getLat() { return lat; }
    public void setLat(String lat) { this.lat = lat; }

    public String getLon() { return lon; }
    public void setLon(String lon) { this.lon = lon; }

    public String getClient() { return client; }
    public void setClient(String client) { this.client = client; }

    @DynamoDBIgnore
    public int getIsSelected() { return isSelected; }
    @DynamoDBIgnore
    public void setIsSelected(int isSelected) { this.isSelected = isSelected; }


}














