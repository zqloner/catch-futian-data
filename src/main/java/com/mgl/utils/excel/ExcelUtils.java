package com.mgl.utils.excel;

import com.mgl.bean.carshop.MglCarshopFutianDataDetail;
import com.mgl.utils.excel.annotation.ExcelColumn;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by zhaohy on 2019/9/2.
 */
public class ExcelUtils {
    private final static Logger log = LoggerFactory.getLogger(ExcelUtils.class);

    private final static String EXCEL2003 = "xls";
    private final static String EXCEL2007 = "xlsx";

    public static <T> List<T> readExcel(String path, Class<T> cls, MultipartFile file, Integer myRow){

        String fileName = file.getOriginalFilename();
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            log.error("上传文件格式不正确");
        }
        List<T> dataList = new ArrayList<>();
        Workbook workbook = null;
        try {
            InputStream is = file.getInputStream();
            if (fileName.endsWith(EXCEL2007)) {
//                FileInputStream is = new FileInputStream(new File(path));
                workbook = new XSSFWorkbook(is);
            }
            if (fileName.endsWith(EXCEL2003)) {
//                FileInputStream is = new FileInputStream(new File(path));
                workbook = new HSSFWorkbook(is);
            }
            if (workbook != null) {
                //类映射  注解 value-->bean columns
                Map<String, List<Field>> classMap = new HashMap<>();
                List<Field> fields = Stream.of(cls.getDeclaredFields()).collect(Collectors.toList());
                fields.forEach(
                        field -> {
                            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                            if (annotation != null) {
                                String value = annotation.value();
                                if (StringUtils.isBlank(value)) {
                                    return;//return起到的作用和continue是相同的 语法
                                }
                                if (!classMap.containsKey(value)) {
                                    classMap.put(value, new ArrayList<>());
                                }
                                field.setAccessible(true);
                                classMap.get(value).add(field);
                            }
                        }
                );
                //索引-->columns
                Map<Integer, List<Field>> reflectionMap = new HashMap<>(16);
                //默认读取第一个sheet
                Sheet sheet = workbook.getSheetAt(0);

                boolean firstRow = true;
                for (int i = myRow; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    //首行  提取注解
                    if (firstRow) {
                        for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                            Cell cell = row.getCell(j);
                            String cellValue = getCellValue(cell);
                            if (classMap.containsKey(cellValue)) {
                                reflectionMap.put(j, classMap.get(cellValue));
                            }
                        }
                        firstRow = false;
                    } else {
                        //忽略空白行
                        if (row == null) {
                            continue;
                        }
                        try {
                            T t = cls.newInstance();
                            //判断是否为空白行
                            boolean allBlank = true;
                            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                                if (reflectionMap.containsKey(j)) {
                                    Cell cell = row.getCell(j);
                                    String cellValue = getCellValue(cell);
                                    if (StringUtils.isNotBlank(cellValue)) {
                                        allBlank = false;
                                    }
                                    List<Field> fieldList = reflectionMap.get(j);
                                    fieldList.forEach(
                                            x -> {
                                                try {
                                                    handleField(t, cellValue, x);
                                                } catch (Exception e) {
                                                    log.error(String.format("reflect field:%s value:%s exception!", x.getName(), cellValue), e);
                                                }
                                            }
                                    );
                                }
                            }
                            if (!allBlank) {
                                dataList.add(t);
                            } else {
                                log.warn(String.format("row:%s is blank ignore!", i));
                            }
                        } catch (Exception e) {
                            log.error(String.format("parse row:%s exception!", i), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(String.format("parse excel exception!"), e);
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    log.error(String.format("parse excel exception!"), e);
                }
            }
        }
        return dataList;
    }

