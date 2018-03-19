package excel.exception;

/**
 * @Author 王宇
 * @DATE 2018/3/12.
 * @Description
 */
public class FileUtilException extends FrameworkException {



    /**
     *
     */
    private static final long serialVersionUID = -4027447917586489176L;

    public FileUtilException(){
        super();
    }

    public FileUtilException(String message){
        super(message);
    }

    public FileUtilException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileUtilException(Throwable cause) {
        super(cause);
    }


}
