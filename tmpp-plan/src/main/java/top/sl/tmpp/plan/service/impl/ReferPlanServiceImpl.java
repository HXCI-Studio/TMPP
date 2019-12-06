package top.sl.tmpp.plan.service.impl;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sl.tmpp.common.entity.ExecutePlan;
import top.sl.tmpp.common.entity.Plan;
import top.sl.tmpp.common.mapper.*;
import top.sl.tmpp.common.util.ObjectUtils;
import top.sl.tmpp.plan.entity.CourseEducationalCache;
import top.sl.tmpp.plan.exception.ExcelReadException;
import top.sl.tmpp.plan.exception.FileException;
import top.sl.tmpp.plan.service.ReferPlanService;
import top.sl.tmpp.plan.util.FileUtil;

import java.io.*;
import java.util.*;

import static top.sl.tmpp.common.util.ObjectUtils.checkNotNull;


/**
 * @author ShuLu
 * @date 2019/6/17 13:46
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ReferPlanServiceImpl implements ReferPlanService {
    private static final Logger logger = LoggerFactory.getLogger(ReferPlanServiceImpl.class);
    private final PlanMapper planMapper;
    private final ExecutePlanMapper executePlanMapper;
    private final CollegesMapper collegesMapper;
    private final BookMapper bookMapper;
    private final PlanBookMapper planBookMapper;
    private final DepartmentMapper departmentMapper;

    public ReferPlanServiceImpl(PlanMapper planMapper, ExecutePlanMapper executePlanMapper, CollegesMapper collegesMapper, BookMapper bookMapper, PlanBookMapper planBookMapper, DepartmentMapper departmentMapper) {
        this.planMapper = planMapper;
        this.executePlanMapper = executePlanMapper;
        this.collegesMapper = collegesMapper;
        this.bookMapper = bookMapper;
        this.planBookMapper = planBookMapper;
        this.departmentMapper = departmentMapper;
    }

    @Override
    public void referPlan(String year, boolean term, String teachingDepartment, String educationalLevel, String fileId) {
        String fileName = System.getProperty("java.io.tmpdir") + File.separator + fileId;
        File file = new File(fileName);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            Workbook workbook = FileUtil.getWorkbook(fileInputStream, fileName);
            if (workbook.getNumberOfSheets() < 1) {
                throw new FileException("工作簿数量错误：" + workbook.getNumberOfSheets(), HttpStatus.BAD_REQUEST);
            }
            Sheet sheet = workbook.getSheetAt(0);
            logger.debug("获取到newFile最后的行数->{}", sheet.getLastRowNum());
            ExecutePlan executePlan = new ExecutePlan();
            ExecutePlan eP = executePlanMapper.selectExecutePlan(year, term, teachingDepartment, educationalLevel);
            if (eP!=null){
                int numberOfSheets = workbook.getNumberOfSheets();
                for (int i=0;i<numberOfSheets;i++){
                    Sheet sheet = workbook.getSheetAt(i);
                    executePlan = eP;
                    //创建新文件
                    String oldFileName = departmentMapper.selectByPrimaryKey(teachingDepartment).getName()
                            + "."
                            + executePlan.getFileType();
                    File oldFile = new File(oldFileName);
                    try (FileOutputStream fileOutputStream = new FileOutputStream(oldFile)) {
                        fileOutputStream.write(executePlan.getFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Workbook newWorkbook = null;
                    try (FileInputStream fileInputStream1 = new FileInputStream(oldFile)) {
                        Workbook workbook1 = FileUtil.getWorkbook(fileInputStream1, oldFileName);
                        Sheet sheet1 = workbook1.getSheetAt(0);
                        newWorkbook = mergeFile(sheet, sheet1, workbook1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    new File(fileName).delete();
                    try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
                        assert newWorkbook != null;
                        newWorkbook.write(fileOutputStream);
                        executePlan.setFile(FileUtils.readFileToByteArray(new File(fileName)));
                        executePlanMapper.updateByPrimaryKeyWithBLOBs(executePlan);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    oldFile.delete();
                    AddPlan(executePlan, sheet);
                }
            }else {
                int numberOfSheets = workbook.getNumberOfSheets();
                Sheet sheet = workbook.getSheetAt(0);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                for (int i=1;i<numberOfSheets;i++){
                    mergeFile(workbook.getSheetAt(i), sheet, workbook);
                    workbook.removeSheetAt(i);
                    workbook.write(fileOutputStream);
                }
                fileOutputStream.close();

                String newExecutePlanId = UUID.randomUUID().toString().replace("-", "");
                executePlan.setId(newExecutePlanId);
                executePlan.setYear(year);
                executePlan.setTerm(term);
                executePlan.setGrade(ObjectUtils.toInt(getCellValue(sheet.getRow(1), 1), () -> new ExcelReadException(1, 1)));
                executePlan.setStatus(false);
                executePlan.setFileType(fileId.substring(fileId.indexOf(".") + 1));
                executePlan.setLevelId(educationalLevel);
                executePlan.setDepartmentId(teachingDepartment);
                Date d = new Date();
                executePlan.setGmtModified(d);
                executePlan.setGmtCreate(d);
                executePlan.setFile(FileUtils.readFileToByteArray(file));
                executePlanMapper.insert(executePlan);
            }

                AddPlan(executePlan, sheet);

            }

        } catch (FileNotFoundException e) {
            throw new FileException("文件丢失，请重新上传", HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void AddPlan(ExecutePlan executePlan, Sheet sheet) {
        List<Plan> planList = new ArrayList<>(sheet.getLastRowNum());
        for (int rowIndex = 3; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            logger.debug("开始获取第{}行", rowIndex);
            Row row = sheet.getRow(rowIndex);
            String collegeName = getCellValue(row, 1);
            String startPro = getCellValue(row, 2);
            String courseCode = getCellValue(row, 3);
            String courseName = getCellValue(row, 4);
            String type = getCellValue(row, 5);
            String clazz = getCellValue(row, 6);
            String clazzNumber = getCellValue(row, 7);
            String teacher = getCellValue(row, 8);
            String remark = getCellValue(row, 9);
            //检查关键字段不为空
            checkCellNoneBlank(rowIndex, collegeName, startPro, courseCode, courseName, type, clazz, clazzNumber, teacher);
            final int tempRowIndex = rowIndex;
            //学院ID
            String collegeId = checkNotNull(collegesMapper.selectIdByName(collegeName), () -> new ExcelReadException(tempRowIndex + 1, 2, "学院不存在"));
            //人数
            int clazzNum = ObjectUtils.toInt(clazzNumber, () -> new ExcelReadException(tempRowIndex + 1, 8, "人数错误"));
            //检查课程代码
            checkCourseCode(courseCode, courseName, tempRowIndex + 1, 4);

            Plan plan = new Plan();
            plan.setId(UUID.randomUUID().toString().replace("-", ""));
            plan.setCollegesId(collegeId);
            plan.setStartPro(startPro);
            plan.setCourseCode(courseCode);
            plan.setCourseName(courseName);
            plan.setType(false);
            plan.setClazz(clazz);
            plan.setClazzNumber(clazzNum);
            plan.setTeacher(teacher);
            plan.setRemark(remark);
            plan.setExecutePlanId(executePlan.getId());
            Date date = new Date();
            plan.setGmtCreate(date);
            plan.setGmtModified(date);
            planList.add(plan);
        }
        planList.forEach(planMapper::insert);
    }

    @Override
    public ExecutePlan downloadExecutePlan(String id) {
        ExecutePlan executePlan = executePlanMapper.selectFileById(id);
        logger.debug("查询执行计划成功");
        return executePlan;
    }

    @Override
    public void removeExecutePlan(String id) {
        logger.debug("删除执行计划相关计划");
        List<Plan> plans = planMapper.selectByExecutePlanId(id);
        //删除plan_book
        plans.forEach(plan -> planBookMapper.deleteByPlanId(plan.getId()));
        //删除book
        bookMapper.selectIdAndStatus(id).forEach(book -> bookMapper.deleteByPrimaryKey(book.getId()));
        //删除plan
        plans.forEach(plan -> planMapper.deleteByPrimaryKey(plan.getId()));
        //删除执行计划
        executePlanMapper.deleteByPrimaryKey(id);
    }

    /**
     * 获取单元格中的数据
     *
     * @param row     行
     * @param cellNum 单元格号
     * @return 该单元格的数据
     */
    private String getCellValue(Row row, int cellNum) {
        String stringCellValue = null;
        Cell cell = row.getCell(cellNum);
        if (cell != null) {
            try {
                stringCellValue = cell.getStringCellValue();
            } catch (IllegalStateException e) {
                logger.debug("getCellValue::CellNum->" + cellNum + "<-尝试获取String类型数据失败");
                try {
                    logger.debug("getCellValue::CellNum->" + cellNum + "<-尝试获取Double类型数据");
                    stringCellValue = String.valueOf(cell.getNumericCellValue());
                } catch (IllegalStateException e1) {
                    try {
                        logger.debug("getCellValue::CellNum->" + cellNum + "<-尝试获取Boolean类型数据");
                        stringCellValue = String.valueOf(cell.getBooleanCellValue());
                    } catch (IllegalStateException e2) {
                        try {
                            logger.debug("getCellValue::CellNum->" + cellNum + "<-尝试获取Date类型数据");
                            stringCellValue = String.valueOf(cell.getDateCellValue());
                        } catch (IllegalStateException e3) {
                            logger.warn("getCellValue::CellNum->" + cellNum + "<-未知类型数据->" + e3.getMessage());
                        }
                    }
                }
            } finally {
                logger.debug("getCellValue::第" + cellNum + "格获取到的数据为->" + stringCellValue);
            }
        } else {
            logger.debug("getCellValue::第" + cellNum + "格为空");
        }
        return stringCellValue;
    }

    /**
     * 使用API接口检查课程代码
     *
     * @param courseCode 课程代码
     * @param courseName 课程名
     */
    @SuppressWarnings("SameParameterValue")
    private void checkCourseCode(String courseCode, String courseName, int row, int cell) {
        Optional<String> optional = CourseEducationalCache.getCourseName(courseCode);
        if (!optional.isPresent()) {
            throw new ExcelReadException(row, cell, "课程代码不存在");
        }
        String courseNameFromApi = optional.get();
        if (!courseNameFromApi.equals(courseName)) {
            throw new ExcelReadException(row, cell + 1, "课程名不正确，应为：[" + courseNameFromApi + "]，但是输入了：[" + courseName + "]");
        }
    }

    /**
     * 检查单元格不为空
     *
     * @param rowIndex 行索引
     * @param cells    单元格数据
     * @throws ExcelReadException 如果单元格数据为空
     */
    private void checkCellNoneBlank(int rowIndex, String... cells) {
        Optional<Integer> optional = ObjectUtils.checkStringIsNotBlank(cells);
        if (optional.isPresent()) {
            throw new ExcelReadException(rowIndex + 1, optional.get() + 2);
        }
    }

    /**
     * 合并两个excel文件
     *
     * @param newSheet
     * @param sheet
     * @param workbook1
     * @return
     */

    private Workbook mergeFile(Sheet newSheet, Sheet sheet, Workbook workbook1) {
        int lastRowNum = sheet.getLastRowNum();
        logger.debug("获取到oldFile最后的行数->{}", lastRowNum);
        logger.debug("获取到sheet1最后的行数->{}", newSheet.getLastRowNum());
        //读取newFile
        for (int rowIndex = 3; rowIndex <= newSheet.getLastRowNum(); rowIndex++) {
            logger.debug("开始获取第{}行", rowIndex);
            Row row = newSheet.getRow(rowIndex);
            String collegeName = getCellValue(row, 1);
            String startPro = getCellValue(row, 2);
            String courseCode = getCellValue(row, 3);
            String courseName = getCellValue(row, 4);
            String type = getCellValue(row, 5);
            String clazz = getCellValue(row, 6);
            String clazzNumber = getCellValue(row, 7);
            String teacher = getCellValue(row, 8);
            String remark = getCellValue(row, 9);
            //检查关键字段不为空
            checkCellNoneBlank(rowIndex, collegeName, startPro, courseCode, courseName, type, clazz, clazzNumber, teacher);
            final int tempRowIndex = rowIndex;
            //学院ID
            String collegeId = checkNotNull(collegesMapper.selectIdByName(collegeName), () -> new ExcelReadException(tempRowIndex + 1, 2, "学院不存在"));
            //人数
            int clazzNum = ObjectUtils.toInt(clazzNumber, () -> new ExcelReadException(tempRowIndex + 1, 8, "人数错误"));
            //检查课程代码
            checkCourseCode(courseCode, courseName, tempRowIndex + 1, 4);
            //往oldFile里写newFile里的内容
            logger.debug("开始获取oldfile第{}行", lastRowNum);
            Row row1 = sheet.createRow(++lastRowNum);
            row1.createCell(0,CellType.NUMERIC).setCellValue(lastRowNum-3);
            row1.createCell(1, CellType.STRING).setCellValue(collegeName);
            row1.createCell(2, CellType.STRING).setCellValue(startPro);
            row1.createCell(3, CellType.STRING).setCellValue(courseCode);
            row1.createCell(4, CellType.STRING).setCellValue(courseName);
            row1.createCell(5, CellType.STRING).setCellValue(type);
            row1.createCell(6, CellType.STRING).setCellValue(clazz);
            row1.createCell(7, CellType.STRING).setCellValue(clazzNumber);
            row1.createCell(8, CellType.STRING).setCellValue(teacher);
            row1.createCell(9, CellType.STRING).setCellValue(remark);
        }
        return workbook1;
    }
}
