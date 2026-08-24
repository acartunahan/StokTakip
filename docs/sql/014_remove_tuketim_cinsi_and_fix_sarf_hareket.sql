-- Existing target schemas only. Do not run against the legacy schema.
-- Removes the mistakenly introduced new-system table and aligns the target
-- depot movement table with DepoZimmetHareket.

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE TUKETIM_CINSI CASCADE CONSTRAINTS PURGE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE DEPO_ZIMMET_HAREKET ADD (ADET NUMBER)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -1430 THEN
            RAISE;
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE DEPO_ZIMMET_HAREKET ADD (TUR VARCHAR2(20))';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -1430 THEN
            RAISE;
        END IF;
END;
/
