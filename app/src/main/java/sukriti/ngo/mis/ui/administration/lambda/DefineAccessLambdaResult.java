package sukriti.ngo.mis.ui.administration.lambda;

public class DefineAccessLambdaResult {
    public int result;
    public String message;

    public DefineAccessLambdaResult(int result, String message) {
        this.result = result;
        this.message = message;
    }

    public DefineAccessLambdaResult() {
    }

    public int getResult() {
        return result;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public void setResult(int result) {
        this.result = result;
    }

}
