package top.sl.tmpp.review.service;

import com.github.pagehelper.PageInfo;
import top.sl.tmpp.common.pojo.AReview;
import top.sl.tmpp.common.pojo.OReview;


/**
 * @author itning
 * @date 2019/6/23 11:35
 */
public interface ReviewService {
    /**
     * 办公室主任我的审核
     *
     * @param planId 执行计划id
     * @param page   页数
     * @param size   每页大小
     * @return {@link PageInfo}
     */
    PageInfo<OReview> getOReviews(String planId, int page, int size);

    /**
     * 教务处我的审核
     *
     * @param planId 执行计划id
     * @param page   页数
     * @param size   每页大小
     * @return {@link PageInfo}
     */
    PageInfo<AReview> getAReviews(String planId, int page, int size);

    /**
     * 是否购买样书
     *
     * @param id 购书计划id
     * @param is 是1 否0
     */
    void isByBook(String id, String is);

    /**
     * 办公室主任全部审核通过
     *
     * @param planId 执行计划ID
     */
    void oAllPassed(String planId);

    /**
     * 教务处全部审核通过
     *
     * @param planId 执行计划ID
     */
    void aAllPassed(String planId);

    /**
     * 办公室主任驳回
     *
     * @param id 购书计划id
     */
    void oTurnDown(String id);

    /**
     * 教务处驳回
     *
     * @param id 购书计划id
     */
    void aTurnDown(String id);
}