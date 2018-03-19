package excel.export;

import excel.exception.FileUtilException;
import excel.export.annotation.ExcelSheet;
import excel.utils.ExcelExportUtil;
import excel.utils.FileUtil;
import excel.utils.ZipUtil;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractExcelExport<T,R> implements ExcelExport<T,R> {
    private static final Logger log = LoggerFactory.getLogger(AbstractExcelExport.class);

    public static final String CLASS_PATH = AbstractExcelExport.class.getResource("/").getPath();
    public static final String EXPORT_PATH = (CLASS_PATH.indexOf("WEB-INF") > 0 ? CLASS_PATH.substring(0, CLASS_PATH.indexOf("WEB-INF")) : CLASS_PATH) + File.separator + "temp" + File.separator;
    /**
     * 导出export
     * @param t
     * @param outputStream
     */
    @Override
    public final void exportExcel(T t, ServletOutputStream outputStream)  {
        try { //第一步获取数量大小
            int count = this.getCount(t);

            //文件数量
            String tempDir = UUID.randomUUID().toString().replaceAll("-", "");
            String sheetName = "";
            if ((count / ExportConstant.MAX_DATA_WORKBOOK) > 0) {
                for (int i = 0; i < ((count-1) / ExportConstant.MAX_DATA_WORKBOOK) + 1; i++) {
                    //查询次数
                    Integer queryTimes = (i * ExportConstant.MAX_DATA_WORKBOOK) < count ?
                            ((ExportConstant.MAX_DATA_WORKBOOK - 1) / ExportConstant.MAX_DATA_QUERY) + 1 :
                            ((count % ExportConstant.MAX_DATA_WORKBOOK) / ExportConstant.MAX_DATA_QUERY)+1;

                    Integer listSize = count < ExportConstant.MAX_DATA_WORKBOOK ? count : ExportConstant.MAX_DATA_WORKBOOK;
                    List<R> rs = new ArrayList<>(listSize);
                    for (int j = 0; j < queryTimes; j++) {
                        Integer pageSize = ((j + 1) * ExportConstant.MAX_DATA_QUERY) > ExportConstant.MAX_DATA_WORKBOOK ? ((j + 1) * ExportConstant.MAX_DATA_QUERY) - ExportConstant.MAX_DATA_WORKBOOK : ExportConstant.MAX_DATA_QUERY;
                        List<R> rs1 = this.getList(t, (i * ExportConstant.MAX_DATA_WORKBOOK + j * ExportConstant.MAX_DATA_QUERY), pageSize);
                        rs.addAll(rs1);
                    }
                    sheetName = rs.get(0).getClass().getSimpleName();
                    Class<?> sheetClass = rs.get(0).getClass();
                    ExcelSheet excelSheet = sheetClass.getAnnotation(ExcelSheet.class);
                    if (excelSheet != null) {
                        if (excelSheet.name() != null && excelSheet.name().trim().length() > 0) {
                            sheetName = excelSheet.name().trim();
                        }
                    }
                    ExcelExportUtil.exportToFile(rs, EXPORT_PATH + tempDir , sheetName + "_" + i + ".xlsx");
                }
                String zipFilename = EXPORT_PATH  + File.separator + tempDir + ".zip";
                ZipUtil.zipMultiFile(EXPORT_PATH + tempDir + "/", zipFilename, Boolean.FALSE);
                FileInputStream fis=new FileInputStream(new File(zipFilename));
                FileUtil.copyInputStreamToOutputStream(fis,outputStream);
                FileUtils.deleteDirectory(new File(EXPORT_PATH + tempDir));
                FileUtils.forceDelete(new File(zipFilename));
            } else {
                Integer queryTimes = (count / ExportConstant.MAX_DATA_QUERY)+1;
                Integer listSize = count < ExportConstant.MAX_DATA_WORKBOOK ? count : ExportConstant.MAX_DATA_WORKBOOK;
                List<R> rs = new ArrayList<>(listSize);
                for (int j = 0; j < queryTimes; j++) {
                    List<R> rs1 = this.getList(t, j * ExportConstant.MAX_DATA_QUERY, ExportConstant.MAX_DATA_QUERY);
                    rs.addAll(rs1);
                }
                Workbook sheets = ExcelExportUtil.exportWorkbook(rs);
                sheets.write(outputStream);
            }
        }catch (FileNotFoundException e) {
            log.error("导出execl导出文件不存在",e);
            throw new RuntimeException(e.getMessage());
        } catch (FileUtilException e) {
            log.error("导出文件Util异常",e);
            throw new RuntimeException(e.getMessage());
        }
        catch (IOException e) {
            log.error("导出文件Util异常",e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public final Class getQueryType() {
        Class<T> clazz = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        return clazz;
    }

    @Override
    public  final Class getResultType() {
        Class<R> clazz = (Class<R>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[1];
        return clazz;
    }
}
