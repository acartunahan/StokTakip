# SQL dosya sirasi

Yeni hedef sema kurulumu icin:

1. `001_create_ortak_kategori.sql`
2. `003_create_missing_target_schema.sql`
3. `004_create_envanter_detay.sql`

Veri/backfill amacli opsiyonel dosyalar:

- `002_migrate_ortak_kategori.sql`: Kaynak `SARF_KATEGORI` ve `RESAT_SABIT_KOD` tablolarina ayni baglamdan erisilebiliyorsa kullanilir.
- `005_backfill_envanter_detay.sql`: Daha once satin alma kolonlari `ENVANTER` uzerindeyken olusmus hedef semalarda `ENVANTER_DETAY` doldurmak icin kullanilir. Yeni kurulumda calistirilmaz.
- `006_add_legacy_table_name_to_migration_audit.sql`: Daha once audit tablolari olusmus semalarda `ESKI_TABLO_ADI` kolonlarini eklemek icin kullanilir.

Legacy test verisi icin:

1. `007_create_legacy_test_schema.sql`: Sadece bos/local legacy test semasinda minimal kaynak tablolarini olusturur. Gercek legacy DB'de calistirilmaz.
2. `008_seed_legacy_test_data.sql`: Migration'i test etmek icin tum legacy kaynak tablolarina iliskili demo veri basar. Tekrar calistirilabilir `MERGE` ifadeleri kullanir.

Docker Compose ile gelen local Oracle legacy container'inda calistirma ornegi:

```powershell
docker compose up -d oracle-legacy oracle-target
Get-Content docs\sql\007_create_legacy_test_schema.sql | docker exec -i oracle-legacy sqlplus -S legacy_readonly/legacy_readonly@//localhost:1521/FREEPDB1
Get-Content docs\sql\008_seed_legacy_test_data.sql | docker exec -i oracle-legacy sqlplus -S legacy_readonly/legacy_readonly@//localhost:1521/FREEPDB1
```

Migration'i kuru calistirmak icin:

```powershell
$env:APP_MIGRATION_ENABLED="true"
$env:APP_MIGRATION_DRY_RUN="true"
& "C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.1.1\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run
```

Hedef DB'ye gercek yazmak icin `APP_MIGRATION_DRY_RUN` degerini `"false"` yap.
