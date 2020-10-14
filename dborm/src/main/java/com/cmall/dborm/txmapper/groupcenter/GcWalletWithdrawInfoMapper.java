package com.cmall.dborm.txmapper.groupcenter;

import com.cmall.dborm.txmodel.groupcenter.GcWalletWithdrawInfo;
import com.cmall.dborm.txmodel.groupcenter.GcWalletWithdrawInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GcWalletWithdrawInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_wallet_withdraw_info
     *
     * @mbggenerated
     */
    int countByExample(GcWalletWithdrawInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_wallet_withdraw_info
     *
     * @mbggenerated
     */
    int deleteByExample(GcWalletWithdrawInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_wallet_withdraw_info
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_wallet_withdraw_info
     *
     * @mbggenerated
     */
    int insert(GcWalletWithdrawInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_wallet_withdraw_info
     *
     * @mbggenerated
     */
    int insertSelective(GcWalletWithdrawInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_wallet_withdraw_info
     *
     * @mbggenerated
     */
    List<GcWalletWithdrawInfo> selectByExample(GcWalletWithdrawInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_wallet_withdraw_info
     *
     * @mbggenerated
     */
    GcWalletWithdrawInfo selectByPrimaryKey(Integer zid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_wallet_withdraw_info
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") GcWalletWithdrawInfo record, @Param("example") GcWalletWithdrawInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_wallet_withdraw_info
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") GcWalletWithdrawInfo record, @Param("example") GcWalletWithdrawInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_wallet_withdraw_info
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(GcWalletWithdrawInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_wallet_withdraw_info
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(GcWalletWithdrawInfo record);
}