package excel.export;

import javax.servlet.ServletOutputStream;
import java.util.List;

public interface ExcelExport<T, R> {
    int getCount(T t);

    List<R> getList(T t, Integer index, Integer pageSize);

    void exportExcel(T t, ServletOutputStream outputStream);

    /**
     * 查询参数类型实例化
     *
     * @return
     */
    Class getQueryType();

    /**
     * 返回结果类型实例化
     *
     * @return
     */
    Class getResultType();

}
