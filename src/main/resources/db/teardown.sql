SET REFERENTIAL_INTEGRITY FALSE;    -- 모든 제약 조건 강제 해제
truncate table member_tb;
SET REFERENTIAL_INTEGRITY TRUE;
