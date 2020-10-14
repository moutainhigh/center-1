package com.cmall.dborm.txmapper;

import com.cmall.dborm.txmodel.OcExchangeGoodsDetail;
import com.cmall.dborm.txmodel.OcExchangeGoodsDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OcExchangeGoodsDetailMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_exchange_goods_detail
     *
     * @mbggenerated
     */
    int countByExample(OcExchangeGoodsDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_exchange_goods_detail
     *
     * @mbggenerated
     */
    int deleteByExample(OcExchangeGoodsDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_exchange_goods_detail
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_exchange_goods_detail
     *
     * @mbggenerated
     */
    int insert(OcExchangeGoodsDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_exchange_goods_detail
     *
     * @mbggenerated
     */
    int insertSelective(OcExchangeGoodsDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_exchange_goods_detail
     *
     * @mbggenerated
     */
    List<OcExchangeGoodsDetail> selectByExample(OcExchangeGoodsDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_exchange_goods_detail
     *
     * @mbggenerated
     */
    OcExchangeGoodsDetail selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_exchange_goods_detail
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") OcExchangeGoodsDetail record, @Param("example") OcExchangeGoodsDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_exchange_goods_detail
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") OcExchangeGoodsDetail record, @Param("example") OcExchangeGoodsDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_exchange_goods_detail
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(OcExchangeGoodsDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_exchange_goods_detail
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(OcExchangeGoodsDetail record);
}