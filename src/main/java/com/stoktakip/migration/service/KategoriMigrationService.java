package com.stoktakip.migration.service;

import com.stoktakip.entity.Kategori;
import com.stoktakip.repository.KategoriRepository;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(prefix = "app.migration", name = "enabled", havingValue = "true")
public class KategoriMigrationService {

    private final JdbcTemplate eskiJdbcTemplate;
    private final KategoriRepository kategoriRepository;

    public KategoriMigrationService(
        @Qualifier("eskiJdbcTemplate") JdbcTemplate eskiJdbcTemplate,
        KategoriRepository kategoriRepository
    ) {
        this.eskiJdbcTemplate = eskiJdbcTemplate;
        this.kategoriRepository = kategoriRepository;
    }

    @Transactional
    public KategoriMigrationResult migrate() {
        List<KategoriSatiri> kaynakSatirlari = new ArrayList<>();
        kaynakSatirlari.addAll(sarfKategorileriOku());
        kaynakSatirlari.addAll(resatSabitKodlariOku());

        List<KategoriSatiri> siraliSatirlar = parentOncelikliSirala(kaynakSatirlari);
        int kaydedilen = 0;
        int guncellenen = 0;
        int atlanan = 0;

        for (KategoriSatiri satir : siraliSatirlar) {
            if (satir.guid() == null || satir.guid().isBlank()) {
                atlanan++;
                continue;
            }

            Optional<Kategori> mevcut = kategoriRepository.findByGuid(satir.guid());
            Kategori kategori = mevcut.orElseGet(Kategori::new);
            kategoriyeYaz(kategori, satir);
            kategoriRepository.save(kategori);

            if (mevcut.isPresent()) {
                guncellenen++;
            } else {
                kaydedilen++;
            }
        }

        return new KategoriMigrationResult(kaynakSatirlari.size(), kaydedilen, guncellenen, atlanan);
    }

    private List<KategoriSatiri> sarfKategorileriOku() {
        String sql = """
            SELECT GUID, PARENT, TITLE
            FROM SARF_KATEGORI
            WHERE GUID IS NOT NULL
            """;
        return eskiJdbcTemplate.query(sql, (rs, rowNum) -> new KategoriSatiri(
            "SARF_KATEGORI",
            metin(rs.getObject("GUID")),
            metin(rs.getObject("PARENT")),
            metin(rs.getObject("TITLE")),
            null,
            null
        ));
    }

    private List<KategoriSatiri> resatSabitKodlariOku() {
        String sql = """
            SELECT GUID, PARENT, AD, DURUM, K_TARIH
            FROM RESAT_SABIT_KOD
            WHERE GUID IS NOT NULL
            """;
        return eskiJdbcTemplate.query(sql, (rs, rowNum) -> new KategoriSatiri(
            "RESAT_SABIT_KOD",
            metin(rs.getObject("GUID")),
            metin(rs.getObject("PARENT")),
            metin(rs.getObject("AD")),
            aktifDegeri(metin(rs.getObject("DURUM"))),
            tarihDegeri(rs.getObject("K_TARIH"))
        ));
    }

    private void kategoriyeYaz(Kategori kategori, KategoriSatiri satir) {
        alanYaz(kategori, "guid", satir.guid());
        alanYaz(kategori, "parent", parentCoz(satir));
        alanYaz(kategori, "baslik", varsayilanBaslik(satir));
        if (satir.aktif() != null) {
            alanYaz(kategori, "aktif", satir.aktif());
        }
        if (satir.olusturmaTarihi() != null) {
            alanYaz(kategori, "olusturmaTarihi", satir.olusturmaTarihi());
        }
    }

    private Kategori parentCoz(KategoriSatiri satir) {
        String parentGuid = satir.parent();
        if (parentGuid == null || parentGuid.isBlank()) {
            return null;
        }
        if (parentGuid.equals(satir.guid())) {
            throw new IllegalStateException(
                satir.kaynakTablo() + ".PARENT kategori kendi GUID degeri olamaz: " + parentGuid
            );
        }
        Kategori parent = kategoriRepository.findByGuid(parentGuid)
            .orElseThrow(() -> new IllegalStateException(
                satir.kaynakTablo() + ".PARENT hedef KATEGORI.GUID icinde bulunamadi: " + parentGuid
            ));
        if (kategoriParentDongusuOlusur(satir.guid(), parent)) {
            throw new IllegalStateException(
                satir.kaynakTablo() + ".PARENT kategori parent dongusu olusturur: "
                    + satir.guid() + " -> " + parentGuid
            );
        }
        return parent;
    }

