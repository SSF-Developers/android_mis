package sukriti.ngo.mis;

import com.amazonaws.regions.Regions;

public class AWSConfig {
    public static final Regions cognitoRegion = Regions.AP_SOUTH_1;
    public static final String userPoolId = "ap-south-1_27sC0r4IV"; //"ap-south-1_iUKSq1xcl";//
    public static final String clientId = "6uioe49pe636tdv92bosu6gbs4"; //"htqnps40fjkda3viunkbbnkph";//
    public static final String clientSecret = "1q759c4qc702i8mmbcbql9jk856ord1qctv1h1ke3ag8p2jn7021"; //null;//
    public static final String providerName = "cognito-idp.ap-south-1.amazonaws.com/ap-south-1_27sC0r4IV";
    public static final String identityPoolID = "ap-south-1:ffdd35f3-1674-43da-bdb5-c53fc669e0ec";
    public static final String endPointMumbai = "https://iot.ap-south-1.amazonaws.com";
    public static final String awsIotEndPoint = "a372gqxqbver9r-ats.iot.ap-south-1.amazonaws.com";
    public static final String THING_GROUP_ROOT = "INDIA";
    public static final String THING_GROUP_CLIENT_ROOT = "ClientGroup";

    public static final String POLICY_CABIN = "my-toilets-iot-device-policy";

    public static final String _ATTRIBUTE_NAME = "NAME";
    public static final String _ATTRIBUTE_CODE = "CODE";
    public static final String _ATTRIBUTE_COMPLEX_CITY_CODE = "CITY_CODE";
    public static final String _ATTRIBUTE_COMPLEX_UUID = "UUID";
    public static final String _ATTRIBUTE_COMPLEX_COCO = "COCO";
    public static final String _ATTRIBUTE_COMPLEX_LATT = "LATT";
    public static final String _ATTRIBUTE_COMPLEX_LONG = "LONG";
    public static final String _ATTRIBUTE_COMPLEX_ADDR  = "ADDR";
    public static final String _ATTRIBUTE_COMPLEX_CLNT = "CLNT";

    public static final int LAMBDA_TIMEOUT = 1 * 60 * 1000;
    public static final String lambdaAggregationBucketName = "mis-lambda-aggregation";
    public static final String misTicketFilesBucketName = "mis-ticket-files";
}
