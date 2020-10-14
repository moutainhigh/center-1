set @outFlag='';
set @error ='';
set @detailStr='1$5;2';
set @productsplit=';';
set @itemsplit='$';
set @orderCode ='DD00';
set @changeType='1';
set @createUser ='system';

CALL proc_sku_stockchange_log_add(@outFlag,@error,@detailStr,@productsplit,@itemsplit,@orderCode,@changeType,@createUser);
SELECT @outFlag,@error;


-- TRUNCATE TABLE oc_orderadress;
-- TRUNCATE TABLE oc_orderdetail;
-- TRUNCATE TABLE oc_orderinfo;



set @outFlag='';
set @error='';
set @orderCode='DD5013110400166'; 
set @orderSource='orderSource';
set @orderType='orderType'; 
set @orderStatus='orderStatus';
set @sellerCode='sellerCode'; 
set @buyerCode='buyerCode';
set @payType='paytype'; 
set @sendType='sendtype';
set @productMoney=20;
set @transportMoney=15;
set @promotionMoney=10;
set @orderMoney=45;
set @payedMoney=45;
set @createTime=''; 
set @updateTime='';
set @areaCode='areacode'; 
set @_address='adress';
set @postCode='postcode'; 
set @_mobilephone='mobilephone';
set @_telephone='telephone'; 
set @receivePerson='receiveperson'; 
set @_email='email';
set @invoiceTitle='invoicetitle'; 
set @flagInvoice='1';
set @_remark='remark';
set @detailStr='sku1$10$1$p1$p1TestName$productpicurl;sku2$20$2$p1$p1TestName$productpicurl';
set @activityStr='productCode1$skuCode1$activityCode1$activityType1$10;productCode2$skuCode2$activityCode2$activityType2$20';
set @freeTransportMoney=10;
set @dueMoney=20;
set @payStr='sequenceid1$paytype1$10$payremark1;sequenceid2$paytype2$20$payremark2';
set @invoiceType='invoiceType';
set @invoiceContent='invoiceContent';
set @productsplit=';';
set @itemsplit='$';

CALL proc_create_order(@outFlag,@error,@orderCode,@orderSource,@orderType,@orderStatus,@sellerCode,@buyerCode,@payType,@sendType,
@productMoney,@transportMoney,@promotionMoney,@orderMoney,@payedMoney,@createTime,@updateTime,@areaCode,@_address,@postCode,
@_mobilephone,@_telephone,@receivePerson,@_email,@invoiceTitle,@flagInvoice,@_remark,@detailStr,@activityStr,@freeTransportMoney,@dueMoney,@payStr,@invoiceType,@invoiceContent,@productsplit,@itemsplit);

SELECT @outFlag,@error;