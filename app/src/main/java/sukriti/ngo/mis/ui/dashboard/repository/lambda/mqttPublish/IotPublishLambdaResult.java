package sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish;

public class IotPublishLambdaResult {
    public int result;
    public String message;
    public String fileName;
    public String error;


    public IotPublishLambdaResult() {
    }

    public int getResult() {
        return result;
    }
    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }

}
