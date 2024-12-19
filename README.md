
# Otopark Yönetim Sistemi

PROJE AÇIKLAMASI:
Bu proje, otopark yönetimi için bir masaüstü uygulamasıdır. Kullanıcıların giriş yaparak otopark bilgilerini görüntüleyebileceği, ödeme yapabileceği ve yeni bir kullanıcı oluşturabileceği özellikler sunar. Uygulama, kullanıcı verilerini ve ödeme bilgilerini MySQL veritabanında saklar.

KULLANILAN TEKNOLOJİLER:
- Programlama Dili: Java
- JAVAFX: Kullanıcı arayüzü tasarımı
- MySQL: Veritabanı yönetimi
- JDBC: Java Veritabanı Bağlantısı

ÖZELLİKLER:
1) Kullanıcı Girişi
- Kullanıcı ; üye veya admin olarak giriş yapabilir.
- Üye ol butonu ile üye olunabilir.
- Kullanıcı Adı ve şifre ile giriş yapılabilir.
- Üye Girişi başarılı olursa müşteri ekranına Yönetici girişi başarılı olursa Yönetici ekranına giriş yapılır.

2) Kayıt Olma
- Yeni kullanıcılar kullanıcı adıve şifre ile üye olabilir.

3) Araba Giriş ve Çıkışı 
- Kullanıcılar, araç girişi ve çıkışı yapabilir.
- Kullanıcılar araçlarının özelliklerini ve park edecekleri alanları seçer ve araç girişi yapar.
- Her kullanıcı birden fazla araçla giriş yapabilir.
- Toplam park alanı dolana kadar araç girişi yapılabilir bir park yerine birden fazla araç park edemez.
- Her kullanıcı giriş yaptığı aracıyla çıkış yapma yetkisine sahiptir.

4)Yönetim Paneli
-Yönetici otoparktaki boş ve dolu alanları görebilir.
-Yönetici otoparkta bulunan arabaların plakalarını giriş çıkış saatlerini görebilir.

EKRANLAR
1) Giriş Ekranı
- Kullanıcı,müşteri girişi veya yönetici girişinden birini seçer.

2) Üye Ol Formu 
- Yeni kullanıcılar için gerekli bilgilerle kayıt işlemi yapılır.

3) Müşteri ve Admin için Giriş Formu
- Müşteri ve Admin formu doldurarak giriş yapabilir.

4)Müşteri Ekranı
- Müşteri Araç girişi ve çıkışı yapabilir.
- Araçlarının özelliklerini görebilir.

5)Yönetici Ekranı
-Yönetici otoparkta bulunan arabaları, plakaları ve giriş çıkış saatlerini görebilir.
-İstatistikler
-Otopark doluluk oranı 

GELİŞTİRİCİLER VE GİTHUB BAĞLANTILARI:
- Cem Güzel ---> https://github.com/cemguzelklu
- Fatih Türker Akpınar ---> https://github.com/fatihturkerakpinar
