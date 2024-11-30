package sukriti.ngo.mis.ui.login.data;

public class LoginLambdaResult {
    public int result;
    public String message;

    public LoginLambdaResult(int result,String message) {
        this.result = result;
        this.message = message;
    }

    public LoginLambdaResult() {
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
