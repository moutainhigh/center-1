package com.cmall.dborm.txmapper.groupcenter;

import com.cmall.dborm.txmodel.groupcenter.GcPayOrderDetail;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GcPayOrderDetailMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_pay_order_detail
     *
     * @mbggenerated
     */
    int countByExample(GcPayOrderDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_pay_order_detail
     *
     * @mbggenerated
     */
    int deleteByExample(GcPayOrderDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_pay_order_detail
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_pay_order_detail
     *
     * @mbggenerated
     */
    int insert(GcPayOrderDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_pay_order_detail
     *
     * @mbggenerated
     */
    int insertSelective(GcPayOrderDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_pay_order_detail
     *
     * @mbggenerated
     */
    List<GcPayOrderDetail> selectByExample(GcPayOrderDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_pay_order_detail
     *
     * @mbggenerated
     */
    GcPayOrderDetail selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_pay_order_detail
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") GcPayOrderDetail record, @Param("example") GcPayOrderDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_pay_order_detail
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") GcPayOrderDetail record, @Param("example") GcPayOrderDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_pay_order_detail
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(GcPayOrderDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_pay_order_detail
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(GcPayOrderDetail record);
}