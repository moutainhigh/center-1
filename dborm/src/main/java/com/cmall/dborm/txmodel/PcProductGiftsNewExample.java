package com.cmall.dborm.txmodel;

import java.util.ArrayList;
import java.util.List;

public class PcProductGiftsNewExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table pc_product_gifts_new
     *
     * @mbggenerated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table pc_product_gifts_new
     *
     * @mbggenerated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table pc_product_gifts_new
     *
     * @mbggenerated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts_new
     *
     * @mbggenerated
     */
    public PcProductGiftsNewExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts_new
     *
     * @mbggenerated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts_new
     *
     * @mbggenerated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts_new
     *
     * @mbggenerated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts_new
     *
     * @mbggenerated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts_new
     *
     * @mbggenerated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts_new
     *
     * @mbggenerated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts_new
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
     * This method corresponds to the database table pc_product_gifts_new
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
     * This method corresponds to the database table pc_product_gifts_new
     *
     * @mbggenerated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pc_product_gifts_new
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
     * This class corresponds to the database table pc_product_gifts_new
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

        public Criteria andGiftIdIsNull() {
            addCriterion("gift_id is null");
            return (Criteria) this;
        }

        public Criteria andGiftIdIsNotNull() {
            addCriterion("gift_id is not null");
            return (Criteria) this;
        }

        public Criteria andGiftIdEqualTo(String value) {
            addCriterion("gift_id =", value, "giftId");
            return (Criteria) this;
        }

        public Criteria andGiftIdNotEqualTo(String value) {
            addCriterion("gift_id <>", value, "giftId");
            return (Criteria) this;
        }

        public Criteria andGiftIdGreaterThan(String value) {
            addCriterion("gift_id >", value, "giftId");
            return (Criteria) this;
        }

        public Criteria andGiftIdGreaterThanOrEqualTo(String value) {
            addCriterion("gift_id >=", value, "giftId");
            return (Criteria) this;
        }

        public Criteria andGiftIdLessThan(String value) {
            addCriterion("gift_id <", value, "giftId");
            return (Criteria) this;
        }

        public Criteria andGiftIdLessThanOrEqualTo(String value) {
            addCriterion("gift_id <=", value, "giftId");
            return (Criteria) this;
        }

        public Criteria andGiftIdLike(String value) {
            addCriterion("gift_id like", value, "giftId");
            return (Criteria) this;
        }

        public Criteria andGiftIdNotLike(String value) {
            addCriterion("gift_id not like", value, "giftId");
            return (Criteria) this;
        }

        public Criteria andGiftIdIn(List<String> values) {
            addCriterion("gift_id in", values, "giftId");
            return (Criteria) this;
        }

        public Criteria andGiftIdNotIn(List<String> values) {
            addCriterion("gift_id not in", values, "giftId");
            return (Criteria) this;
        }

        public Criteria andGiftIdBetween(String value1, String value2) {
            addCriterion("gift_id between", value1, value2, "giftId");
            return (Criteria) this;
        }

        public Criteria andGiftIdNotBetween(String value1, String value2) {
            addCriterion("gift_id not between", value1, value2, "giftId");
            return (Criteria) this;
        }

        public Criteria andGiftNameIsNull() {
            addCriterion("gift_name is null");
            return (Criteria) this;
        }

        public Criteria andGiftNameIsNotNull() {
            addCriterion("gift_name is not null");
            return (Criteria) this;
        }

        public Criteria andGiftNameEqualTo(String value) {
            addCriterion("gift_name =", value, "giftName");
            return (Criteria) this;
        }

        public Criteria andGiftNameNotEqualTo(String value) {
            addCriterion("gift_name <>", value, "giftName");
            return (Criteria) this;
        }

        public Criteria andGiftNameGreaterThan(String value) {
            addCriterion("gift_name >", value, "giftName");
            return (Criteria) this;
        }

        public Criteria andGiftNameGreaterThanOrEqualTo(String value) {
            addCriterion("gift_name >=", value, "giftName");
            return (Criteria) this;
        }

        public Criteria andGiftNameLessThan(String value) {
            addCriterion("gift_name <", value, "giftName");
            return (Criteria) this;
        }

        public Criteria andGiftNameLessThanOrEqualTo(String value) {
            addCriterion("gift_name <=", value, "giftName");
            return (Criteria) this;
        }

        public Criteria andGiftNameLike(String value) {
            addCriterion("gift_name like", value, "giftName");
            return (Criteria) this;
        }

        public Criteria andGiftNameNotLike(String value) {
            addCriterion("gift_name not like", value, "giftName");
            return (Criteria) this;
        }

        public Criteria andGiftNameIn(List<String> values) {
            addCriterion("gift_name in", values, "giftName");
            return (Criteria) this;
        }

        public Criteria andGiftNameNotIn(List<String> values) {
            addCriterion("gift_name not in", values, "giftName");
            return (Criteria) this;
        }

        public Criteria andGiftNameBetween(String value1, String value2) {
            addCriterion("gift_name between", value1, value2, "giftName");
            return (Criteria) this;
        }

        public Criteria andGiftNameNotBetween(String value1, String value2) {
            addCriterion("gift_name not between", value1, value2, "giftName");
            return (Criteria) this;
        }

        public Criteria andStyleIdIsNull() {
            addCriterion("style_id is null");
            return (Criteria) this;
        }

        public Criteria andStyleIdIsNotNull() {
            addCriterion("style_id is not null");
            return (Criteria) this;
        }

        public Criteria andStyleIdEqualTo(String value) {
            addCriterion("style_id =", value, "styleId");
            return (Criteria) this;
        }

        public Criteria andStyleIdNotEqualTo(String value) {
            addCriterion("style_id <>", value, "styleId");
            return (Criteria) this;
        }

        public Criteria andStyleIdGreaterThan(String value) {
            addCriterion("style_id >", value, "styleId");
            return (Criteria) this;
        }

        public Criteria andStyleIdGreaterThanOrEqualTo(String value) {
            addCriterion("style_id >=", value, "styleId");
            return (Criteria) this;
        }

        public Criteria andStyleIdLessThan(String value) {
            addCriterion("style_id <", value, "styleId");
            return (Criteria) this;
        }

        public Criteria andStyleIdLessThanOrEqualTo(String value) {
            addCriterion("style_id <=", value, "styleId");
            return (Criteria) this;
        }

        public Criteria andStyleIdLike(String value) {
            addCriterion("style_id like", value, "styleId");
            return (Criteria) this;
        }

        public Criteria andStyleIdNotLike(String value) {
            addCriterion("style_id not like", value, "styleId");
            return (Criteria) this;
        }

        public Criteria andStyleIdIn(List<String> values) {
            addCriterion("style_id in", values, "styleId");
            return (Criteria) this;
        }

        public Criteria andStyleIdNotIn(List<String> values) {
            addCriterion("style_id not in", values, "styleId");
            return (Criteria) this;
        }

        public Criteria andStyleIdBetween(String value1, String value2) {
            addCriterion("style_id between", value1, value2, "styleId");
            return (Criteria) this;
        }

        public Criteria andStyleIdNotBetween(String value1, String value2) {
            addCriterion("style_id not between", value1, value2, "styleId");
            return (Criteria) this;
        }

        public Criteria andColorIdIsNull() {
            addCriterion("color_id is null");
            return (Criteria) this;
        }

        public Criteria andColorIdIsNotNull() {
            addCriterion("color_id is not null");
            return (Criteria) this;
        }

        public Criteria andColorIdEqualTo(String value) {
            addCriterion("color_id =", value, "colorId");
            return (Criteria) this;
        }

        public Criteria andColorIdNotEqualTo(String value) {
            addCriterion("color_id <>", value, "colorId");
            return (Criteria) this;
        }

        public Criteria andColorIdGreaterThan(String value) {
            addCriterion("color_id >", value, "colorId");
            return (Criteria) this;
        }

        public Criteria andColorIdGreaterThanOrEqualTo(String value) {
            addCriterion("color_id >=", value, "colorId");
            return (Criteria) this;
        }

        public Criteria andColorIdLessThan(String value) {
            addCriterion("color_id <", value, "colorId");
            return (Criteria) this;
        }

        public Criteria andColorIdLessThanOrEqualTo(String value) {
            addCriterion("color_id <=", value, "colorId");
            return (Criteria) this;
        }

        public Criteria andColorIdLike(String value) {
            addCriterion("color_id like", value, "colorId");
            return (Criteria) this;
        }

        public Criteria andColorIdNotLike(String value) {
            addCriterion("color_id not like", value, "colorId");
            return (Criteria) this;
        }

        public Criteria andColorIdIn(List<String> values) {
            addCriterion("color_id in", values, "colorId");
            return (Criteria) this;
        }

        public Criteria andColorIdNotIn(List<String> values) {
            addCriterion("color_id not in", values, "colorId");
            return (Criteria) this;
        }

        public Criteria andColorIdBetween(String value1, String value2) {
            addCriterion("color_id between", value1, value2, "colorId");
            return (Criteria) this;
        }

        public Criteria andColorIdNotBetween(String value1, String value2) {
            addCriterion("color_id not between", value1, value2, "colorId");
            return (Criteria) this;
        }

        public Criteria andEventIdIsNull() {
            addCriterion("event_id is null");
            return (Criteria) this;
        }

        public Criteria andEventIdIsNotNull() {
            addCriterion("event_id is not null");
            return (Criteria) this;
        }

        public Criteria andEventIdEqualTo(String value) {
            addCriterion("event_id =", value, "eventId");
            return (Criteria) this;
        }

        public Criteria andEventIdNotEqualTo(String value) {
            addCriterion("event_id <>", value, "eventId");
            return (Criteria) this;
        }

        public Criteria andEventIdGreaterThan(String value) {
            addCriterion("event_id >", value, "eventId");
            return (Criteria) this;
        }

        public Criteria andEventIdGreaterThanOrEqualTo(String value) {
            addCriterion("event_id >=", value, "eventId");
            return (Criteria) this;
        }

        public Criteria andEventIdLessThan(String value) {
            addCriterion("event_id <", value, "eventId");
            return (Criteria) this;
        }

        public Criteria andEventIdLessThanOrEqualTo(String value) {
            addCriterion("event_id <=", value, "eventId");
            return (Criteria) this;
        }

        public Criteria andEventIdLike(String value) {
            addCriterion("event_id like", value, "eventId");
            return (Criteria) this;
        }

        public Criteria andEventIdNotLike(String value) {
            addCriterion("event_id not like", value, "eventId");
            return (Criteria) this;
        }

        public Criteria andEventIdIn(List<String> values) {
            addCriterion("event_id in", values, "eventId");
            return (Criteria) this;
        }

        public Criteria andEventIdNotIn(List<String> values) {
            addCriterion("event_id not in", values, "eventId");
            return (Criteria) this;
        }

        public Criteria andEventIdBetween(String value1, String value2) {
            addCriterion("event_id between", value1, value2, "eventId");
            return (Criteria) this;
        }

        public Criteria andEventIdNotBetween(String value1, String value2) {
            addCriterion("event_id not between", value1, value2, "eventId");
            return (Criteria) this;
        }

        public Criteria andGiftCdIsNull() {
            addCriterion("gift_cd is null");
            return (Criteria) this;
        }

        public Criteria andGiftCdIsNotNull() {
            addCriterion("gift_cd is not null");
            return (Criteria) this;
        }

        public Criteria andGiftCdEqualTo(String value) {
            addCriterion("gift_cd =", value, "giftCd");
            return (Criteria) this;
        }

        public Criteria andGiftCdNotEqualTo(String value) {
            addCriterion("gift_cd <>", value, "giftCd");
            return (Criteria) this;
        }

        public Criteria andGiftCdGreaterThan(String value) {
            addCriterion("gift_cd >", value, "giftCd");
            return (Criteria) this;
        }

        public Criteria andGiftCdGreaterThanOrEqualTo(String value) {
            addCriterion("gift_cd >=", value, "giftCd");
            return (Criteria) this;
        }

        public Criteria andGiftCdLessThan(String value) {
            addCriterion("gift_cd <", value, "giftCd");
            return (Criteria) this;
        }

        public Criteria andGiftCdLessThanOrEqualTo(String value) {
            addCriterion("gift_cd <=", value, "giftCd");
            return (Criteria) this;
        }

        public Criteria andGiftCdLike(String value) {
            addCriterion("gift_cd like", value, "giftCd");
            return (Criteria) this;
        }

        public Criteria andGiftCdNotLike(String value) {
            addCriterion("gift_cd not like", value, "giftCd");
            return (Criteria) this;
        }

        public Criteria andGiftCdIn(List<String> values) {
            addCriterion("gift_cd in", values, "giftCd");
            return (Criteria) this;
        }

        public Criteria andGiftCdNotIn(List<String> values) {
            addCriterion("gift_cd not in", values, "giftCd");
            return (Criteria) this;
        }

        public Criteria andGiftCdBetween(String value1, String value2) {
            addCriterion("gift_cd between", value1, value2, "giftCd");
            return (Criteria) this;
        }

        public Criteria andGiftCdNotBetween(String value1, String value2) {
            addCriterion("gift_cd not between", value1, value2, "giftCd");
            return (Criteria) this;
        }

        public Criteria andSellerCodeIsNull() {
            addCriterion("seller_code is null");
            return (Criteria) this;
        }

        public Criteria andSellerCodeIsNotNull() {
            addCriterion("seller_code is not null");
            return (Criteria) this;
        }

        public Criteria andSellerCodeEqualTo(String value) {
            addCriterion("seller_code =", value, "sellerCode");
            return (Criteria) this;
        }

        public Criteria andSellerCodeNotEqualTo(String value) {
            addCriterion("seller_code <>", value, "sellerCode");
            return (Criteria) this;
        }

        public Criteria andSellerCodeGreaterThan(String value) {
            addCriterion("seller_code >", value, "sellerCode");
            return (Criteria) this;
        }

        public Criteria andSellerCodeGreaterThanOrEqualTo(String value) {
            addCriterion("seller_code >=", value, "sellerCode");
            return (Criteria) this;
        }

        public Criteria andSellerCodeLessThan(String value) {
            addCriterion("seller_code <", value, "sellerCode");
            return (Criteria) this;
        }

        public Criteria andSellerCodeLessThanOrEqualTo(String value) {
            addCriterion("seller_code <=", value, "sellerCode");
            return (Criteria) this;
        }

        public Criteria andSellerCodeLike(String value) {
            addCriterion("seller_code like", value, "sellerCode");
            return (Criteria) this;
        }

        public Criteria andSellerCodeNotLike(String value) {
            addCriterion("seller_code not like", value, "sellerCode");
            return (Criteria) this;
        }

        public Criteria andSellerCodeIn(List<String> values) {
            addCriterion("seller_code in", values, "sellerCode");
            return (Criteria) this;
        }

        public Criteria andSellerCodeNotIn(List<String> values) {
            addCriterion("seller_code not in", values, "sellerCode");
            return (Criteria) this;
        }

        public Criteria andSellerCodeBetween(String value1, String value2) {
            addCriterion("seller_code between", value1, value2, "sellerCode");
            return (Criteria) this;
        }

        public Criteria andSellerCodeNotBetween(String value1, String value2) {
            addCriterion("seller_code not between", value1, value2, "sellerCode");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(String value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(String value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(String value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(String value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(String value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(String value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLike(String value) {
            addCriterion("update_time like", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotLike(String value) {
            addCriterion("update_time not like", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<String> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<String> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(String value1, String value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(String value1, String value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table pc_product_gifts_new
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
     * This class corresponds to the database table pc_product_gifts_new
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