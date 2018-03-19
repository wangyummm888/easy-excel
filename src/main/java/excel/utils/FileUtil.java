package excel.utils;

import excel.exception.FileUtilException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Author 王宇
 * @DATE 2018/3/12.
 * @Description
 */
public class FileUtil {
    private static final int STREAM_BYTE_ARRAY_SIZE = 1024;

    private FileUtil(){ }

    /**
     * 从输入流复制数据到输出流
     * Description:
     * @Version 1.0 2012-9-21 下午01:16:56 by 李洪波（hb.li@zhuche.com）创建
     * @param in
     * @param out
     * @throws com.zuche.framework.exception.FrameworkException
     */
    public static void copyInputStreamToOutputStream(InputStream in, OutputStream out)throws FileUtilException{

        byte[] buffer = new byte[STREAM_BYTE_ARRAY_SIZE];
        if (in != null) {
            try {
                int r ;
                while ((r = in.read(buffer)) != -1) {
                    out.write(buffer, 0, r);
                }
                out.flush();
            } catch (IOException e) {
                throw new FileUtilException(e);
            }
        }
    }

    public static List<String> getAllFile(String filePath, String start, String end)throws FileUtilException{

        File file = new File(filePath);
        if(file.isDirectory()){

            Map<String,List<String>> map = new HashMap<String, List<String>>();
            Map<String,String> mapParameter = new HashMap<String, String>();
            mapParameter.put(start, end);
            fineFile(file.listFiles(new FilenameFilterImpl()),mapParameter,map);

            Iterator<List<String>> ite =  map.values().iterator();
            if(ite.hasNext()){
                return ite.next();
            }

        }else{
            throw new FileUtilException("filePath 不是目录，参数异常!");
        }
        return null;
    }


    public static Map<String,List<String>> getAllFile(String filePath,Map<String,String> mapParameter)throws FileUtilException{

        File file = new File(filePath);
        if(file.isDirectory()){

            Map<String,List<String>> map = new HashMap<String, List<String>>();

            fineFile(file.listFiles(new FilenameFilterImpl()),mapParameter,map);

            return map;

        }else{
            throw new FileUtilException("filePath 不是目录，参数异常!");
        }

    }

    private static void fineFile(File[]filePath,Map<String,String> mapParameter,Map<String,List<String>> map){

        for(int i=0;i<filePath.length;i++){
            File temp = filePath[i];
            if(!temp.isFile()){
                fineFile(temp.listFiles(new FilenameFilterImpl()),mapParameter,map);
            }else{
                addResult(temp.getAbsolutePath(), mapParameter, map);
            }

        }

    }

    private static void addResult(String filePath,Map<String,String> mapParameter,Map<String,List<String>> map){

        Iterator<String>ite = mapParameter.keySet().iterator();
        while(ite.hasNext()){
            String key = ite.next();
            String value = mapParameter.get(key);
            String name = new File(filePath).getName();
            if(name.startsWith(key) && name.endsWith(value)){

                List<String> list = map.get(key+value);

                if(list!= null){
                    list.add(filePath);
                }else{
                    list = new ArrayList<String>();
                    list.add(filePath);
                    map.put(key+value,list);
                }
            }

        }

    }

    static class FilenameFilterImpl implements FilenameFilter {

        @Override
        public boolean accept(File dir, String name) {

            return true;
        }

    }

    /**
     * 检测文件夹是否存在,如果是不存在则创建一个
     *
     * @param dirPath
     * @since 1.0.1
     * @version 1.0.1
     */
    public static void checkDir(String dirPath) {
        File directory = new File(dirPath);
        if (!directory.isDirectory()) {
            directory.mkdir();
        }
    }
    /**
     * 检测文件夹是否存在,如果是不存在则创建一个
     *
     * @param dirPath
     * @since 1.0.1
     * @version 1.0.1
     */
    public static void checkDir(String[] dirPath) {
        String tempPath = "";
        for (int i = 0; i < dirPath.length; i++) {
            tempPath = tempPath + "/" + dirPath[i];
            File directory = new File(tempPath);
            if (!directory.isDirectory()) {
                directory.mkdir();
            }
        }
    }



}
