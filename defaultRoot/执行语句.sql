信息表以及信息附件表做出的更改（添加THIRD_ID,THIRD_NAME二个字段）
alter table oa_information add (THIRD_ID NUMBER(20,0));
alter table oa_information add (THIRD_NAME varchar2(30));
alter table oa_informationaccessory add (THIRD_ID NUMBER(20,0));
alter table oa_informationaccessory add (THIRD_NAME varchar2(30));

insert into org_right
values
(hibernate_sequence.nextval,
   '查询',
   '个人状态查询',
   '个人办公',
   '1',
   '11110',
   '全部/本人/本组织及下属组织/本组织/自定义',
   'ztcx*01*01',
   '0');
   

insert into whir$portswitch
values 
(hibernate_sequence.nextval,
	
)


create sequence SEQ_whir$mhxtyxjc
minvalue 1
maxvalue 99999999
start with 1
increment by 1
nocache
order;


create sequence SEQ_QD_SUPPORTTOOL
minvalue 1
maxvalue 99999999
start with 1
increment by 1
nocache
order;