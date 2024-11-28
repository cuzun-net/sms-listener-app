# Bugbusters SMS Forwarder

## 1. Uygulamanın Amacı
Bugbusters SMS Forwarder, Android cihazlara gelen SMS mesajlarını otomatik olarak yakalayıp, belirtilen bir API endpoint'ine ileten bir uygulamadır. Uygulama özellikle:
- SMS içeriğinden belirli formattaki verileri (örn: 6 haneli kodlar) çıkarabilir
- Çıkarılan veriyi JSON formatında API'ye iletebilir
- Farklı HTTP metodları ile (POST, PUT, PATCH) veri gönderebilir

## 2. Uygulamanın Kullanım Şekli
1. Uygulama kurulduktan sonra:
   - SMS izinlerini vermeniz gerekir
   - API URL'ini kontrol edin/değiştirin
   - HTTP metodunu seçin (varsayılan: POST)
   - İsterseniz regex pattern'i özelleştirin

2. Varsayılan ayarlar:
   - API URL: https://sms-listener-api.onrender.com/api/data
   - HTTP Metodu: POST
   - Regex Pattern: (?<=:)\\s*(\\d{6}) (iki nokta üst üste sonrası 6 haneli sayıyı yakalar)

3. Veri formatı:
   ```json
   {
       "message": "extracted_content"
   }
   ```

## 3. Gereksinimler
1. Geliştirme ortamı için:
   - Android Studio (en son versiyon)
   - JDK 8 veya üzeri
   - Android SDK (API Level 21 ve üzeri)
   - Bir Android cihaz veya emülatör

2. Cihaz gereksinimleri:
   - Android 5.0 (API Level 21) veya üzeri
   - SMS alma özelliği
   - Internet bağlantısı

## 4. Uygulama Geliştirme Adımları
1. Proje Oluşturma:
   ```
   - Android Studio'yu açın
   - New Project > Empty Activity seçin
   - Name: Bugbusters Forwarder
   - Package name: com.example.smsprojx
   - Language: Java
   - Minimum SDK: API 21
   ```

2. Gerekli izinleri AndroidManifest.xml'e ekleyin:
   ```xml
   <uses-permission android:name="android.permission.RECEIVE_SMS" />
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
   ```

3. build.gradle (app) dosyasına dependencies ekleyin:
   ```gradle
   dependencies {
       implementation 'androidx.appcompat:appcompat:1.6.1'
       implementation 'com.google.android.material:material:1.10.0'
       implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
       implementation 'com.squareup.okhttp3:okhttp:4.11.0'
   }
   ```

4. Ana bileşenleri oluşturun:
   - MainActivity.java: Kullanıcı arayüzü ve ayarlar
   - SmsReceiver.java: SMS yakalama ve işleme
   - activity_main.xml: Arayüz tasarımı

5. Test:
   - Uygulamayı Android cihaza yükleyin
   - SMS izinlerini verin
   - Test SMS'i gönderin (örn: "Test mesajı : 123456")
   - API yanıtını kontrol edin

## 5. APK Oluşturma
1. Debug APK oluşturma:
   ```
   - Android Studio'da Build menüsüne tıklayın
   - Build Bundle(s) / APK(s) seçin
   - Build APK(s) seçin
   ```
   APK dosyası: app/build/outputs/apk/debug/app-debug.apk

2. Release APK oluşturma:
   ```
   - Build menüsü > Generate Signed Bundle / APK
   - APK seçin
   - Create new keystore
   - Keystore bilgilerini doldurun
   - Release seçin ve Finish'e tıklayın
   ```
   APK dosyası: app/build/outputs/apk/release/app-release.apk

## 6. Kurulum ve Test
1. APK Kurulumu:
   - Android cihazda "Bilinmeyen kaynaklar"ı etkinleştirin
   - APK dosyasını cihaza kopyalayın
   - APK'ya tıklayıp kurulumu başlatın

2. Test:
   - Uygulamayı açın
   - SMS izinlerini onaylayın
   - API URL'i kontrol edin
   - Test SMS'i gönderin
   - API yanıtını kontrol edin

## 7. Özelleştirme
1. Regex Pattern değiştirme:
   - Farklı SMS formatları için pattern'i değiştirin
   - Örnek: `"\\d+"` (herhangi bir sayı dizisi)
   - Örnek: `"(?<=Amount:)\\s*(\\d+)"` (Amount: sonrası sayı)

2. API URL değiştirme:
   - Kendi API endpoint'inizi girin
   - HTTP metodunu seçin
   - Ayarları kaydedin

## 8. Hata Ayıklama
1. Logcat'te hataları kontrol edin
2. API yanıtlarını kontrol edin
3. SMS izinlerinin verildiğinden emin olun
4. Internet bağlantısını kontrol edin

## 9. Güvenlik Notları
1. API anahtarlarını kodda saklamayın
2. Release APK'yı imzalayın
3. ProGuard kurallarını kullanın
4. SSL/TLS kullanın (https://)