    private static <T> void handleField(T t, String value, Field field) throws Exception {
        Class<?> type = field.getType();
        if (type == null || type == void.class || StringUtils.isBlank(value)) {
            return;
        }
        if (type == Object.class) {
            field.set(t, value);
            //数字类型
        } else if (type.getSuperclass() == null || type.getSuperclass() == Number.class) {
            if (type == int.class || type == Integer.class) {
                field.set(t, NumberUtils.toInt(value));
            } else if (type == long.class || type == Long.class) {
                field.set(t, NumberUtils.toLong(value));
            } else if (type == byte.class || type == Byte.class) {
                field.set(t, NumberUtils.toByte(value));
            } else if (type == short.class || type == Short.class) {
                field.set(t, NumberUtils.toShort(value));
            } else if (type == double.class || type == Double.class) {
                field.set(t, NumberUtils.toDouble(value));
            } else if (type == float.class || type == Float.class) {
                field.set(t, NumberUtils.toFloat(value));
            } else if (type == char.class || type == Character.class) {
                field.set(t, CharUtils.toChar(value));
            } else if (type == boolean.class) {
                field.set(t, BooleanUtils.toBoolean(value));
            } else if (type == BigDecimal.class) {
                field.set(t, new BigDecimal(value));
            }
        } else if (type == Boolean.class) {
            field.set(t, BooleanUtils.toBoolean(value));
        } else if (type == Date.class) {
            //
            field.set(t, value);
        } else if(type == LocalDate.class){
            Date time =new Date(value);
            LocalDate localDate = Instant.ofEpochMilli(time.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            field.set(t,localDate);
        } else if(type == LocalDateTime.class){
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            Date time = sdf.parse(value);
            LocalDateTime localDateTime = Instant.ofEpochMilli(time.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
            field.set(t,localDateTime);
        } else if (type == String.class) {
            field.set(t, value);
        } else {
            Constructor<?> constructor = type.getConstructor(String.class);
            field.set(t, constructor.newInstance(value));
        }
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return HSSFDateUtil.getJavaDate(cell.getNumericCellValue()).toString();
            } else {
                return new BigDecimal(cell.getNumericCellValue()).toString();
            }
        } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            return StringUtils.trimToEmpty(cell.getStringCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            return StringUtils.trimToEmpty(cell.getCellFormula());
        } else if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
            return "";
        } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_ERROR) {
            return "ERROR";
        } else {
            return cell.toString().trim();
        }

    }

    /**
     * 浏览器下载excel
     * @param fileName
     * @param wb
     * @param response
     */

    private static  void  buildExcelDocument(String fileName, Workbook wb, HttpServletResponse response){
        try {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName, "utf-8"));
            response.flushBuffer();
            wb.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成excel
     * @param response
     * @param dataList
     * @param fileName
     * @param <T>
     */
    public static <T> void createExcel(HttpServletResponse response, List<T> dataList, Class<T> clazz, String fileName) {
        SXSSFWorkbook workbook = null;
        // 得到所有定义字段
        Field[] allFields = clazz.getDeclaredFields();
        List<Field> fields = Arrays.stream(allFields)
                .filter(field -> {
                    ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                    if (annotation != null && annotation.col() > 0) {
                        field.setAccessible(true);
                        return true;
                    }
                    return false;
                }).sorted(Comparator.comparing(field -> {
                    int col = 0;
                    ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                    if (annotation != null) {
                        col = annotation.col();
                    }
                    return col;
                })).collect(Collectors.toList());
        // 产生工作薄对象
        workbook = new SXSSFWorkbook();
        // excel2003中每个sheet中最多有65536行
//        int sheetSize = 65536;
        int sheetSize = 200000;
        // 取出一共有多少个sheet.
        double sheetNo = Math.ceil(dataList.size() / sheetSize);
        for (int index =0;index<=sheetNo;index++) {
            // 产生工作表对象
            Sheet sheet = workbook.createSheet();
            Row row;
            // 产生单元格
            Cell cell;
            // 产生一行
            row = sheet.createRow(0);
            for (int i = 0; i < fields.size(); i++){
                Field field = fields.get(i);
                ExcelColumn attr = field.getAnnotation(ExcelColumn.class);
                // 创建列
                cell = row.createCell(i);
                // 设置列中写入内容为String类型
                cell.setCellType(Cell.CELL_TYPE_STRING);
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
                cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                if (attr.value().indexOf("注：") >= 0)
                {
                    Font font = workbook.createFont();
                    font.setColor(HSSFFont.COLOR_RED);
                    cellStyle.setFont(font);
                    cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
                    sheet.setColumnWidth(i, 6000);
                }
                else
                {
                    Font font = workbook.createFont();
                    // 粗体显示
                    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                    // 选择需要用到的字体格式
                    cellStyle.setFont(font);
                    cellStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
                    // 设置列宽
                    sheet.setColumnWidth(i, (int) ((attr.width() + 0.72) * 256));
                    row.setHeight((short) (attr.height() * 20));
                }
                // 设置列宽
                sheet.setColumnWidth(i, (int) ((attr.width() + 0.72) * 256));
                row.setHeight((short) (attr.height() * 20));
                cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
                cellStyle.setWrapText(true);
                cell.setCellStyle(cellStyle);
                // 写入列名
                cell.setCellValue(attr.value());
                // 如果设置了提示信息则鼠标放上去提示.
                if (StringUtils.isNotEmpty(attr.prompt())){
                    // 这里默认设了2-101列提示.
                    setHSSFPrompt(sheet, "", attr.prompt(), 1, sheetSize, i, i);
                }
                // 如果设置了combo属性则本列只能选择不能输入
                if (attr.combo().length > 0){
                    // 这里默认设了2-101列只能选择不能输入.
                    setHSSFValidation(sheet, attr.combo(), 1, sheetSize, i, i);
                }
            }
            int startNo = index * sheetSize;
            int endNo = Math.min(startNo + sheetSize, dataList.size());
            // 写入各条记录,每条记录对应excel表中的一行
            CellStyle cs = workbook.createCellStyle();
            cs.setAlignment(CellStyle.ALIGN_CENTER);
            cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            for (int i = startNo; i < endNo; i++)
            {
                row = sheet.createRow(i + 1 - startNo);
                // 得到导出对象.
                T vo = dataList.get(i);
                for (int j = 0; j < fields.size(); j++)
                {
                    // 获得field.
                    Field field = fields.get(j);
                    // 设置实体类私有属性可访问
                    field.setAccessible(true);
                    ExcelColumn attr = field.getAnnotation(ExcelColumn.class);
                    try
                    {
                        // 设置行高
                        row.setHeight((short) (attr.height() * 20));
                        // 根据Excel中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
                        if (attr.isExport())
                        {
                            // 创建cell
                            cell = row.createCell(j);
                            cell.setCellStyle(cs);
                            if (vo == null)
                            {
                                // 如果数据存在就填入,不存在填入空格.
                                cell.setCellValue("");
                                continue;
                            }

                            String dateFormat = attr.dateFormat();
                            String readConverterExp = attr.readConverterExp();
                            if (StringUtils.isNotEmpty(dateFormat))
                            {
                                cell.setCellValue(DateUtils.localDateTimeToString((LocalDateTime) field.get(vo),dateFormat));
                            }
                            else if (StringUtils.isNotEmpty(readConverterExp))
                            {
                                cell.setCellValue(convertByExp(String.valueOf(field.get(vo)), readConverterExp));
                            }
                            else
                            {
                                cell.setCellType(Cell.CELL_TYPE_STRING);
                                // 如果数据存在就填入,不存在填入空格.
                                cell.setCellValue(field.get(vo) == null ? attr.defaultValue() : field.get(vo) + attr.suffix());
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        log.error("导出Excel失败{}", e.getMessage());
                    }
                }
            }
        }
        buildExcelDocument(fileName,workbook,response);
    }

    /**
     * MGL-抓取福田数据-为了满足一个汽车一个sheet
     * @param response
     * @param dataList
     * @param fileName
     * @param <T>
     */
    public static <T> void createExcelByCarVin(HttpServletResponse response, List<List<T>> datas, Class<T> clazz, String fileName) {
        SXSSFWorkbook workbook = null;
        // 得到所有定义字段
        Field[] allFields = clazz.getDeclaredFields();
        List<Field> fields = Arrays.stream(allFields)
                .filter(field -> {
                    ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                    if (annotation != null && annotation.col() > 0) {
                        field.setAccessible(true);
                        return true;
                    }
                    return false;
                }).sorted(Comparator.comparing(field -> {
                    int col = 0;
                    ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                    if (annotation != null) {
                        col = annotation.col();
                    }
                    return col;
                })).collect(Collectors.toList());
        // 产生工作薄对象
        workbook = new SXSSFWorkbook();
        // excel2003中每个sheet中最多有65536行
//        int sheetSize = 65536;
        // 取出一共有多少个sheet.
        for (int index =0;index<datas.size();index++) {
            int sheetSize = datas.get(index).size();
            // 产生工作表对象
            Sheet sheet = workbook.createSheet();
            Row row;
            // 产生单元格
            Cell cell;
            // 产生一行
            row = sheet.createRow(0);
            for (int i = 0; i < fields.size(); i++){
                Field field = fields.get(i);
                ExcelColumn attr = field.getAnnotation(ExcelColumn.class);
                // 创建列
                cell = row.createCell(i);
                // 设置列中写入内容为String类型
                cell.setCellType(Cell.CELL_TYPE_STRING);
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
                cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
                if (attr.value().indexOf("注：") >= 0)
                {
                    Font font = workbook.createFont();
                    font.setColor(HSSFFont.COLOR_RED);
                    cellStyle.setFont(font);
                    cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
                    sheet.setColumnWidth(i, 6000);
                }
                else
                {
                    Font font = workbook.createFont();
                    // 粗体显示
                    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                    // 选择需要用到的字体格式
                    cellStyle.setFont(font);
                    cellStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
                    // 设置列宽
                    sheet.setColumnWidth(i, (int) ((attr.width() + 0.72) * 256));
                    row.setHeight((short) (attr.height() * 20));
                }
                // 设置列宽
                sheet.setColumnWidth(i, (int) ((attr.width() + 0.72) * 256));
                row.setHeight((short) (attr.height() * 20));
                cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
                cellStyle.setWrapText(true);
                cell.setCellStyle(cellStyle);
                // 写入列名
                cell.setCellValue(attr.value());
                // 如果设置了提示信息则鼠标放上去提示.
                if (StringUtils.isNotEmpty(attr.prompt())){
                    // 这里默认设了2-101列提示.
                    setHSSFPrompt(sheet, "", attr.prompt(), 1, sheetSize, i, i);
                }
                // 如果设置了combo属性则本列只能选择不能输入
                if (attr.combo().length > 0){
                    // 这里默认设了2-101列只能选择不能输入.
                    setHSSFValidation(sheet, attr.combo(), 1, sheetSize, i, i);
                }
            }
            int startNo = 0;
            int endNo = datas.get(index).size();
            // 写入各条记录,每条记录对应excel表中的一行
            CellStyle cs = workbook.createCellStyle();
            cs.setAlignment(CellStyle.ALIGN_CENTER);
            cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            for (int i = startNo; i < endNo; i++)
            {
                row = sheet.createRow(i + 1 - startNo);
                // 得到导出对象.
                T vo = datas.get(index).get(i);
                for (int j = 0; j < fields.size(); j++)
                {
                    // 获得field.
                    Field field = fields.get(j);
                    // 设置实体类私有属性可访问
                    field.setAccessible(true);
                    ExcelColumn attr = field.getAnnotation(ExcelColumn.class);
                    try
                    {
                        // 设置行高
                        row.setHeight((short) (attr.height() * 20));
                        // 根据Excel中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
                        if (attr.isExport())
                        {
                            // 创建cell
                            cell = row.createCell(j);
                            cell.setCellStyle(cs);
                            if (vo == null)
                            {
                                // 如果数据存在就填入,不存在填入空格.
                                cell.setCellValue("");
                                continue;
                            }

                            String dateFormat = attr.dateFormat();
                            String readConverterExp = attr.readConverterExp();
                            if (StringUtils.isNotEmpty(dateFormat))
                            {
                                cell.setCellValue(DateUtils.localDateTimeToString((LocalDateTime) field.get(vo),dateFormat));
                            }
                            else if (StringUtils.isNotEmpty(readConverterExp))
                            {
                                cell.setCellValue(convertByExp(String.valueOf(field.get(vo)), readConverterExp));
                            }
                            else
                            {
                                cell.setCellType(Cell.CELL_TYPE_STRING);
                                // 如果数据存在就填入,不存在填入空格.
                                cell.setCellValue(field.get(vo) == null ? attr.defaultValue() : field.get(vo) + attr.suffix());
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        log.error("导出Excel失败{}", e.getMessage());
                    }
                }
            }
        }
        buildExcelDocument(fileName,workbook,response);
    }


    /**
     * 设置单元格上提示
     *
     * @param sheet 要设置的sheet.
     * @param promptTitle 标题
     * @param promptContent 内容
     * @param firstRow 开始行
     * @param endRow 结束行
     * @param firstCol 开始列
     * @param endCol 结束列
     * @return 设置好的sheet.
     */
    public static Sheet setHSSFPrompt(Sheet sheet, String promptTitle, String promptContent, int firstRow,
                                      int endRow, int firstCol, int endCol){
        // 构造constraint对象
        DVConstraint constraint = DVConstraint.createCustomFormulaConstraint("DD1");
        // 四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        // 数据有效性对象
        HSSFDataValidation dataValidationView = new HSSFDataValidation(regions, constraint);
        dataValidationView.createPromptBox(promptTitle, promptContent);
        sheet.addValidationData(dataValidationView);
        return sheet;
    }

    /**
     * 设置某些列的值只能输入预制的数据,显示下拉框.
     *
     * @param sheet 要设置的sheet.
     * @param textlist 下拉框显示的内容
     * @param firstRow 开始行
     * @param endRow 结束行
     * @param firstCol 开始列
     * @param endCol 结束列
     * @return 设置好的sheet.
     */
    public static Sheet setHSSFValidation(Sheet sheet, String[] textlist, int firstRow, int endRow,
                                          int firstCol, int endCol){
        // 加载下拉列表内容
        DVConstraint constraint = DVConstraint.createExplicitListConstraint(textlist);
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        // 数据有效性对象
        HSSFDataValidation dataValidationList = new HSSFDataValidation(regions, constraint);
        sheet.addValidationData(dataValidationList);
        return sheet;
    }

    /**
     * 解析导出值 0=女,1=男,2=未知
     *
     * @param propertyValue 参数值
     * @param converterExp 翻译注解
     * @return 解析后值
     * @throws Exception
     */
    public static String convertByExp(String propertyValue, String converterExp) throws Exception
    {
        try
        {
            String[] convertSource = converterExp.split(",");
            for (String item : convertSource)
            {
                String[] itemArray = item.split("=");
                if (itemArray[0].equals(propertyValue))
                {
                    return itemArray[1];
                }
            }
        }
        catch (Exception e)
        {
            throw e;
        }
        return propertyValue;
    }

    public static void createFormalLineTemplate(HttpServletResponse response, List<String> listNoPageEnterprises, List<String> listNoPageLines, String fileName) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        Row row =  sheet.createRow(0);
        row.setHeight((short)(12*20*5));
        Cell cell = row.createCell(0);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT); // 创建一个居左格式
        cellStyle.setAlignment(HSSFCellStyle.VERTICAL_CENTER);
//        cellStyle.setWrapText(true); // 自动换行
        cell.setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,7));
        cell.setCellValue("1.线路名称为系统中已经添加的线路，下拉可选择想要分配的路线\r\n" +
                        "2.单位名称为后天添加的所有企业，下拉可选择想要分配的企业\r\n" +
                        "3.疗养日期填报格式为 XXXX-XX-XX 例如2019-01-01\r\n" +
                        "4.报名时间填报格式为 XXXX-XX-XX XXXX-XX-XX 例如2019-01-01 08:00:00");
        Row row2 = sheet.createRow(1);

        CellStyle cellStyle2 = workbook.createCellStyle();
        cellStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        Font font = workbook.createFont();
        // 粗体显示
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 选择需要用到的字体格式
        cellStyle2.setFont(font);
        cellStyle2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);

        Cell cell1 = row2.createCell(0);
        cell1.setCellValue("线路名称");
        cell1.setCellStyle(cellStyle2);
        sheet.setColumnWidth(0, 6000);


        Cell cell2 = row2.createCell(1);
        cell2.setCellValue("疗养开始时间");
        cell2.setCellStyle(cellStyle2);
        sheet.setColumnWidth(1, 4000);

        Cell cell3 = row2.createCell(2);
        cell3.setCellValue("疗养结束时间");
        cell3.setCellStyle(cellStyle2);
        sheet.setColumnWidth(2, 4000);

        Cell cell4 = row2.createCell(3);
        cell4.setCellValue("报名单位");
        cell4.setCellStyle(cellStyle2);
        sheet.setColumnWidth(3, 8000);

        Cell cell5 = row2.createCell(4);
        cell5.setCellValue("限报人数");
        cell5.setCellStyle(cellStyle2);
        sheet.setColumnWidth(4, 4000);

        Cell cell6 = row2.createCell(5);
        cell6.setCellValue("报名开始时间");
        cell6.setCellStyle(cellStyle2);
        sheet.setColumnWidth(5, 6000);

        Cell cell7 = row2.createCell(6);
        cell7.setCellValue("报名结束时间");
        cell7.setCellStyle(cellStyle2);
        sheet.setColumnWidth(6, 6000);

        Cell cell8 = row2.createCell(7);
        cell8.setCellValue("职工类型");
        cell8.setCellStyle(cellStyle2);
        sheet.setColumnWidth(7, 6000);

        //生成下拉框内容
        try {
            String[] enters = listNoPageEnterprises.toArray(new String[listNoPageEnterprises.size()]);
            String[] lines = listNoPageLines.toArray(new String[listNoPageLines.size()]);
            setValidationData(sheet, 2, 50000, 0, 0, lines);
            setValidationData(sheet, 2, 50000, 3, 3, enters);
            String[] types = {"普通职工","优秀职工","劳模职工"};
            setValidationData(sheet, 2, 50000, 7, 7,types);
        } catch (Exception e) {
            e.printStackTrace();
        }
        buildExcelDocument(fileName,workbook,response);
    }

    /**
     * 添加数据有效性检查.
     * @param sheet 要添加此检查的Sheet
     * @param firstRow 开始行
     * @param lastRow 结束行
     * @param firstCol 开始列
     * @param lastCol 结束列
     * @param explicitListValues 有效性检查的下拉列表
     * @throws IllegalArgumentException 如果传入的行或者列小于0(< 0)或者结束行/列比开始行/列小
     */
    public static void setValidationData(Sheet sheet, int firstRow, int lastRow,
                                         int firstCol, int lastCol, String[] explicitListValues) throws IllegalArgumentException{
        if (firstRow < 0 || lastRow < 0 || firstCol < 0 || lastCol < 0 || lastRow < firstRow || lastCol < firstCol) {
            throw new IllegalArgumentException("Wrong Row or Column index : " + firstRow+":"+lastRow+":"+firstCol+":" +lastCol);
        }
        if (sheet instanceof XSSFSheet) {
            XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet)sheet);
            XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
                    .createExplicitListConstraint(explicitListValues);
            CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
            XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
            validation.setSuppressDropDownArrow(true);
            validation.setShowErrorBox(true);
            sheet.addValidationData(validation);
        } else if(sheet instanceof HSSFSheet){
            CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
            DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(explicitListValues);
            DataValidation validation = new HSSFDataValidation(addressList, dvConstraint);
            validation.setSuppressDropDownArrow(true);
            validation.setShowErrorBox(true);
            sheet.addValidationData(validation);
        }
    }
}
