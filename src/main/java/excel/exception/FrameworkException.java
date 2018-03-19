package excel.exception;

/**
 * @Author 王宇
 * @DATE 2018/3/12.
 * @Description  framework 受检查异常
 */
public class FrameworkException  extends Exception{

    private static final long serialVersionUID = 405082117695663275L;

    public FrameworkException(){
        super();
    }

    public FrameworkException(String message){
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public FrameworkException(Throwable cause) {
        super(cause);
    }


}
