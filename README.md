# Cek Keberangkatan Haji

Aplikasi Android sederhana yang membuka layanan informasi resmi di [haji.go.id/data-informasi](https://haji.go.id/data-informasi) menggunakan WebView.

## Build APK melalui GitHub Actions

1. Unggah/push seluruh isi folder proyek ini ke repository `wachid01-ui/Cek-Haji` pada branch `main`.
2. Buka tab **Actions** dan jalankan workflow **Build Android APK** (workflow juga berjalan otomatis setiap push ke `main`).
3. Setelah workflow selesai, buka hasil run dan unduh artifact `cek-keberangkatan-haji-debug`.
4. Ekstrak artifact untuk memperoleh `app-debug.apk`, lalu pasang di perangkat Android.

Workflow menggunakan Java 17 dan Gradle 8.9. APK debug cocok untuk instalasi dan uji coba. Untuk distribusi Play Store, perlu konfigurasi signing release tersendiri.

## Pengembangan lokal

Buka folder ini sebagai proyek Gradle Android di Android Studio. Aplikasi membutuhkan koneksi internet dan memuat konten langsung dari situs resmi; ketersediaan serta tampilan halaman mengikuti situs tersebut.
