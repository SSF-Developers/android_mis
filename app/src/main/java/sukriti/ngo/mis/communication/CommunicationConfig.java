package sukriti.ngo.mis.communication;

public class CommunicationConfig {
    public static final int AWS_PING_INTERVAL =  10; //Seconds
    public static final int STABLE_CONNECTION_THRESHOLD = 4; //SECONDS
    public static final int TIMEOUT_THRESHOLD_CONNECT_ACTION = 1 * 35;  //SECONDS
    public static final int TIMEOUT_THRESHOLD_SUBSCRIBE_ACTION = 5 * 60;  //SECONDS
    public static final int RECONNECT_INITIAL_DELAY = 10;  //SECONDS
    public static final int WORKER_PERIODIC_REQUEST_INTERVAL = 15; //MINUTES
    public static final int WORKER_PUBLISH_BACKOFF_DELAY =  5; //MINUTES
    public static final int POOLING_SLEEP =  1000; //Milli-Seconds
    public static final int POOLING_SLEEP_SHORT =  400; //Milli-Seconds
    public static final int POOLING_SLEEP_PUBLISH =  200; //Milli-Seconds
    public static final int MAX_UN_ENQUEUED_DURATION =  15; //Seconds
}
