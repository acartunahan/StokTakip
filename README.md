# StokTakip Veri Göç Altyapısı

Bu proje, Delphi + Oracle ile yazılmış eski stok/demirbaş sisteminden Java 25 + Spring Boot 4 + Oracle hedef modele güvenli veri göçü yapmak için hazırlanmış iskelettir.

Temel kabuller:

- Kaynak veritabanı sadece okunur kabul edilir.
- Hedef entity anahtarı `GUID` kolonudur; eski `ID` varsa doğrudan entity içindeki `ID` alanında saklanır.
- Her aktarılan satırın ham kaynak değeri `VERI_GOC_ESKI_VERI_YUKU` tablosuna JSON olarak yazılır.
- Veri göçü varsayılan olarak kapalı ve kuru çalışma modundadır.
- Çalıştırmak için `app.migration.enabled=true`, gerçek yazma için ayrıca `app.migration.dry-run=false` verilmelidir.

Ayrıntılı analiz ve eşleme: [docs/migration-analysis.md](docs/migration-analysis.md).
