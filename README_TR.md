# CyberShield Mobile

Android için Kotlin tabanlı, mavi temalı güvenlik yardımcı uygulaması başlangıç projesi.

## Gereksinimler
- Android Studio (güncel sürüm)
- JDK 17
- Android SDK Platform 35
- İnternet bağlantısı yalnızca Android Studio/Gradle bağımlılıklarını indirmek için gerekir.

## Açma ve çalıştırma
1. ZIP dosyasını çıkar.
2. Android Studio > Open ile `CyberShieldMobile` klasörünü aç.
3. Gradle senkronizasyonunun bitmesini bekle.
4. Bir Android telefon bağla veya emülatör aç.
5. Run (▶) düğmesine bas.

APK oluşturmak için: Build > Build Bundle(s) / APK(s) > Build APK(s).
APK genellikle `app/build/outputs/apk/debug/app-debug.apk` konumunda oluşur.

## Özellikler
- SMS metnini kullanıcı yapıştırdığında yerel sezgisel analiz
- URL üzerinde temel oltalama işaretleri kontrolü
- Kullanıcının seçtiği APK'nın paket adı, sürümü ve izinlerini okuma
- Android sürümü ve güvenlik yaması bilgisini gösterme

## Gizlilik ve güvenlik sınırları
- SMS'ler arka planda okunmaz; SMS izni istenmez.
- APK dosyası çalıştırılmaz veya yüklenmez.
- Mesaj ve bağlantı içeriği herhangi bir sunucuya gönderilmez.
- Analizler imza tabanlı antivirüs veya çevrimiçi tehdit istihbaratı değildir.
- "Risk bulunmadı" sonucu güvenlik garantisi değildir.
- Bu proje ilk çalışan prototip/öğrenme başlangıcıdır; üretim seviyesinde antivirüs değildir.

## Önemli
Google Play, SMS ve arama kayıtları izinlerini sıkı biçimde sınırlar. Bu prototip özellikle SMS izni istemez. Gerçek zamanlı SMS filtreleme gerekiyorsa Android'in varsayılan SMS uygulaması/RoleManager ve Play politika gereksinimleri ayrıca ele alınmalıdır.
