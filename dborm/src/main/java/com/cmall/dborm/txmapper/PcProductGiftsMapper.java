package com.cmall.dborm.txmapper;

import com.cmall.dborm.txmodel.PcProductGifts;
import com.cmall.dborm.txmodel.PcProductGiftsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PcProductGiftsMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts
     *
     * @mbggenerated
     */
    int countByExample(PcProductGiftsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts
     *
     * @mbggenerated
     */
    int deleteByExample(PcProductGiftsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts
     *
     * @mbggenerated
     */
    int insert(PcProductGifts record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts
     *
     * @mbggenerated
     */
    int insertSelective(PcProductGifts record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts
     *
     * @mbggenerated
     */
    List<PcProductGifts> selectByExample(PcProductGiftsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts
     *
     * @mbggenerated
     */
    PcProductGifts selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") PcProductGifts record, @Param("example") PcProductGiftsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") PcProductGifts record, @Param("example") PcProductGiftsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(PcProductGifts record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(PcProductGifts record);
}