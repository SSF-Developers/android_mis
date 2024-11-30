package sukriti.ngo.mis.ui.administration.lambda.DeleteUser;

public class DeleteUserLambdaResult {
    public int result;
    public String message;

    public DeleteUserLambdaResult() {
    }

    public DeleteUserLambdaResult(int result, String message) {
        this.result = result;
        this.message = message;
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

}
