package sukriti.ngo.mis.ui.administration.interfaces;

import java.util.HashMap;

import sukriti.ngo.mis.ui.administration.data.ValidationError;

public interface FormSubmitHandler {
    public void onSuccess();
    public void onServerError(String message);
    public void onValidationError(HashMap<ValidationError.Companion.FieldNames, ValidationError> errorList);
}
