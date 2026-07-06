-- Existing target-schema migration: move target ENVANTER_BARKOD data into ENVANTER.BARKOD.
-- Fresh installs do not need this file because 003_create_missing_target_schema.sql already creates ENVANTER.BARKOD.

DECLARE
    v_column_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_column_count
    FROM USER_TAB_COLUMNS
    WHERE TABLE_NAME = 'ENVANTER'
      AND COLUMN_NAME = 'BARKOD';

    IF v_column_count = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE ENVANTER ADD (BARKOD VARCHAR2(100))';
    END IF;
END;
/

DECLARE
    v_table_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_table_count
    FROM USER_TABLES
    WHERE TABLE_NAME = 'ENVANTER_BARKOD';

    IF v_table_count > 0 THEN
        EXECUTE IMMEDIATE '
            MERGE INTO ENVANTER e
            USING (
                SELECT R_DEMIRBASGUID, MIN(BARKOD) AS BARKOD
                FROM ENVANTER_BARKOD
                WHERE R_DEMIRBASGUID IS NOT NULL
                  AND BARKOD IS NOT NULL
                GROUP BY R_DEMIRBASGUID
            ) b
            ON (e.GUID = b.R_DEMIRBASGUID)
            WHEN MATCHED THEN UPDATE
                SET e.BARKOD = b.BARKOD
                WHERE e.BARKOD IS NULL';

        EXECUTE IMMEDIATE 'DROP TABLE ENVANTER_BARKOD CASCADE CONSTRAINTS';
    END IF;
END;
/
