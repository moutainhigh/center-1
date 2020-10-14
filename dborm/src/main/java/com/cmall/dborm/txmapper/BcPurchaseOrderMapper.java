package com.cmall.dborm.txmapper;

import com.cmall.dborm.txmodel.BcPurchaseOrder;
import com.cmall.dborm.txmodel.BcPurchaseOrderExample;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BcPurchaseOrderMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_order
     *
     * @mbggenerated
     */
    int countByExample(BcPurchaseOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_order
     *
     * @mbggenerated
     */
    int deleteByExample(BcPurchaseOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_order
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_order
     *
     * @mbggenerated
     */
    int insert(BcPurchaseOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_order
     *
     * @mbggenerated
     */
    int insertSelective(BcPurchaseOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_order
     *
     * @mbggenerated
     */
    List<BcPurchaseOrder> selectByExample(BcPurchaseOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_order
     *
     * @mbggenerated
     */
    BcPurchaseOrder selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_order
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") BcPurchaseOrder record, @Param("example") BcPurchaseOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_order
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") BcPurchaseOrder record, @Param("example") BcPurchaseOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_order
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(BcPurchaseOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bc_purchase_order
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(BcPurchaseOrder record);
    
    /**
     * 更新钱和商品数量
     * @param balanceMoney
     * @param purchaseCount
     * @param purchaseorderCode
     * @return
     */
    int updateMoneyByCode(@Param("balanceMoney")BigDecimal balanceMoney,@Param("purchaseCount")int purchaseCount,@Param("purchaseorderCode")String purchaseorderCode);
}