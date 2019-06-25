package top.sl.tmpp.export.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author ShuLu
 * @date 2019/6/25 10:42
 */
@Controller
public class ExportController {
    /**
     * 导出采购教材汇总表
     *
     * @param executePlanId 执行计划id
     */
    @GetMapping("/procurement_table")
    public void procurementTable(@RequestParam("execute_plan_id") String executePlanId) {

    }

    /**
     * 征订教材汇总表格
     *
     * @param year               年
     * @param college            学院
     * @param teachingDepartment 授课部门
     * @param term               学期
     */
    @GetMapping("/down_book_materials")
    public void downBookMaterials(@RequestParam() String year,
                                  @RequestParam String college,
                                  @RequestParam String teachingDepartment,
                                  @RequestParam String term) {

    }

    /**
     * 考试/考察/总体订书率表
     *
     * @param executePlanId 执行计划id
     */
    @GetMapping("/summary_table")
    public void summaryTable(@RequestParam("execute_plan_id") String executePlanId) {

    }

    /**
     * 征订教材计划统计表
     *
     * @param executePlanId 执行计划id
     */
    @GetMapping("/textbook_plan_statistics")
    public void textbookPlanStatistics(@RequestParam("execute_plan_id") String executePlanId) {

    }

    /**
     * 出版社统计数量表
     *
     * @param executePlanId 执行计划id
     */
    @GetMapping("/publishing_house_statistics")
    public void publishingHouseStatistics(@RequestParam("execute_plan_id") String executePlanId) {

    }

    /**
     * 征订教材样书统计表
     *
     * @param executePlanId 执行计划id
     */
    @GetMapping("/subscription_book")
    public void subscriptionBook(@RequestParam("execute_plan_id") String executePlanId) {

    }

    /**
     * 教师领取教材汇总表
     *
     * @param executePlanId 执行计划id
     */
    @GetMapping("/teacher_receiving_textbook")
    public void teacherReceivingTextbook(@RequestParam("execute_plan_id") String executePlanId) {

    }

    /**
     * 学生班级领取教材反馈表
     *
     * @param executePlanId 执行计划id
     */
    @GetMapping("/student_textbook")
    public void studentTextbook(@RequestParam("execute_plan_id") String executePlanId) {

    }
}