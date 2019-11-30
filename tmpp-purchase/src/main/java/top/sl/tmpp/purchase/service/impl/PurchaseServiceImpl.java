package top.sl.tmpp.purchase.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.sl.tmpp.common.entity.*;
import top.sl.tmpp.common.exception.EmptyParameterException;
import top.sl.tmpp.common.exception.IllegalParameterException;
import top.sl.tmpp.common.mapper.*;
import top.sl.tmpp.common.pojo.BookDTO;
import top.sl.tmpp.common.util.ObjectUtils;
import top.sl.tmpp.purchase.service.PurchaseService;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author itning
 * @date 2019/6/23 15:11
 */
@Service
public class PurchaseServiceImpl implements PurchaseService {
    private final BookMapper bookMapper;
    private final PlanMapper planMapper;
    private final PlanBookMapper planBookMapper;
    private final ExecutePlanMapper executePlanMapper;
    private final DepartmentMapper departmentMapper;

    @Autowired
    public PurchaseServiceImpl(BookMapper bookMapper, PlanMapper planMapper, PlanBookMapper planBookMapper, ExecutePlanMapper executePlanMapper, DepartmentMapper departmentMapper) {
        this.bookMapper = bookMapper;
        this.planMapper = planMapper;
        this.planBookMapper = planBookMapper;
        this.executePlanMapper = executePlanMapper;
        this.departmentMapper = departmentMapper;
    }

    @Override
    public void buyBook(String loginUserId, String executePlanId, String courseCode, String isbn, String textBookName, Boolean textBookCategory,
                        String press, String author, BigDecimal unitPrice, Integer teacherBookNumber, BigDecimal discount,
                        String awardInformation, Date publicationDate, String subscriber, String subscriberTel, String bookId) {
        checkTel(subscriberTel);
        Book book = new Book();
        book.setId(bookId);
        book.setIsbn(isbn);
        book.setTextBookName(textBookName);
        book.setTextBookCategory(textBookCategory);
        book.setPress(press);
        book.setAuthor(author);
        book.setUnitPrice(unitPrice);
        book.setTeacherBookNumber(teacherBookNumber);
        book.setDiscount(discount);
        book.setAwardInformation(awardInformation);
        book.setPublicationDate(publicationDate);
        book.setSubscriber(subscriber);
        book.setSubscriberTel(subscriberTel);
        book.setIsBookPurchase(true);
        book.setLoginUserId(loginUserId);
        book.setExecutePlanId(executePlanId);
        book.setStatus(0);
        //教务处购买样书默认false
        book.setIsBuyBook(false);
        Date date = new Date();
        book.setGmtModified(date);
        book.setGmtCreate(date);
        if (bookId == null) {
            //新增图书
            book.setId(UUID.randomUUID().toString().replace("-", ""));
            ObjectUtils.checkObjectFieldsNotEmpty(book, "reason", "isBuyBook");
            saveBookInfo(executePlanId, courseCode, book.getId(), book);
        } else {
            //修改图书
            ObjectUtils.checkObjectFieldsNotEmpty(book, "reason", "isBuyBook");
            bookMapper.updateByPrimaryKey(book);
        }

    }

    /**
     * 检查电话号码正确性
     *
     * @param tel 电话号
     */
    private void checkTel(String tel) {
        long telLong = NumberUtils.toLong(tel);
        if (telLong == 0L) {
            throw new IllegalParameterException("手机号不正确");
        }
        if (tel.length() != 11) {
            throw new IllegalParameterException("手机号长度不正确，输入了" + tel.length() + "位");
        }
        if (!tel.startsWith("1")) {
            throw new IllegalParameterException("手机号不正确");
        }
    }

    @Override
    public void notBuyBook(String loginUserId, String executePlanId, String courseCode, String reason, String bookId) {
        if (StringUtils.isAnyBlank(reason, courseCode, executePlanId)) {
            throw new EmptyParameterException("原因或课程代码或执行计划ID为空");
        }
        Book book = new Book();
        book.setId(bookId);
        book.setIsBookPurchase(false);
        book.setReason(reason);
        book.setLoginUserId(loginUserId);
        book.setExecutePlanId(executePlanId);
        //0: 未审核
        book.setStatus(0);
        //教务处购买样书默认false
        book.setIsBuyBook(false);
        Date date = new Date();
        book.setGmtModified(date);
        book.setGmtCreate(date);
        if (bookId == null) {
            book.setId(UUID.randomUUID().toString().replace("-", ""));
            saveBookInfo(executePlanId, courseCode, book.getId(), book);
        } else {
            bookMapper.updateByPrimaryKey(book);
        }
    }

