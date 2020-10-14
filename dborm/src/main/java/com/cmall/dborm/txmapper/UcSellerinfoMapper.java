package com.cmall.dborm.txmapper;

import com.cmall.dborm.txmodel.UcSellerinfo;
import com.cmall.dborm.txmodel.UcSellerinfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UcSellerinfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table uc_sellerinfo
     *
     * @mbggenerated
     */
    int countByExample(UcSellerinfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table uc_sellerinfo
     *
     * @mbggenerated
     */
    int deleteByExample(UcSellerinfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table uc_sellerinfo
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table uc_sellerinfo
     *
     * @mbggenerated
     */
    int insert(UcSellerinfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table uc_sellerinfo
     *
     * @mbggenerated
     */
    int insertSelective(UcSellerinfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table uc_sellerinfo
     *
     * @mbggenerated
     */
    List<UcSellerinfo> selectByExample(UcSellerinfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table uc_sellerinfo
     *
     * @mbggenerated
     */
    UcSellerinfo selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table uc_sellerinfo
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") UcSellerinfo record, @Param("example") UcSellerinfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table uc_sellerinfo
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") UcSellerinfo record, @Param("example") UcSellerinfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table uc_sellerinfo
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(UcSellerinfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table uc_sellerinfo
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(UcSellerinfo record);
}