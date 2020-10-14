package com.cmall.dborm.txmapper;

import com.cmall.dborm.txmodel.BcPurchaseDetail;
import com.cmall.dborm.txmodel.BcPurchaseDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BcPurchaseDetailMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_detail
     *
     * @mbggenerated
     */
    int countByExample(BcPurchaseDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_detail
     *
     * @mbggenerated
     */
    int deleteByExample(BcPurchaseDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_detail
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_detail
     *
     * @mbggenerated
     */
    int insert(BcPurchaseDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_detail
     *
     * @mbggenerated
     */
    int insertSelective(BcPurchaseDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_detail
     *
     * @mbggenerated
     */
    List<BcPurchaseDetail> selectByExample(BcPurchaseDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_detail
     *
     * @mbggenerated
     */
    BcPurchaseDetail selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_detail
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") BcPurchaseDetail record, @Param("example") BcPurchaseDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_detail
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") BcPurchaseDetail record, @Param("example") BcPurchaseDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_detail
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(BcPurchaseDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_detail
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(BcPurchaseDetail record);
}