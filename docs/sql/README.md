# SQL dosya sirasi

Yeni hedef sema kurulumu icin:

1. `001_create_kategori.sql`
2. `003_create_missing_target_schema.sql`
3. `004_create_envanter_detay.sql`

Veri/backfill amacli opsiyonel dosyalar:

- `002_migrate_kategori.sql`: Kaynak `SARF_KATEGORI` ve `RESAT_SABIT_KOD` tablolarina ayni baglamdan erisilebiliyorsa kullanilir.
- `005_backfill_envanter_detay.sql`: Daha once satin alma kolonlari `ENVANTER` uzerindeyken olusmus hedef semalarda `ENVANTER_DETAY` doldurmak icin kullanilir. Yeni kurulumda calistirilmaz.
- `006_add_legacy_table_name_to_migration_audit.sql`: Daha once audit tablolari olusmus semalarda `ESKI_TABLO_ADI` kolonlarini eklemek icin kullanilir.
- `009_move_envanter_barkod_to_envanter.sql`: Daha once `ENVANTER_BARKOD` hedef tablosu olusmus semalarda barkod degerini `ENVANTER.BARKOD` kolonuna tasir ve eski hedef tabloyu kaldirir. Yeni kurulumda calistirilmaz.

Legacy test verisi icin:

1. `007_create_legacy_test_schema.sql`: Sadece bos/local legacy test semasinda minimal kaynak tablolarini olusturur. Gercek legacy DB'de calistirilmaz.
2. `008_seed_legacy_test_data.sql`: Migration'i test etmek icin tum legacy kaynak tablolarina iliskili demo veri basar. Tekrar calistirilabilir `MERGE` ifadeleri kullanir.

Docker Compose ile gelen local Oracle container'larini ayaga kaldirma:

```bash
docker compose up -d oracle-legacy oracle-target
```

Legacy test semasina tablo ve demo veri basma:

```bash
cat docs/sql/007_create_legacy_test_schema.sql | docker exec -i oracle-legacy sqlplus -S legacy_readonly/legacy_readonly@//localhost:1521/FREEPDB1
cat docs/sql/008_seed_legacy_test_data.sql | docker exec -i oracle-legacy sqlplus -S legacy_readonly/legacy_readonly@//localhost:1521/FREEPDB1
```

Not: `docker exec` container icinden calistigi icin legacy baglantisinda `1521` kullanilir. Uygulama Ubuntu host'undan baglanirken legacy host portu `1522`, hedef stoktakip host portu `1521` olmalidir.

Migration'i hedef DB'ye gercek yazacak sekilde calistirma:

```bash
mvn spring-boot:run \
  -Dspring-boot.run.arguments="--spring.main.web-application-type=none --app.migration.enabled=true --app.migration.dry-run=false --spring.datasource.url=jdbc:oracle:thin:@//localhost:1521/FREEPDB1 --spring.datasource.username=stoktakip --spring.datasource.password=stoktakip --app.migration.legacy-datasource.url=jdbc:oracle:thin:@//localhost:1522/FREEPDB1 --app.migration.legacy-datasource.username=legacy_readonly --app.migration.legacy-datasource.password=legacy_readonly"
```

Logda hedef datasource icin `user=STOKTAKIP`, legacy datasource icin `user=LEGACY_READONLY` gorulmeli. Hedef datasource `LEGACY_READONLY` cikarsa migration durdurulur.
