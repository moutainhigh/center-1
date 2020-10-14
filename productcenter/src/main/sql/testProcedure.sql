
set @outFlag='';
set @error ='';
set @detailStr='sku1$5;sku1$5';
set @productsplit=';';
set @itemsplit='$';
set @flag=0;

CALL proc_sku_stock(@outFlag,@error,@detailStr,@productsplit,@itemsplit,@flag);
SELECT @outFlag,@error;


TRUNCATE TABLE pc_productinfo;
TRUNCATE TABLE pc_productdescription;
TRUNCATE TABLE pc_productproperty;
TRUNCATE TABLE pc_skupic;
TRUNCATE TABLE pc_skuinfo;
TRUNCATE TABLE pc_productpic;
TRUNCATE TABLE pc_productcategory_rel;
TRUNCATE TABLE pc_productflow;

delete from  pc_productinfo where product_code='P00000001';
				delete from  pc_productdescription where product_code='P00000001';
				delete from  pc_productproperty where product_code='P00000001';
				SELECT * from  pc_skupic where product_code='P00000001';,
				delete from  pc_skuinfo where product_code='P00000001';
				delete from  pc_productpic where product_code='P00000001';
				delete from  pc_productcategory_rel where product_code='P00000001';


				
				
				

set @outFlag='';
set @error='';
set @productCode='P00000002';
set @produtName='produtName';
set @sellerCode='sellerCode';
set @brandCode='brandCode';
set @productWeight=10;
set @flagSale=1;
set @_catogetyId='44971603000200010001';
set @_description='abcd';
set @picUrlStr='';
set @productPropertyStr=''; -- propertyNameCode$propertyCode$propertyName$propertyValue$propertyType;
set @skuStr='SK130912100116$#$11.0$#$10.0$#$12$#$449746200001=4497462000010001&449746200002=4497462000020001$#$颜色=红色&型号=A型$#$sku1url$#$sku1name$#$sku1productcode$#$10$#$adv##SK130912100117$#$11.0$#$10.0$#$12$#$449746200001=4497462000010001&449746200002=4497462000020002$#$颜色=红色&型号=B型$#$sku2url$#$sku2name$#$sku2productcode$#$10$#$adv1';          --  skuCode$sellPrice$marketPrice$stockNum$skuKey$skuValue;
set @productsplit='##';
set @itemsplit='$#$';
set @flag=0;
set @_keyword='keyword';
set @flowStatus='0';
set @flowCode='aaa';
set @productJson='ssss';
set @_updator='bbb';
-- set @skuPicStr='aa';

set @productVolume=21;
set @transportTemplate='transportTemplate';
set @sellProductCode='sellProductCode';
set @mainpicurl='mainpicurl';

set @_labels ='';
set @_flagPayWay=0;
set @_productVolumeItem='';
set @productStatus='4497153900060002';


CALL proc_add_product(@outFlag,@error,@productCode,@produtName,@sellerCode,@brandCode,@productWeight,@flagSale,@_catogetyId,@_description,@picUrlStr,@productPropertyStr,@skuStr,@productsplit,@itemsplit,
@flag,@_keyword,@flowStatus,@flowCode,@productJson,@_updator,0,0,0,@productVolume,@transportTemplate,@sellProductCode,@mainpicurl,@_labels,@_flagPayWay,@_productVolumeItem,@productStatus);

SELECT @outFlag,@error