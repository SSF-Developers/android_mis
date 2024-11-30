package sukriti.ngo.mis.dataModel;

// https://docs.oracle.com/javase/tutorial/java/generics/types.html
//    The most commonly used type parameter names are:
//        E - Element (used extensively by the Java Collections Framework)
//        K - Key
//        N - Number
//        T - Type
//        V - Value
//        S,U,V etc. - 2nd, 3rd, 4th types


public abstract class _Result<T> {
    private _Result() {}

    public static final class Success<T> extends _Result<T> {
        public T data;
        public Success(T data) {
            this.data = data;
        }
    }

    public static final class Error<T> extends _Result<T> {
        public Exception exception;
        public String message;
        public Integer errorCode = -1;

        public Error(String message, Exception exception) {
            this.message = message;
            this.exception = exception;
        }

        public Error(Integer errorCode, String message, Exception exception) {
            this.errorCode = errorCode;
            this.message = message;
            this.exception = exception;
        }

        public Error(Integer errorCode, String message) {
            this.errorCode = errorCode;
            this.message = message;
        }
    }

}
