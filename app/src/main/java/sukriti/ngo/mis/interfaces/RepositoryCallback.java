package sukriti.ngo.mis.interfaces;

import sukriti.ngo.mis.dataModel.ValidationResult;
import sukriti.ngo.mis.dataModel._Result;

public interface RepositoryCallback<T> {
    void onComplete(_Result<T> result);
}
