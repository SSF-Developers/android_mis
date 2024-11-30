package sukriti.ngo.mis.communication;

public interface AWSPublishHandler {
    public void onSuccess(Object object);
    public void onError(Object object, String ErrorMsg);
}
