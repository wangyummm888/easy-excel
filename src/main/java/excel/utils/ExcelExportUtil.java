package excel.utils;

import excel.export.annotation.ExcelField;
import excel.export.annotation.ExcelSheet;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ExcelExportUtil {
    private static Logger logger = LoggerFactory.getLogger(ExcelExportUtil.class);


    /**
     * 导出Excel对象
     *
     * @param dataList Excel数据
     * @return
     */
    public static Workbook exportWorkbook(List<?> dataList) {

        // data
        if (dataList == null || dataList.size() == 0) {
            throw new RuntimeException(">>>>>>>>>>> excel error, data can not be empty.");
        }

        // sheet
        Class<?> sheetClass = dataList.get(0).getClass();
        ExcelSheet excelSheet = sheetClass.getAnnotation(ExcelSheet.class);

        String sheetName = dataList.get(0).getClass().getSimpleName();
        HSSFColor.HSSFColorPredefined headColor = null;
        if (excelSheet != null) {
            if (excelSheet.name() != null && excelSheet.name().trim().length() > 0) {
                sheetName = excelSheet.name().trim();
            }
            headColor = excelSheet.headColor();
        }


        // sheet field
        List<Field> fields = new ArrayList<Field>();
        if (sheetClass.getDeclaredFields() != null && sheetClass.getDeclaredFields().length > 0) {
            for (Field field : sheetClass.getDeclaredFields()) {
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                if (Modifier.isStatic(field.getModifiers()) || excelField == null) {
                    continue;
                }
                fields.add(field);
            }
        }
        //排序
        sortFiled(fields);
        if (fields == null || fields.size() == 0) {
            throw new RuntimeException(">>>>>>>>>>> excel error, data field can not be empty.");
        }

        // 常驻内存是1w条
        Workbook wb = new SXSSFWorkbook(10000);
        Sheet sheet = wb.createSheet(sheetName);

        // sheet header row
        CellStyle headStyle = null;
        if (headColor != null) {
            headStyle = wb.createCellStyle();
            headStyle.setFillForegroundColor(headColor.getIndex());
            headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headStyle.setFillBackgroundColor(headColor.getIndex());
        }

        Row headRow = sheet.createRow(0);
        List<Integer> hiddenColumns = new ArrayList<>();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            ExcelField excelField = field.getAnnotation(ExcelField.class);
            if(excelField.isHidden()){
                hiddenColumns.add(i);
            }
            String fieldName = (excelField != null && excelField.name() != null && excelField.name().trim().length() > 0) ? excelField.name() : field.getName();

            Cell cellX = headRow.createCell(i, CellType.STRING);
            if (headStyle != null) {
                cellX.setCellStyle(headStyle);
            }
            cellX.setCellValue(String.valueOf(fieldName));


        }

        // sheet data rows
        for (int dataIndex = 0; dataIndex < dataList.size(); dataIndex++) {
            int rowIndex = dataIndex + 1;
            Object rowData = dataList.get(dataIndex);
            Row rowX = sheet.createRow(rowIndex);
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);

                PropertyDescriptor pd = null;
                try {
                    pd = new PropertyDescriptor(field.getName(), sheetClass);
                    //获得get方法
                    Method getMethod = pd.getReadMethod();
                    //执行get方法返回一个Object
                    Object fieldValue = getMethod.invoke(rowData);
                    String fieldValueString = FieldReflectionUtil.formatValue(field, fieldValue);

                    field.setAccessible(true);
                    Cell cellX = rowX.createCell(i, CellType.STRING);
                    cellX.setCellValue(fieldValueString);
                } catch (IntrospectionException e) {
                    logger.error(e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage(), e);
                } catch (InvocationTargetException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        //处理隐藏列
        for(Integer i : hiddenColumns){
            sheet.setColumnHidden(i,true);
        }
        return wb;
    }

    /**
     * 导出Excel文件到磁盘
     *
     * @param dataList
     * @param dirPath
     * @param fileName
     */
    public static void exportToFile(List<?> dataList, String dirPath,String fileName) {
        // workbook
        Workbook workbook = exportWorkbook(dataList);

        FileOutputStream fileOutputStream = null;
        try {
            File f=new File(dirPath);
            if(!f.exists()){
                f.mkdirs();
            }
            // workbook 2 FileOutputStream
            fileOutputStream = new FileOutputStream(dirPath+File.separator +fileName);
            workbook.write(fileOutputStream);

            // flush
            fileOutputStream.flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }


    //对List排序
    public static void sortFiled(List<Field> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = i; j < list.size(); j++) {
                ExcelField annotation0 = list.get(i).getAnnotation(ExcelField.class);
                ExcelField annotation1 = list.get(j).getAnnotation(ExcelField.class);
                if (annotation0.index() > annotation1.index()) {
                    Field field0 = list.get(i);
                    Field field1 = list.get(j);
                    list.set(j, field0);
                    list.set(i, field1);
                }
            }
        }
    }


//    public static void main(String[] args) {
//
//        Class<CustomerDataVo> customerDataVoClass = CustomerDataVo.class;
//        ExcelSheet excelSheet = customerDataVoClass.getAnnotation(ExcelSheet.class);
//        // sheet field
//        List<Field> fields = new ArrayList<Field>();
//        if (customerDataVoClass.getDeclaredFields() != null && customerDataVoClass.getDeclaredFields().length > 0) {
//            for (Field field : customerDataVoClass.getDeclaredFields()) {
//                ExcelField excelField = field.getAnnotation(ExcelField.class);
//                if (Modifier.isStatic(field.getModifiers()) || excelField == null) {
//                    continue;
//                }
//                fields.add(field);
//            }
//        }
//        //排序
//        sortFiled(fields);
//
//        for (int i = 0; i < fields.size(); i++) {
//            System.out.println(fields.get(i).getAnnotation(ExcelField.class).name());
//        }
//    }


}
