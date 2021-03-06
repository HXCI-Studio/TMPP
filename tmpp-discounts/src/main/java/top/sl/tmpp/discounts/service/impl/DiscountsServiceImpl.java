package top.sl.tmpp.discounts.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.sl.tmpp.common.entity.Discounts;
import top.sl.tmpp.common.exception.IdNotFoundException;
import top.sl.tmpp.common.mapper.DiscountsMapper;
import top.sl.tmpp.discounts.service.DiscountsService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author ShuLu
 */
@Service
public class DiscountsServiceImpl implements DiscountsService {
    private final DiscountsMapper discountsMapper;
    private Logger log = LoggerFactory.getLogger(DiscountsServiceImpl.class);

    public DiscountsServiceImpl(DiscountsMapper discountsMapper) {
        this.discountsMapper = discountsMapper;
    }

    @Override
    public void save(BigDecimal discount) {
        log.debug("添加折扣");
        Date date = new Date();
        Discounts discounts = new Discounts(UUID.randomUUID().toString().replace("-", ""), discount, date, date);
        discountsMapper.insert(discounts);
    }

    @Override
    public List<Discounts> getAllDiscount() {
        List<Discounts> discounts = discountsMapper.selectAll();
        log.debug("查找全部折扣");
        return discounts;
    }

    @Override
    public void remove(String id) {
        if (discountsMapper.selectByPrimaryKey(id) == null) {
            log.debug("删除失败");
            throw new IdNotFoundException(id);
        }
        log.debug("删除折扣");
        discountsMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void modify(String id, BigDecimal discount) {
        Discounts modifyDiscount = discountsMapper.selectByPrimaryKey(id);
        if (modifyDiscount == null) {
            log.debug("修改折扣失败");
            throw new IdNotFoundException(id);
        }
        log.debug("修改折扣");
        modifyDiscount.setDiscount(discount);
        modifyDiscount.setGmtModified(new Date());
        discountsMapper.updateByPrimaryKey(modifyDiscount);
    }
}
