package excel.export.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author 王宇
 * @DATE 2017/12/4.
 * @Description
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelField {
    /**
     * 列名称
     *
     * @return
     */
    String name() default "";

    /**
     * 列在 表格的顺序
     *
     * @return
     */
    int index() default Integer.MAX_VALUE;

    /**
     * 时间格式化，日期类型时生效
     *
     * @return
     */
    String dateformat() default "yyyy-MM-dd HH:mm:ss";

    /**
     * 是否隐藏
     * @return
     */
    boolean isHidden() default false;


}
