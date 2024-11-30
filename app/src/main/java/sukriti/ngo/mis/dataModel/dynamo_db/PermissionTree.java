package sukriti.ngo.mis.dataModel.dynamo_db;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;

@DynamoDBDocument
public class PermissionTree {
    private Country country;

    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }
}
