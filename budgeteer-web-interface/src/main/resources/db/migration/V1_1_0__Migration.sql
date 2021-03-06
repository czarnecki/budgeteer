--MUST BE RUN AGAINST PR #196, commit 6660f94 on ORACLE

CREATE TABLE TEMPLATE
(	"ID" NUMBER(19,0) NOT NULL ENABLE,
    "DESCRIPTION" VARCHAR2(512 CHAR),
    "ISDEFAULT" NUMBER(1,0),
    "NAME" VARCHAR2(128 CHAR),
    "PROJECT_ID" NUMBER(19,0),
    "TYPE" NUMBER(10,0),
    "TEMPLATE" BLOB,
     PRIMARY KEY ("ID")
);


CREATE TABLE FORGOT_PASSWORD_TOKEN
(	"ID" NUMBER(19,0) NOT NULL ENABLE,
    "EXPIRY_DATE" TIMESTAMP (6),
    "TOKEN" VARCHAR2(255 CHAR),
    "USER_ID" NUMBER(19,0) NOT NULL ENABLE,
     PRIMARY KEY ("ID")
);

CREATE TABLE VERIFICATION_TOKEN
(
"ID" NUMBER(19,0) NOT NULL ENABLE,
"EXPIRY_DATE" TIMESTAMP (6),
"TOKEN" VARCHAR2(255 CHAR),
"USER_ID" NUMBER(19,0) NOT NULL ENABLE,
 PRIMARY KEY ("ID")
);

ALTER TABLE BUDGETEER_USER
    ADD( MAIL VARCHAR(255),
        MAIL_VERIFIED NUMBER(1,0));

ALTER TABLE BUDGET
    ADD (LIMIT NUMBER(19),
         NOTE CLOB);