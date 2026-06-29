# SQL dosya sirasi

Yeni hedef sema kurulumu icin:

1. `001_create_ortak_kategori.sql`
2. `003_create_missing_target_schema.sql`
3. `004_create_envanter_detay.sql`

Veri/backfill amacli opsiyonel dosyalar:

- `002_migrate_ortak_kategori.sql`: Kaynak `SARF_KATEGORI` ve `RESAT_SABIT_KOD` tablolarina ayni baglamdan erisilebiliyorsa kullanilir.
- `005_backfill_envanter_detay.sql`: Daha once satin alma kolonlari `ENVANTER` uzerindeyken olusmus hedef semalarda `ENVANTER_DETAY` doldurmak icin kullanilir. Yeni kurulumda calistirilmaz.
- `006_add_legacy_table_name_to_migration_audit.sql`: Daha once audit tablolari olusmus semalarda `ESKI_TABLO_ADI` kolonlarini eklemek icin kullanilir.
