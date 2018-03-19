package excel.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import excel.export.ExcelExport;
import excel.export.ExportConstant;
import excel.export.annotation.ExcelSheet;
import excel.utils.SpringUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 王宇
 * @DATE 2018/3/12.
 * @Description excel导出
 */
@Controller
@RequestMapping("/excelExport")
public class ExcelExportController {

    private static final Logger log = LoggerFactory.getLogger(ExcelExportController.class);
    /**
     * 通用导出excel
     * 1.需要导出 pojo 使用注解 @ExcelSheet  @ExcelFile
     * 2.
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/doExport")
    public String doProductExport(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("content-type","text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        try {
            long startTime = System.currentTimeMillis();
            Map<String, String> requstPram = getRequstPram(request);
            Map<String, String> stringStringMap = checkEcportParm(requstPram);
            String sheetName = stringStringMap.get("sheetName");
            //文件类型
            String fileType = stringStringMap.get("fileType");
            String serviceRef = requstPram.get("serviceRef");
            log.error("{} 导出操作,parms:{}", serviceRef, JSON.toJSONString(stringStringMap));
            if("sucess".equals(stringStringMap.get("status"))){
                ServletOutputStream outputStream = response.getOutputStream();
                Object bean = SpringUtil.getBean(serviceRef);
                ExcelExport exportService= (ExcelExport)bean;
                String fileName = new String(sheetName.getBytes(), "ISO8859_1");
                //设置附件名称
                response.setHeader("Content-disposition", "attachment; filename=" + fileName + "."+fileType);
                Object parmter = getParmter(requstPram);
                exportService.exportExcel(parmter,outputStream);
                log.error("{}导出操作,parms:{},耗时{}", sheetName, JSON.toJSONString(parmter), (System.currentTimeMillis() - startTime));
            }else if("fail".equals(stringStringMap.get("status"))){
                response.getWriter().print(stringStringMap.get("result"));
            }

        }catch (Exception e) {
            log.error("下载过程出现异常", e);
            try {
                response.getWriter().print("<script>alert('下载过程出现异常！！');history.go(-1);</script>");
            } catch (IOException e1) {
            }
        }
        return null;
    }

    /**
     * @Title: getRequstPram
     * @Description: TODO  获取request参数
     * @param @param request
     * @author px.yang@zuche.com
     * @date 2015-12-13 上午11:57:20
     */
    public static Map<String, String> getRequstPram(HttpServletRequest request){
        // 获得request参数和值
        Map<String, String> map = new HashMap<String, String>();
        Enumeration<?> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                String paramValue = paramValues[0];
                if (paramValue.length() != 0) {
                    map.put(paramName, paramValue);
                }
            }
        }
        return map;
    }






    public Object getParmter(Map<String, String> requstPram){
        String serviceRef = requstPram.get("serviceRef");
        Object bean = SpringUtil.getBean(serviceRef);
        ExcelExport exportService=(ExcelExport) bean;
        Class classActualType = exportService.getQueryType();
        return  map2Bean(requstPram, classActualType);
    }





    /**
     * 校验导出功能
     * @param requstPram
     * @return
     */
    public Map<String,String> checkEcportParm(Map<String, String> requstPram){
        Map<String,String> resultMap= Maps.newHashMap();
        int count4BigDataExport = 0;

        String serviceRef = requstPram.get("serviceRef");
        Object bean = SpringUtil.getBean(serviceRef);
        ExcelExport exportService=null;
        String sheetName = requstPram.get("sheetName");
        if(StringUtils.isBlank(serviceRef)||bean==null||!(bean instanceof ExcelExport)) {
            log.error("导出校验参数错误{},parm{}","serviceRef未设置",JSON.toJSONString(requstPram));
            resultMap.put("status","fail");
            resultMap.put("result","<script>alert('请求参数错误,请联系管理员');history.go(-1);</script>");
            return resultMap;
        }else{
            exportService=(ExcelExport) bean;
        }

        Class classActualType = exportService.getQueryType();

        Class resultType = exportService.getResultType();

        ExcelSheet excelSheet = (ExcelSheet) resultType.getAnnotation(ExcelSheet.class);

        if (excelSheet != null) {
            if (excelSheet.name() != null && excelSheet.name().trim().length() > 0) {
                sheetName = excelSheet.name().trim();
            }
        }
        Object o = map2Bean(requstPram, classActualType);
        count4BigDataExport = exportService.getCount(o);
        if(count4BigDataExport> ExportConstant.MAX_DATA_UPLOAD){
            log.error("导出数量过大,parm{}",JSON.toJSONString(requstPram));
            resultMap.put("status","fail");
            resultMap.put("result", "<script>alert('导出数量不能超过" + ExportConstant.MAX_DATA_UPLOAD + ",请筛选后再进行查询');history.go(-1);</script>");
        } else if (count4BigDataExport == 0) {
            log.error("数据为空,parm{}", JSON.toJSONString(requstPram));
            resultMap.put("status", "fail");
            resultMap.put("result", "<script>alert('导出数据不能为空,请确定筛选条件是否正常');history.go(-1);</script>");
        } else if (count4BigDataExport > ExportConstant.MAX_DATA_WORKBOOK) {
            resultMap.put("fileType","zip");
            resultMap.put("fileName",sheetName);
            resultMap.put("sheetName",sheetName);
            resultMap.put("status", "sucess");
        }else{
            resultMap.put("fileType","xlsx");
            resultMap.put("fileName",sheetName);
            resultMap.put("sheetName",sheetName);
            resultMap.put("status", "sucess");
        }
        return resultMap;
    }





    /**
     * @param map 参数map
     * @param class1 class
     * @param <T> 具体类型
     * @return 具体实体
     */
    public static <T> T map2Bean(Map<String, String> map, Class<T> class1) {
        T bean = null;
        if(class1.isAssignableFrom(Map.class)){
            return (T)map;
        }else {

            try {
                bean = class1.newInstance();
                BeanUtils.populate(bean, map);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return bean;
        }
    }

}
