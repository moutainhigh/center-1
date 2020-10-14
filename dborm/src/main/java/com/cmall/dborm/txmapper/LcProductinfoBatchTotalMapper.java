package com.cmall.dborm.txmapper;

import com.cmall.dborm.txmodel.LcProductinfoBatchTotal;
import com.cmall.dborm.txmodel.LcProductinfoBatchTotalExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface LcProductinfoBatchTotalMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lc_productinfo_batch_total
     *
     * @mbggenerated
     */
    int countByExample(LcProductinfoBatchTotalExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lc_productinfo_batch_total
     *
     * @mbggenerated
     */
    int deleteByExample(LcProductinfoBatchTotalExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lc_productinfo_batch_total
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lc_productinfo_batch_total
     *
     * @mbggenerated
     */
    int insert(LcProductinfoBatchTotal record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lc_productinfo_batch_total
     *
     * @mbggenerated
     */
    int insertSelective(LcProductinfoBatchTotal record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lc_productinfo_batch_total
     *
     * @mbggenerated
     */
    List<LcProductinfoBatchTotal> selectByExample(LcProductinfoBatchTotalExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lc_productinfo_batch_total
     *
     * @mbggenerated
     */
    LcProductinfoBatchTotal selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lc_productinfo_batch_total
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") LcProductinfoBatchTotal record, @Param("example") LcProductinfoBatchTotalExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lc_productinfo_batch_total
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") LcProductinfoBatchTotal record, @Param("example") LcProductinfoBatchTotalExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lc_productinfo_batch_total
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(LcProductinfoBatchTotal record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lc_productinfo_batch_total
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(LcProductinfoBatchTotal record);
}