    /**
     * 新增图书
     *
     * @param executePlanId 执行计划ID
     * @param courseCode    课程代码
     * @param newBookId     图书ID
     * @param book          {@link Book}
     */
    private void saveBookInfo(String executePlanId, String courseCode, String newBookId, Book book) {
        bookMapper.insertSelective(book);
        planMapper.selectByExecutePlanIdAndCourseCode(executePlanId, courseCode)
                .stream()
                .map(Plan::getId)
                .forEach(planId -> planBookMapper.insert(new PlanBook(planId, newBookId)));
    }


    @Override
    public PageInfo<BookDTO> getAllTeacherBooks(String loginUserId, String executePlanId, int page, int size) {
        return PageHelper
                .startPage(page, size)
                .doSelectPageInfo(() -> bookMapper.selectMyBook(loginUserId, executePlanId));
    }

    @Override
    public void subscriptionPlan(OutputStream outputStream, String loginUserId, String executePlanId) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();

        List<BookDTO> bookDTOS = bookMapper.selectMyBook(loginUserId, executePlanId);
        Department department = departmentMapper.selectByPrimaryKey(executePlanMapper.selectByPrimaryKey(executePlanId).getDepartmentId());
        ExecutePlan executePlan = executePlanMapper.selectByPrimaryKey(executePlanId);

        int k=1;
        for (int i = 0; i < bookDTOS.size(); i++) {

            BookDTO bookDTO = bookDTOS.get(i);
            if (bookDTO.getReason() == null) {
                //征订教材计划单
                XSSFSheet sheet = wb.createSheet("征订教材汇总表"+(k++));
                sheet.setVerticallyCenter(true);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
                XSSFRow row0 = sheet.createRow(0);
                XSSFCell cell = getCellWithStyle(wb, row0);
                cell.setCellValue(executePlan.getYear()+"学年第"+(executePlan.getTerm() ? "一" : "二")+"学期 征订教材计划单");

                sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 3));
                XSSFRow row1 = sheet.createRow(1);
                Date date=new Date();
                SimpleDateFormat dateFormatYear=new SimpleDateFormat("YYYY");
                SimpleDateFormat dateFormatMonth=new SimpleDateFormat("MM");
                SimpleDateFormat dateFormatDay=new SimpleDateFormat("dd");
                row1.createCell(0, CellType.STRING).setCellValue(department.getName() + "   院/系/部");
                row1.createCell(2, CellType.STRING).setCellValue("填表时间：    "+dateFormatYear.format(date)+"年    "+dateFormatMonth.format(date)+"月   "+dateFormatDay.format(date)+"日");

                sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));
                sheet.createRow(2);

                XSSFRow row3 = sheet.createRow(3);
                getCellWithStyle(wb, row3);
                row3.createCell(0, CellType.STRING).setCellValue("教材类别");
                row3.createCell(1, CellType.STRING).setCellValue("□外购教材     □自编教材");
                row3.createCell(2, CellType.STRING).setCellValue("课程性质");
                row3.createCell(3, CellType.STRING).setCellValue("□选修               □必修");

                sheet.addMergedRegion(new CellRangeAddress(4, 4, 1, 3));
                XSSFRow row4 = sheet.createRow(4);
                row4.createCell(0, CellType.STRING).setCellValue("教材所属系列");
                row4.createCell(1, CellType.STRING).setCellValue(bookDTO.getAwardInformation());

                XSSFRow row5 = sheet.createRow(5);
                row5.createCell(0, CellType.STRING).setCellValue("征订单提交人");
                row5.createCell(1, CellType.STRING).setCellValue(bookDTO.getSubscriber());
                row5.createCell(2, CellType.STRING).setCellValue("教师教材征订数量");
                row5.createCell(2, CellType.STRING).setCellValue(bookDTO.getTeacherBookNumber());

                sheet.addMergedRegion(new CellRangeAddress(6, 6, 1, 3));
                XSSFRow row6 = sheet.createRow(6);
                row6.createCell(0, CellType.STRING).setCellValue("课程名称");
                row6.createCell(1, CellType.STRING).setCellValue(bookDTO.getCourseName());

                sheet.addMergedRegion(new CellRangeAddress(7, 7, 1, 3));
                XSSFRow row7 = sheet.createRow(7);
                row7.createCell(0, CellType.STRING).setCellValue("教材名称");
                row7.createCell(1, CellType.STRING).setCellValue(bookDTO.getTextBookName());

                XSSFRow row8 = sheet.createRow(8);
                getCellWithStyle(wb, row8);
                row8.createCell(0, CellType.STRING).setCellValue("出版社");
                row8.createCell(1, CellType.STRING).setCellValue(bookDTO.getPress());
                row8.createCell(2, CellType.STRING).setCellValue("作者");
                row8.createCell(3, CellType.STRING).setCellValue(bookDTO.getAuthor());

                XSSFRow row9 = sheet.createRow(9);
                getCellWithStyle(wb, row9);
                row9.createCell(0, CellType.STRING).setCellValue("单价");
                row9.createCell(1, CellType.STRING).setCellValue(bookDTO.getUnitPrice().toString());
                row9.createCell(2, CellType.STRING).setCellValue("书号");
                row9.createCell(3, CellType.STRING).setCellValue(bookDTO.getIsbn());

                sheet.addMergedRegion(new CellRangeAddress(10, 10, 0, 3));
                sheet.createRow(10);

                XSSFRow row11 = sheet.createRow(11);
                getCellWithStyle(wb, row11);
                row11.createCell(0, CellType.STRING).setCellValue("使用专业");
                row11.createCell(1, CellType.STRING).setCellValue("使用班级 ");
                row11.createCell(2, CellType.STRING).setCellValue("使用数量");
                row11.createCell(3, CellType.STRING).setCellValue("任课教师签字");


                String courseCode = bookDTO.getCourseCode();

                List<Plan> plans = planMapper.selectByExecutePlanIdAndCourseCode(executePlanId, courseCode);
                int s=plans.size();
                int sum=0;
                for (int j = 0; j < s; j++) {
                    XSSFRow row12 = sheet.createRow(12 + j);
                    getCellWithStyle(wb, row12);
                    row12.createCell(0, CellType.STRING).setCellValue(plans.get(j).getStartPro());
                    row12.createCell(1, CellType.STRING).setCellValue(plans.get(j).getClazz());
                    row12.createCell(2, CellType.STRING).setCellValue(plans.get(j).getClazzNumber());
                    sum=sum+plans.get(j).getClazzNumber();
                }

                int star=12 + s;
                XSSFRow row = sheet.createRow(star++);
                row.createCell(0,CellType.STRING).setCellValue("学生教材合计");
                row.createCell(1,CellType.STRING).setCellValue(sum+"册");
                row.createCell(2,CellType.STRING).setCellValue("教材总计");
                row.createCell(3,CellType.STRING).setCellValue((sum+bookDTO.getTeacherBookNumber())+"册");

                sheet.addMergedRegion(new CellRangeAddress(star, star, 0, 3));
                XSSFRow row2 = sheet.createRow(star++);

                sheet.addMergedRegion(new CellRangeAddress(star, star+1, 0, 0));
                sheet.addMergedRegion(new CellRangeAddress(star, ++star, 1, 3));
                XSSFRow row10 = sheet.createRow(star++-1);
                row10.createCell(0,CellType.STRING).setCellValue("专业主任意见：");
                row10.createCell(1,CellType.STRING).setCellValue("                           签字：           年    月   日        ");

                sheet.addMergedRegion(new CellRangeAddress(star, star+1, 0, 0));
                sheet.addMergedRegion(new CellRangeAddress(star, ++star, 1, 3));
                XSSFRow row12 = sheet.createRow(star++-1);
                row12.createCell(0,CellType.STRING).setCellValue("院/系/部负责人意见");
                row12.createCell(1,CellType.STRING).setCellValue("                           签字：           年    月   日        ");

                sheet.addMergedRegion(new CellRangeAddress(star, star, 0, 3));
                XSSFRow row13 = sheet.createRow(star++);

                sheet.addMergedRegion(new CellRangeAddress(star, star, 0, 3));
                XSSFRow row14 = sheet.createRow(star++);
                row14.createCell(0,CellType.STRING).setCellValue("备注：");

                sheet.addMergedRegion(new CellRangeAddress(star, star, 0, 3));
                XSSFRow row15 = sheet.createRow(star++);
                row15.createCell(0,CellType.STRING).setCellValue("1、请在画\"□\"处打\"√\"确定所选内容。");

                sheet.addMergedRegion(new CellRangeAddress(star, star, 0, 3));
                XSSFRow row16 = sheet.createRow(star++);
                row16.createCell(0,CellType.STRING).setCellValue("2、《征订教材计划单》至少在教材使用日前30天提交院/系/部通过并汇总成《院系部征订教材计划统计表》。");

                sheet.addMergedRegion(new CellRangeAddress(star, star, 0, 3));
                XSSFRow row17 = sheet.createRow(star++);
                row17.createCell(0,CellType.STRING).setCellValue("3、加盖院/系/部公章的《征订教材计划表》纸介和电子版，于教材使用前30天交教务处教材科一份。     ");

            }


        }

        wb.write(outputStream);

    }

    private XSSFCell getCellWithStyle(XSSFWorkbook wb, XSSFRow row) {
        CellStyle style = wb.createCellStyle();

        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);

        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);


        XSSFCell cell = row.createCell(0, CellType.STRING);
        cell.setCellStyle(style);
        return cell;
    }
}
