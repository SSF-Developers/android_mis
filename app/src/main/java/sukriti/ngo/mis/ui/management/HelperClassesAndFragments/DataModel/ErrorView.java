package sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel;

public class ErrorView {
    public String Message;
    public int ViewRef;
    public ErrorView(String message, int ViewRef)
    {
        this.Message = message;
        this.ViewRef = ViewRef;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public int getViewRef() {
        return ViewRef;
    }

    public void setViewRef(int viewRef) {
        ViewRef = viewRef;
    }
}