    private static List<KategoriSatiri> parentOncelikliSirala(List<KategoriSatiri> satirlar) {
        Map<String, KategoriSatiri> guidSatirlari = new LinkedHashMap<>();
        for (KategoriSatiri satir : satirlar) {
            if (satir.guid() != null && !satir.guid().isBlank()) {
                guidSatirlari.putIfAbsent(satir.guid(), satir);
            }
        }

        List<KategoriSatiri> sirali = new ArrayList<>();
        Set<String> ziyaretEdilen = new HashSet<>();
        Set<String> ziyaretEdiliyor = new HashSet<>();
        for (KategoriSatiri satir : satirlar) {
            parentOncelikliEkle(satir, guidSatirlari, ziyaretEdilen, ziyaretEdiliyor, sirali);
        }
        return sirali;
    }

    private static void parentOncelikliEkle(
        KategoriSatiri satir,
        Map<String, KategoriSatiri> guidSatirlari,
        Set<String> ziyaretEdilen,
        Set<String> ziyaretEdiliyor,
        List<KategoriSatiri> sirali
    ) {
        String guid = satir.guid();
        if (guid == null || guid.isBlank()) {
            sirali.add(satir);
            return;
        }
        if (ziyaretEdilen.contains(guid)) {
            return;
        }
        if (!ziyaretEdiliyor.add(guid)) {
            throw new IllegalStateException("Kategori parent dongusu bulundu: " + guid);
        }

        KategoriSatiri parent = guidSatirlari.get(satir.parent());
        if (parent != null) {
            parentOncelikliEkle(parent, guidSatirlari, ziyaretEdilen, ziyaretEdiliyor, sirali);
        }
        ziyaretEdiliyor.remove(guid);
        ziyaretEdilen.add(guid);
        sirali.add(satir);
    }

    private static String varsayilanBaslik(KategoriSatiri satir) {
        if (satir.baslik() != null && !satir.baslik().isBlank()) {
            return satir.baslik().length() <= 120 ? satir.baslik() : satir.baslik().substring(0, 120);
        }
        return "LEGACY " + satir.guid();
    }

    private static Boolean aktifDegeri(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return switch (value.trim().toUpperCase()) {
            case "1", "A", "AKTIF", "TRUE", "E", "Y" -> Boolean.TRUE;
            case "0", "P", "PASIF", "FALSE", "H", "N", "D", "S", "SIL", "SILINMIS" -> Boolean.FALSE;
            default -> null;
        };
    }

    private static LocalDateTime tarihDegeri(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        if (value instanceof java.sql.Date date) {
            return date.toLocalDate().atStartOfDay();
        }
        if (value instanceof java.util.Date date) {
            return new Timestamp(date.getTime()).toLocalDateTime();
        }
        return null;
    }

    private static String metin(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    private static void alanYaz(Object entity, String alanAdi, Object value) {
        try {
            Field field = alan(entity.getClass(), alanAdi);
            field.set(entity, value);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Entity alani yazilamadi: " + alanAdi, exception);
        }
    }

    private static boolean kategoriParentDongusuOlusur(String kategoriGuid, Kategori parent) {
        Set<String> ziyaretEdilen = new HashSet<>();
        Kategori current = parent;
        while (current != null) {
            String currentGuid = metin(alanOku(current, "guid"));
            if (currentGuid == null || !ziyaretEdilen.add(currentGuid)) {
                return true;
            }
            if (currentGuid.equals(kategoriGuid)) {
                return true;
            }
            Object next = alanOku(current, "parent");
            current = next instanceof Kategori kategori ? kategori : null;
        }
        return false;
    }

    private static Object alanOku(Object entity, String alanAdi) {
        try {
            Field field = alan(entity.getClass(), alanAdi);
            return field.get(entity);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Entity alani okunamadi: " + alanAdi, exception);
        }
    }

    private static Field alan(Class<?> type, String alanAdi) {
        Class<?> current = type;
        while (current != null) {
            try {
                Field field = current.getDeclaredField(alanAdi);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new IllegalStateException("Entity alani bulunamadi: " + alanAdi);
    }

    private record KategoriSatiri(
        String kaynakTablo,
        String guid,
        String parent,
        String baslik,
        Boolean aktif,
        LocalDateTime olusturmaTarihi
    ) {
    }

    public record KategoriMigrationResult(
        int okunan,
        int kaydedilen,
        int guncellenen,
        int atlanan
    ) {
    }
}
