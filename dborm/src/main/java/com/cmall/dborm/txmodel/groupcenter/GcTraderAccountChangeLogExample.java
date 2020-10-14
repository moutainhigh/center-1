package com.cmall.dborm.txmodel.groupcenter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GcTraderAccountChangeLogExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table gc_trader_account_change_log
     *
     * @mbggenerated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table gc_trader_account_change_log
     *
     * @mbggenerated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table gc_trader_account_change_log
     *
     * @mbggenerated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_account_change_log
     *
     * @mbggenerated
     */
    public GcTraderAccountChangeLogExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_account_change_log
     *
     * @mbggenerated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_account_change_log
     *
     * @mbggenerated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_account_change_log
     *
     * @mbggenerated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_account_change_log
     *
     * @mbggenerated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_account_change_log
     *
     * @mbggenerated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_account_change_log
     *
     * @mbggenerated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_account_change_log
     *
     * @mbggenerated
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_account_change_log
     *
     * @mbggenerated
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_account_change_log
     *
     * @mbggenerated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table gc_trader_account_change_log
     *
     * @mbggenerated
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table gc_trader_account_change_log
     *
     * @mbggenerated
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andZidIsNull() {
            addCriterion("zid is null");
            return (Criteria) this;
        }

        public Criteria andZidIsNotNull() {
            addCriterion("zid is not null");
            return (Criteria) this;
        }

        public Criteria andZidEqualTo(Integer value) {
            addCriterion("zid =", value, "zid");
            return (Criteria) this;
        }

        public Criteria andZidNotEqualTo(Integer value) {
            addCriterion("zid <>", value, "zid");
            return (Criteria) this;
        }

        public Criteria andZidGreaterThan(Integer value) {
            addCriterion("zid >", value, "zid");
            return (Criteria) this;
        }

        public Criteria andZidGreaterThanOrEqualTo(Integer value) {
            addCriterion("zid >=", value, "zid");
            return (Criteria) this;
        }

        public Criteria andZidLessThan(Integer value) {
            addCriterion("zid <", value, "zid");
            return (Criteria) this;
        }

        public Criteria andZidLessThanOrEqualTo(Integer value) {
            addCriterion("zid <=", value, "zid");
            return (Criteria) this;
        }

        public Criteria andZidIn(List<Integer> values) {
            addCriterion("zid in", values, "zid");
            return (Criteria) this;
        }

        public Criteria andZidNotIn(List<Integer> values) {
            addCriterion("zid not in", values, "zid");
            return (Criteria) this;
        }

        public Criteria andZidBetween(Integer value1, Integer value2) {
            addCriterion("zid between", value1, value2, "zid");
            return (Criteria) this;
        }

        public Criteria andZidNotBetween(Integer value1, Integer value2) {
            addCriterion("zid not between", value1, value2, "zid");
            return (Criteria) this;
        }

        public Criteria andUidIsNull() {
            addCriterion("uid is null");
            return (Criteria) this;
        }

        public Criteria andUidIsNotNull() {
            addCriterion("uid is not null");
            return (Criteria) this;
        }

        public Criteria andUidEqualTo(String value) {
            addCriterion("uid =", value, "uid");
            return (Criteria) this;
        }

        public Criteria andUidNotEqualTo(String value) {
            addCriterion("uid <>", value, "uid");
            return (Criteria) this;
        }

        public Criteria andUidGreaterThan(String value) {
            addCriterion("uid >", value, "uid");
            return (Criteria) this;
        }

        public Criteria andUidGreaterThanOrEqualTo(String value) {
            addCriterion("uid >=", value, "uid");
            return (Criteria) this;
        }

        public Criteria andUidLessThan(String value) {
            addCriterion("uid <", value, "uid");
            return (Criteria) this;
        }

        public Criteria andUidLessThanOrEqualTo(String value) {
            addCriterion("uid <=", value, "uid");
            return (Criteria) this;
        }

        public Criteria andUidLike(String value) {
            addCriterion("uid like", value, "uid");
            return (Criteria) this;
        }

        public Criteria andUidNotLike(String value) {
            addCriterion("uid not like", value, "uid");
            return (Criteria) this;
        }

        public Criteria andUidIn(List<String> values) {
            addCriterion("uid in", values, "uid");
            return (Criteria) this;
        }

        public Criteria andUidNotIn(List<String> values) {
            addCriterion("uid not in", values, "uid");
            return (Criteria) this;
        }

        public Criteria andUidBetween(String value1, String value2) {
            addCriterion("uid between", value1, value2, "uid");
            return (Criteria) this;
        }

        public Criteria andUidNotBetween(String value1, String value2) {
            addCriterion("uid not between", value1, value2, "uid");
            return (Criteria) this;
        }

        public Criteria andTraderCodeIsNull() {
            addCriterion("trader_code is null");
            return (Criteria) this;
        }

        public Criteria andTraderCodeIsNotNull() {
            addCriterion("trader_code is not null");
            return (Criteria) this;
        }

        public Criteria andTraderCodeEqualTo(String value) {
            addCriterion("trader_code =", value, "traderCode");
            return (Criteria) this;
        }

        public Criteria andTraderCodeNotEqualTo(String value) {
            addCriterion("trader_code <>", value, "traderCode");
            return (Criteria) this;
        }

        public Criteria andTraderCodeGreaterThan(String value) {
            addCriterion("trader_code >", value, "traderCode");
            return (Criteria) this;
        }

        public Criteria andTraderCodeGreaterThanOrEqualTo(String value) {
            addCriterion("trader_code >=", value, "traderCode");
            return (Criteria) this;
        }

        public Criteria andTraderCodeLessThan(String value) {
            addCriterion("trader_code <", value, "traderCode");
            return (Criteria) this;
        }

        public Criteria andTraderCodeLessThanOrEqualTo(String value) {
            addCriterion("trader_code <=", value, "traderCode");
            return (Criteria) this;
        }

        public Criteria andTraderCodeLike(String value) {
            addCriterion("trader_code like", value, "traderCode");
            return (Criteria) this;
        }

        public Criteria andTraderCodeNotLike(String value) {
            addCriterion("trader_code not like", value, "traderCode");
            return (Criteria) this;
        }

        public Criteria andTraderCodeIn(List<String> values) {
            addCriterion("trader_code in", values, "traderCode");
            return (Criteria) this;
        }

        public Criteria andTraderCodeNotIn(List<String> values) {
            addCriterion("trader_code not in", values, "traderCode");
            return (Criteria) this;
        }

        public Criteria andTraderCodeBetween(String value1, String value2) {
            addCriterion("trader_code between", value1, value2, "traderCode");
            return (Criteria) this;
        }

        public Criteria andTraderCodeNotBetween(String value1, String value2) {
            addCriterion("trader_code not between", value1, value2, "traderCode");
            return (Criteria) this;
        }

        public Criteria andPreAvailableMoneyIsNull() {
            addCriterion("pre_available_money is null");
            return (Criteria) this;
        }

        public Criteria andPreAvailableMoneyIsNotNull() {
            addCriterion("pre_available_money is not null");
            return (Criteria) this;
        }

        public Criteria andPreAvailableMoneyEqualTo(BigDecimal value) {
            addCriterion("pre_available_money =", value, "preAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andPreAvailableMoneyNotEqualTo(BigDecimal value) {
            addCriterion("pre_available_money <>", value, "preAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andPreAvailableMoneyGreaterThan(BigDecimal value) {
            addCriterion("pre_available_money >", value, "preAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andPreAvailableMoneyGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("pre_available_money >=", value, "preAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andPreAvailableMoneyLessThan(BigDecimal value) {
            addCriterion("pre_available_money <", value, "preAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andPreAvailableMoneyLessThanOrEqualTo(BigDecimal value) {
            addCriterion("pre_available_money <=", value, "preAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andPreAvailableMoneyIn(List<BigDecimal> values) {
            addCriterion("pre_available_money in", values, "preAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andPreAvailableMoneyNotIn(List<BigDecimal> values) {
            addCriterion("pre_available_money not in", values, "preAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andPreAvailableMoneyBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("pre_available_money between", value1, value2, "preAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andPreAvailableMoneyNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("pre_available_money not between", value1, value2, "preAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andChangeMoneyIsNull() {
            addCriterion("change_money is null");
            return (Criteria) this;
        }

        public Criteria andChangeMoneyIsNotNull() {
            addCriterion("change_money is not null");
            return (Criteria) this;
        }

        public Criteria andChangeMoneyEqualTo(BigDecimal value) {
            addCriterion("change_money =", value, "changeMoney");
            return (Criteria) this;
        }

        public Criteria andChangeMoneyNotEqualTo(BigDecimal value) {
            addCriterion("change_money <>", value, "changeMoney");
            return (Criteria) this;
        }

        public Criteria andChangeMoneyGreaterThan(BigDecimal value) {
            addCriterion("change_money >", value, "changeMoney");
            return (Criteria) this;
        }

        public Criteria andChangeMoneyGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("change_money >=", value, "changeMoney");
            return (Criteria) this;
        }

        public Criteria andChangeMoneyLessThan(BigDecimal value) {
            addCriterion("change_money <", value, "changeMoney");
            return (Criteria) this;
        }

        public Criteria andChangeMoneyLessThanOrEqualTo(BigDecimal value) {
            addCriterion("change_money <=", value, "changeMoney");
            return (Criteria) this;
        }

        public Criteria andChangeMoneyIn(List<BigDecimal> values) {
            addCriterion("change_money in", values, "changeMoney");
            return (Criteria) this;
        }

        public Criteria andChangeMoneyNotIn(List<BigDecimal> values) {
            addCriterion("change_money not in", values, "changeMoney");
            return (Criteria) this;
        }

        public Criteria andChangeMoneyBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("change_money between", value1, value2, "changeMoney");
            return (Criteria) this;
        }

        public Criteria andChangeMoneyNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("change_money not between", value1, value2, "changeMoney");
            return (Criteria) this;
        }

        public Criteria andNowAvailableMoneyIsNull() {
            addCriterion("now_available_money is null");
            return (Criteria) this;
        }

        public Criteria andNowAvailableMoneyIsNotNull() {
            addCriterion("now_available_money is not null");
            return (Criteria) this;
        }

        public Criteria andNowAvailableMoneyEqualTo(BigDecimal value) {
            addCriterion("now_available_money =", value, "nowAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andNowAvailableMoneyNotEqualTo(BigDecimal value) {
            addCriterion("now_available_money <>", value, "nowAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andNowAvailableMoneyGreaterThan(BigDecimal value) {
            addCriterion("now_available_money >", value, "nowAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andNowAvailableMoneyGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("now_available_money >=", value, "nowAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andNowAvailableMoneyLessThan(BigDecimal value) {
            addCriterion("now_available_money <", value, "nowAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andNowAvailableMoneyLessThanOrEqualTo(BigDecimal value) {
            addCriterion("now_available_money <=", value, "nowAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andNowAvailableMoneyIn(List<BigDecimal> values) {
            addCriterion("now_available_money in", values, "nowAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andNowAvailableMoneyNotIn(List<BigDecimal> values) {
            addCriterion("now_available_money not in", values, "nowAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andNowAvailableMoneyBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("now_available_money between", value1, value2, "nowAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andNowAvailableMoneyNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("now_available_money not between", value1, value2, "nowAvailableMoney");
            return (Criteria) this;
        }

        public Criteria andChangeTypeIsNull() {
            addCriterion("change_type is null");
            return (Criteria) this;
        }

        public Criteria andChangeTypeIsNotNull() {
            addCriterion("change_type is not null");
            return (Criteria) this;
        }

        public Criteria andChangeTypeEqualTo(String value) {
            addCriterion("change_type =", value, "changeType");
            return (Criteria) this;
        }

        public Criteria andChangeTypeNotEqualTo(String value) {
            addCriterion("change_type <>", value, "changeType");
            return (Criteria) this;
        }

        public Criteria andChangeTypeGreaterThan(String value) {
            addCriterion("change_type >", value, "changeType");
            return (Criteria) this;
        }

        public Criteria andChangeTypeGreaterThanOrEqualTo(String value) {
            addCriterion("change_type >=", value, "changeType");
            return (Criteria) this;
        }

        public Criteria andChangeTypeLessThan(String value) {
            addCriterion("change_type <", value, "changeType");
            return (Criteria) this;
        }

        public Criteria andChangeTypeLessThanOrEqualTo(String value) {
            addCriterion("change_type <=", value, "changeType");
            return (Criteria) this;
        }

        public Criteria andChangeTypeLike(String value) {
            addCriterion("change_type like", value, "changeType");
            return (Criteria) this;
        }

        public Criteria andChangeTypeNotLike(String value) {
            addCriterion("change_type not like", value, "changeType");
            return (Criteria) this;
        }

        public Criteria andChangeTypeIn(List<String> values) {
            addCriterion("change_type in", values, "changeType");
            return (Criteria) this;
        }

        public Criteria andChangeTypeNotIn(List<String> values) {
            addCriterion("change_type not in", values, "changeType");
            return (Criteria) this;
        }

        public Criteria andChangeTypeBetween(String value1, String value2) {
            addCriterion("change_type between", value1, value2, "changeType");
            return (Criteria) this;
        }

        public Criteria andChangeTypeNotBetween(String value1, String value2) {
            addCriterion("change_type not between", value1, value2, "changeType");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(String value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(String value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(String value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(String value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(String value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(String value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLike(String value) {
            addCriterion("create_time like", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotLike(String value) {
            addCriterion("create_time not like", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<String> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<String> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(String value1, String value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(String value1, String value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andChangeCodesIsNull() {
            addCriterion("change_codes is null");
            return (Criteria) this;
        }

        public Criteria andChangeCodesIsNotNull() {
            addCriterion("change_codes is not null");
            return (Criteria) this;
        }

        public Criteria andChangeCodesEqualTo(String value) {
            addCriterion("change_codes =", value, "changeCodes");
            return (Criteria) this;
        }

        public Criteria andChangeCodesNotEqualTo(String value) {
            addCriterion("change_codes <>", value, "changeCodes");
            return (Criteria) this;
        }

        public Criteria andChangeCodesGreaterThan(String value) {
            addCriterion("change_codes >", value, "changeCodes");
            return (Criteria) this;
        }

        public Criteria andChangeCodesGreaterThanOrEqualTo(String value) {
            addCriterion("change_codes >=", value, "changeCodes");
            return (Criteria) this;
        }

        public Criteria andChangeCodesLessThan(String value) {
            addCriterion("change_codes <", value, "changeCodes");
            return (Criteria) this;
        }

        public Criteria andChangeCodesLessThanOrEqualTo(String value) {
            addCriterion("change_codes <=", value, "changeCodes");
            return (Criteria) this;
        }

        public Criteria andChangeCodesLike(String value) {
            addCriterion("change_codes like", value, "changeCodes");
            return (Criteria) this;
        }

        public Criteria andChangeCodesNotLike(String value) {
            addCriterion("change_codes not like", value, "changeCodes");
            return (Criteria) this;
        }

        public Criteria andChangeCodesIn(List<String> values) {
            addCriterion("change_codes in", values, "changeCodes");
            return (Criteria) this;
        }

        public Criteria andChangeCodesNotIn(List<String> values) {
            addCriterion("change_codes not in", values, "changeCodes");
            return (Criteria) this;
        }

        public Criteria andChangeCodesBetween(String value1, String value2) {
            addCriterion("change_codes between", value1, value2, "changeCodes");
            return (Criteria) this;
        }

        public Criteria andChangeCodesNotBetween(String value1, String value2) {
            addCriterion("change_codes not between", value1, value2, "changeCodes");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNull() {
            addCriterion("remark is null");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNotNull() {
            addCriterion("remark is not null");
            return (Criteria) this;
        }

        public Criteria andRemarkEqualTo(String value) {
            addCriterion("remark =", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotEqualTo(String value) {
            addCriterion("remark <>", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThan(String value) {
            addCriterion("remark >", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("remark >=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThan(String value) {
            addCriterion("remark <", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThanOrEqualTo(String value) {
            addCriterion("remark <=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLike(String value) {
            addCriterion("remark like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotLike(String value) {
            addCriterion("remark not like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkIn(List<String> values) {
            addCriterion("remark in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotIn(List<String> values) {
            addCriterion("remark not in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkBetween(String value1, String value2) {
            addCriterion("remark between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotBetween(String value1, String value2) {
            addCriterion("remark not between", value1, value2, "remark");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table gc_trader_account_change_log
     *
     * @mbggenerated do_not_delete_during_merge
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table gc_trader_account_change_log
     *
     * @mbggenerated
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}