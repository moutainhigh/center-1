package com.cmall.dborm.txmodel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OcOrderActivityExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table oc_order_activity
     *
     * @mbggenerated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table oc_order_activity
     *
     * @mbggenerated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table oc_order_activity
     *
     * @mbggenerated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_order_activity
     *
     * @mbggenerated
     */
    public OcOrderActivityExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_order_activity
     *
     * @mbggenerated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_order_activity
     *
     * @mbggenerated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_order_activity
     *
     * @mbggenerated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_order_activity
     *
     * @mbggenerated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_order_activity
     *
     * @mbggenerated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_order_activity
     *
     * @mbggenerated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_order_activity
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
     * This method corresponds to the database table oc_order_activity
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
     * This method corresponds to the database table oc_order_activity
     *
     * @mbggenerated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oc_order_activity
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
     * This class corresponds to the database table oc_order_activity
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

        public Criteria andOrderCodeIsNull() {
            addCriterion("order_code is null");
            return (Criteria) this;
        }

        public Criteria andOrderCodeIsNotNull() {
            addCriterion("order_code is not null");
            return (Criteria) this;
        }

        public Criteria andOrderCodeEqualTo(String value) {
            addCriterion("order_code =", value, "orderCode");
            return (Criteria) this;
        }

        public Criteria andOrderCodeNotEqualTo(String value) {
            addCriterion("order_code <>", value, "orderCode");
            return (Criteria) this;
        }

        public Criteria andOrderCodeGreaterThan(String value) {
            addCriterion("order_code >", value, "orderCode");
            return (Criteria) this;
        }

        public Criteria andOrderCodeGreaterThanOrEqualTo(String value) {
            addCriterion("order_code >=", value, "orderCode");
            return (Criteria) this;
        }

        public Criteria andOrderCodeLessThan(String value) {
            addCriterion("order_code <", value, "orderCode");
            return (Criteria) this;
        }

        public Criteria andOrderCodeLessThanOrEqualTo(String value) {
            addCriterion("order_code <=", value, "orderCode");
            return (Criteria) this;
        }

        public Criteria andOrderCodeLike(String value) {
            addCriterion("order_code like", value, "orderCode");
            return (Criteria) this;
        }

        public Criteria andOrderCodeNotLike(String value) {
            addCriterion("order_code not like", value, "orderCode");
            return (Criteria) this;
        }

        public Criteria andOrderCodeIn(List<String> values) {
            addCriterion("order_code in", values, "orderCode");
            return (Criteria) this;
        }

        public Criteria andOrderCodeNotIn(List<String> values) {
            addCriterion("order_code not in", values, "orderCode");
            return (Criteria) this;
        }

        public Criteria andOrderCodeBetween(String value1, String value2) {
            addCriterion("order_code between", value1, value2, "orderCode");
            return (Criteria) this;
        }

        public Criteria andOrderCodeNotBetween(String value1, String value2) {
            addCriterion("order_code not between", value1, value2, "orderCode");
            return (Criteria) this;
        }

        public Criteria andProductCodeIsNull() {
            addCriterion("product_code is null");
            return (Criteria) this;
        }

        public Criteria andProductCodeIsNotNull() {
            addCriterion("product_code is not null");
            return (Criteria) this;
        }

        public Criteria andProductCodeEqualTo(String value) {
            addCriterion("product_code =", value, "productCode");
            return (Criteria) this;
        }

        public Criteria andProductCodeNotEqualTo(String value) {
            addCriterion("product_code <>", value, "productCode");
            return (Criteria) this;
        }

        public Criteria andProductCodeGreaterThan(String value) {
            addCriterion("product_code >", value, "productCode");
            return (Criteria) this;
        }

        public Criteria andProductCodeGreaterThanOrEqualTo(String value) {
            addCriterion("product_code >=", value, "productCode");
            return (Criteria) this;
        }

        public Criteria andProductCodeLessThan(String value) {
            addCriterion("product_code <", value, "productCode");
            return (Criteria) this;
        }

        public Criteria andProductCodeLessThanOrEqualTo(String value) {
            addCriterion("product_code <=", value, "productCode");
            return (Criteria) this;
        }

        public Criteria andProductCodeLike(String value) {
            addCriterion("product_code like", value, "productCode");
            return (Criteria) this;
        }

        public Criteria andProductCodeNotLike(String value) {
            addCriterion("product_code not like", value, "productCode");
            return (Criteria) this;
        }

        public Criteria andProductCodeIn(List<String> values) {
            addCriterion("product_code in", values, "productCode");
            return (Criteria) this;
        }

        public Criteria andProductCodeNotIn(List<String> values) {
            addCriterion("product_code not in", values, "productCode");
            return (Criteria) this;
        }

        public Criteria andProductCodeBetween(String value1, String value2) {
            addCriterion("product_code between", value1, value2, "productCode");
            return (Criteria) this;
        }

        public Criteria andProductCodeNotBetween(String value1, String value2) {
            addCriterion("product_code not between", value1, value2, "productCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeIsNull() {
            addCriterion("sku_code is null");
            return (Criteria) this;
        }

        public Criteria andSkuCodeIsNotNull() {
            addCriterion("sku_code is not null");
            return (Criteria) this;
        }

        public Criteria andSkuCodeEqualTo(String value) {
            addCriterion("sku_code =", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeNotEqualTo(String value) {
            addCriterion("sku_code <>", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeGreaterThan(String value) {
            addCriterion("sku_code >", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeGreaterThanOrEqualTo(String value) {
            addCriterion("sku_code >=", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeLessThan(String value) {
            addCriterion("sku_code <", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeLessThanOrEqualTo(String value) {
            addCriterion("sku_code <=", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeLike(String value) {
            addCriterion("sku_code like", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeNotLike(String value) {
            addCriterion("sku_code not like", value, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeIn(List<String> values) {
            addCriterion("sku_code in", values, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeNotIn(List<String> values) {
            addCriterion("sku_code not in", values, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeBetween(String value1, String value2) {
            addCriterion("sku_code between", value1, value2, "skuCode");
            return (Criteria) this;
        }

        public Criteria andSkuCodeNotBetween(String value1, String value2) {
            addCriterion("sku_code not between", value1, value2, "skuCode");
            return (Criteria) this;
        }

        public Criteria andPreferentialMoneyIsNull() {
            addCriterion("preferential_money is null");
            return (Criteria) this;
        }

        public Criteria andPreferentialMoneyIsNotNull() {
            addCriterion("preferential_money is not null");
            return (Criteria) this;
        }

        public Criteria andPreferentialMoneyEqualTo(BigDecimal value) {
            addCriterion("preferential_money =", value, "preferentialMoney");
            return (Criteria) this;
        }

        public Criteria andPreferentialMoneyNotEqualTo(BigDecimal value) {
            addCriterion("preferential_money <>", value, "preferentialMoney");
            return (Criteria) this;
        }

        public Criteria andPreferentialMoneyGreaterThan(BigDecimal value) {
            addCriterion("preferential_money >", value, "preferentialMoney");
            return (Criteria) this;
        }

        public Criteria andPreferentialMoneyGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("preferential_money >=", value, "preferentialMoney");
            return (Criteria) this;
        }

        public Criteria andPreferentialMoneyLessThan(BigDecimal value) {
            addCriterion("preferential_money <", value, "preferentialMoney");
            return (Criteria) this;
        }

        public Criteria andPreferentialMoneyLessThanOrEqualTo(BigDecimal value) {
            addCriterion("preferential_money <=", value, "preferentialMoney");
            return (Criteria) this;
        }

        public Criteria andPreferentialMoneyIn(List<BigDecimal> values) {
            addCriterion("preferential_money in", values, "preferentialMoney");
            return (Criteria) this;
        }

        public Criteria andPreferentialMoneyNotIn(List<BigDecimal> values) {
            addCriterion("preferential_money not in", values, "preferentialMoney");
            return (Criteria) this;
        }

        public Criteria andPreferentialMoneyBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("preferential_money between", value1, value2, "preferentialMoney");
            return (Criteria) this;
        }

        public Criteria andPreferentialMoneyNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("preferential_money not between", value1, value2, "preferentialMoney");
            return (Criteria) this;
        }

        public Criteria andActivityCodeIsNull() {
            addCriterion("activity_code is null");
            return (Criteria) this;
        }

        public Criteria andActivityCodeIsNotNull() {
            addCriterion("activity_code is not null");
            return (Criteria) this;
        }

        public Criteria andActivityCodeEqualTo(String value) {
            addCriterion("activity_code =", value, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeNotEqualTo(String value) {
            addCriterion("activity_code <>", value, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeGreaterThan(String value) {
            addCriterion("activity_code >", value, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeGreaterThanOrEqualTo(String value) {
            addCriterion("activity_code >=", value, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeLessThan(String value) {
            addCriterion("activity_code <", value, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeLessThanOrEqualTo(String value) {
            addCriterion("activity_code <=", value, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeLike(String value) {
            addCriterion("activity_code like", value, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeNotLike(String value) {
            addCriterion("activity_code not like", value, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeIn(List<String> values) {
            addCriterion("activity_code in", values, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeNotIn(List<String> values) {
            addCriterion("activity_code not in", values, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeBetween(String value1, String value2) {
            addCriterion("activity_code between", value1, value2, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityCodeNotBetween(String value1, String value2) {
            addCriterion("activity_code not between", value1, value2, "activityCode");
            return (Criteria) this;
        }

        public Criteria andActivityTypeIsNull() {
            addCriterion("activity_type is null");
            return (Criteria) this;
        }

        public Criteria andActivityTypeIsNotNull() {
            addCriterion("activity_type is not null");
            return (Criteria) this;
        }

        public Criteria andActivityTypeEqualTo(String value) {
            addCriterion("activity_type =", value, "activityType");
            return (Criteria) this;
        }

        public Criteria andActivityTypeNotEqualTo(String value) {
            addCriterion("activity_type <>", value, "activityType");
            return (Criteria) this;
        }

        public Criteria andActivityTypeGreaterThan(String value) {
            addCriterion("activity_type >", value, "activityType");
            return (Criteria) this;
        }

        public Criteria andActivityTypeGreaterThanOrEqualTo(String value) {
            addCriterion("activity_type >=", value, "activityType");
            return (Criteria) this;
        }

        public Criteria andActivityTypeLessThan(String value) {
            addCriterion("activity_type <", value, "activityType");
            return (Criteria) this;
        }

        public Criteria andActivityTypeLessThanOrEqualTo(String value) {
            addCriterion("activity_type <=", value, "activityType");
            return (Criteria) this;
        }

        public Criteria andActivityTypeLike(String value) {
            addCriterion("activity_type like", value, "activityType");
            return (Criteria) this;
        }

        public Criteria andActivityTypeNotLike(String value) {
            addCriterion("activity_type not like", value, "activityType");
            return (Criteria) this;
        }

        public Criteria andActivityTypeIn(List<String> values) {
            addCriterion("activity_type in", values, "activityType");
            return (Criteria) this;
        }

        public Criteria andActivityTypeNotIn(List<String> values) {
            addCriterion("activity_type not in", values, "activityType");
            return (Criteria) this;
        }

        public Criteria andActivityTypeBetween(String value1, String value2) {
            addCriterion("activity_type between", value1, value2, "activityType");
            return (Criteria) this;
        }

        public Criteria andActivityTypeNotBetween(String value1, String value2) {
            addCriterion("activity_type not between", value1, value2, "activityType");
            return (Criteria) this;
        }

        public Criteria andOutActiveCodeIsNull() {
            addCriterion("out_active_code is null");
            return (Criteria) this;
        }

        public Criteria andOutActiveCodeIsNotNull() {
            addCriterion("out_active_code is not null");
            return (Criteria) this;
        }

        public Criteria andOutActiveCodeEqualTo(String value) {
            addCriterion("out_active_code =", value, "outActiveCode");
            return (Criteria) this;
        }

        public Criteria andOutActiveCodeNotEqualTo(String value) {
            addCriterion("out_active_code <>", value, "outActiveCode");
            return (Criteria) this;
        }

        public Criteria andOutActiveCodeGreaterThan(String value) {
            addCriterion("out_active_code >", value, "outActiveCode");
            return (Criteria) this;
        }

        public Criteria andOutActiveCodeGreaterThanOrEqualTo(String value) {
            addCriterion("out_active_code >=", value, "outActiveCode");
            return (Criteria) this;
        }

        public Criteria andOutActiveCodeLessThan(String value) {
            addCriterion("out_active_code <", value, "outActiveCode");
            return (Criteria) this;
        }

        public Criteria andOutActiveCodeLessThanOrEqualTo(String value) {
            addCriterion("out_active_code <=", value, "outActiveCode");
            return (Criteria) this;
        }

        public Criteria andOutActiveCodeLike(String value) {
            addCriterion("out_active_code like", value, "outActiveCode");
            return (Criteria) this;
        }

        public Criteria andOutActiveCodeNotLike(String value) {
            addCriterion("out_active_code not like", value, "outActiveCode");
            return (Criteria) this;
        }

        public Criteria andOutActiveCodeIn(List<String> values) {
            addCriterion("out_active_code in", values, "outActiveCode");
            return (Criteria) this;
        }

        public Criteria andOutActiveCodeNotIn(List<String> values) {
            addCriterion("out_active_code not in", values, "outActiveCode");
            return (Criteria) this;
        }

        public Criteria andOutActiveCodeBetween(String value1, String value2) {
            addCriterion("out_active_code between", value1, value2, "outActiveCode");
            return (Criteria) this;
        }

        public Criteria andOutActiveCodeNotBetween(String value1, String value2) {
            addCriterion("out_active_code not between", value1, value2, "outActiveCode");
            return (Criteria) this;
        }

        public Criteria andTicketCodeIsNull() {
            addCriterion("ticket_code is null");
            return (Criteria) this;
        }

        public Criteria andTicketCodeIsNotNull() {
            addCriterion("ticket_code is not null");
            return (Criteria) this;
        }

        public Criteria andTicketCodeEqualTo(String value) {
            addCriterion("ticket_code =", value, "ticketCode");
            return (Criteria) this;
        }

        public Criteria andTicketCodeNotEqualTo(String value) {
            addCriterion("ticket_code <>", value, "ticketCode");
            return (Criteria) this;
        }

        public Criteria andTicketCodeGreaterThan(String value) {
            addCriterion("ticket_code >", value, "ticketCode");
            return (Criteria) this;
        }

        public Criteria andTicketCodeGreaterThanOrEqualTo(String value) {
            addCriterion("ticket_code >=", value, "ticketCode");
            return (Criteria) this;
        }

        public Criteria andTicketCodeLessThan(String value) {
            addCriterion("ticket_code <", value, "ticketCode");
            return (Criteria) this;
        }

        public Criteria andTicketCodeLessThanOrEqualTo(String value) {
            addCriterion("ticket_code <=", value, "ticketCode");
            return (Criteria) this;
        }

        public Criteria andTicketCodeLike(String value) {
            addCriterion("ticket_code like", value, "ticketCode");
            return (Criteria) this;
        }

        public Criteria andTicketCodeNotLike(String value) {
            addCriterion("ticket_code not like", value, "ticketCode");
            return (Criteria) this;
        }

        public Criteria andTicketCodeIn(List<String> values) {
            addCriterion("ticket_code in", values, "ticketCode");
            return (Criteria) this;
        }

        public Criteria andTicketCodeNotIn(List<String> values) {
            addCriterion("ticket_code not in", values, "ticketCode");
            return (Criteria) this;
        }

        public Criteria andTicketCodeBetween(String value1, String value2) {
            addCriterion("ticket_code between", value1, value2, "ticketCode");
            return (Criteria) this;
        }

        public Criteria andTicketCodeNotBetween(String value1, String value2) {
            addCriterion("ticket_code not between", value1, value2, "ticketCode");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table oc_order_activity
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
     * This class corresponds to the database table oc_order_activity
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