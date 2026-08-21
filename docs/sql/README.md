# SQL dosya sirasi

Yeni hedef sema kurulumu icin:

1. `001_create_kategori.sql`
2. `003_create_missing_target_schema.sql`
3. `004_create_envanter_detay.sql`
4. `012_extend_zimmet.sql`

Veri/backfill amacli opsiyonel dosyalar:

- `002_migrate_kategori.sql`: Kaynak `SARF_KATEGORI` ve `RESAT_SABIT_KOD` tablolarina ayni baglamdan erisilebiliyorsa kullanilir.
- `005_backfill_envanter_detay.sql`: Daha once satin alma kolonlari `ENVANTER` uzerindeyken olusmus hedef semalarda `ENVANTER_DETAY` doldurmak icin kullanilir. Yeni kurulumda calistirilmaz.
- `006_add_legacy_table_name_to_migration_audit.sql`: Daha once audit tablolari olusmus semalarda `ESKI_TABLO_ADI` kolonlarini eklemek icin kullanilir.
- `009_move_envanter_barkod_to_envanter.sql`: Daha once `ENVANTER_BARKOD` hedef tablosu olusmus semalarda barkod degerini `ENVANTER.BARKOD` kolonuna tasir ve eski hedef tabloyu kaldirir. Yeni kurulumda calistirilmaz.
- `010_split_envanter_detay_identity.sql`: Daha once `ENVANTER_DETAY.GUID` ayni anda hem PK hem `ENVANTER(GUID)` FK olarak olusmus semalarda `ENVANTER_GUID` kolonunu ekler ve one-to-one FK'yi bu kolona tasir. Yeni kurulumda calistirilmaz.
- `011_update_kategori_parent_fk.sql`: Daha once `KATEGORI.PARENT` self-FK constraint'i eski adla olusmus semalarda constraint adini `FK_KATEGORI_PARENT_GUID` olacak sekilde duzeltir ve parent index'ini garanti eder. Yeni kurulumda calistirilmaz.
- `013_enable_aparat_zimmet_existing_schema.sql`: Envanter-only zimmet semasi daha once kurulmus hedef veritabanlarina `APARAT` tablosunu ve iki hareket tablosundaki `APARAT_GUID` baglantilarini ekler. Yeni kurulumda calistirilmaz.

`012_extend_zimmet.sql` yeni kurulumda bir kez calistirilir. Envanter veya Aparat hedefleyen zimmet kolonlarini, constraint/index'leri ve yeni hareket ID sequence'lerini ekler; legacy semaya veya veri goc verilerine dokunmaz. Envanter-only `012` dosyasi daha once calistirildiysa yeni `012` tekrar calistirilmaz; onun yerine `013_enable_aparat_zimmet_existing_schema.sql` kullanilir.

Legacy test verisi icin:

1. `007_create_legacy_test_schema.sql`: Sadece bos/local legacy test semasinda minimal kaynak tablolarini olusturur. Gercek legacy DB'de calistirilmaz.
2. `008_seed_legacy_test_data.sql`: Migration'i test etmek icin desteklenen legacy kaynak tablolarina 10'ar iliskili demo satir basar. Tekrar calistirilabilir `MERGE` ifadeleri kullanir.

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
