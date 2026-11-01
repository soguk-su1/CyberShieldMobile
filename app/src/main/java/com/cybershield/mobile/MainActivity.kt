package com.cybershield.mobile

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.widget.*
import java.util.Locale

class MainActivity : Activity() {
    private val bg = Color.rgb(7, 20, 38)
    private val panel = Color.rgb(13, 35, 61)
    private val blue = Color.rgb(53, 182, 246)
    private val white = Color.rgb(235, 245, 255)
    private val muted = Color.rgb(151, 177, 201)
    private lateinit var content: LinearLayout
    private val apkPicker = 41

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        showHome()
    }

    private fun base(title: String, subtitle: String) {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(20, 22, 20, 18)
        }
        val header = TextView(this).apply {
            text = "🛡  CYBERSHIELD"
            textSize = 13f
            setTextColor(blue)
            letterSpacing = .16f
        }
        root.addView(header)
        root.addView(TextView(this).apply {
            text = title; textSize = 27f; setTextColor(white)
            setPadding(0, 12, 0, 3); typeface = android.graphics.Typeface.DEFAULT_BOLD
        })
        root.addView(TextView(this).apply {
            text = subtitle; textSize = 13f; setTextColor(muted)
        })
        val scroll = ScrollView(this)
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 18, 0, 12)
        }
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        listOf("Ana Sayfa", "SMS", "APK", "Link").forEachIndexed { i, label ->
            val b = Button(this).apply {
                text = label
                textSize = 11f
                setTextColor(white)
                setBackgroundColor(panel)
                setOnClickListener { when(i) { 0 -> showHome(); 1 -> showSms(); 2 -> showApk(); else -> showLink() } }
            }
            nav.addView(b, LinearLayout.LayoutParams(0, 48, 1f).apply { setMargins(3, 5, 3, 0) })
        }
        root.addView(nav)
        setContentView(root)
    }

    private fun card(title: String, body: String) {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 14, 16, 14)
            setBackgroundColor(panel)
        }
        box.addView(TextView(this).apply {
            text = title; textSize = 16f; setTextColor(white)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })
        box.addView(TextView(this).apply {
            text = body; textSize = 13f; setTextColor(muted); setPadding(0, 6, 0, 0)
        })
        content.addView(box, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = 12 })
    }

    private fun action(label: String, click: () -> Unit) {
        val b = Button(this).apply {
            text = label; setTextColor(Color.WHITE); setBackgroundColor(Color.rgb(18, 105, 165))
            setOnClickListener { click() }
        }
        content.addView(b, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = 10 })
    }

    private fun showHome() {
        base("Güvenlik merkezi", "Telefonunu kontrol et, şüpheli içerikleri analiz et.")
        card("●  Koruma araçları hazır", "SMS, APK ve bağlantıları cihaz üzerinde temel kurallarla inceleyebilirsin.")
        card("Gizlilik öncelikli", "Bu başlangıç sürümü SMS'lerini arka planda okumaz, mesajlarını veya dosyalarını sunucuya göndermez.")
        action("SMS dolandırıcılık kontrolü") { showSms() }
        action("APK dosyası incele") { showApk() }
        action("Bağlantı kontrolü") { showLink() }
        action("Cihaz güvenlik kontrolü") { showDevice() }
        card("Önemli", "Bu araç bir antivirüs motoru değildir. Sonuçlar sezgisel kontrollerdir; temiz sonucu zararsızlık garantisi sayma.")
    }

    private fun input(hint: String, multiline: Boolean = false): EditText {
        return EditText(this).apply {
            setHint(hint); setHintTextColor(muted); setTextColor(white)
            textSize = 14f; setPadding(14, 12, 14, 12)
            setBackgroundColor(panel)
            if (multiline) { minLines = 5; gravity = Gravity.TOP; inputType =  textInputType() }
            else inputType = android.text.InputType.TYPE_CLASS_TEXT
        }
    }

    private fun textInputType(): Int =
        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE

    private fun showSms() {
        base("SMS güvenliği", "Mesaj içeriğini buraya yapıştır; şüpheli işaretleri kontrol edelim.")
        val field = input("SMS metnini buraya yapıştır…", true)
        content.addView(field, LinearLayout.LayoutParams(-1, 150).apply { bottomMargin = 10 })
        val result = TextView(this).apply { setTextColor(white); textSize = 14f; setPadding(12, 12, 12, 12) }
        content.addView(result)
        action("Mesajı analiz et") {
            val report = analyzeText(field.text.toString())
            result.text = report
            result.setBackgroundColor(panel)
        }
        card("Güvenli kullanım", "Bankanın veya kargonun mesajındaki numarayı/linki kullanmak yerine kurumun resmî uygulamasını ya da kendin yazdığın resmî adresi aç.")
    }

    private fun analyzeText(text: String): String {
        if (text.isBlank()) return "Analiz için önce mesaj metni gir."
        val t = text.lowercase(Locale.ROOT)
        val findings = mutableListOf<String>()
        val riskyWords = listOf("hemen tıkla", " hesabınız ", "şifreniz", "doğrula", "ödül kazandınız",
            "kargonuz", "icra", "askıya alın", "son  dakika", "kapınızda")
        if (riskyWords.any { t.contains(it.trim()) }) findings.add("• Aciliyet, hesap doğrulama veya ödül teması içeriyor.")
        val urls = Regex("""https?://\S+|www\.\S+""").findAll(text).map { it.value }.toList()
        if (urls.isNotEmpty()) {
            findings.add("• Mesajda ${urls.size} bağlantı var. Göndereni bağımsız doğrulamadan açma.")
            urls.forEach { findings.add("  - $it") }
        }
        if (Regex("""\b\d{4,8}\b""").containsMatchIn(text)) findings.add("• Sayısal kod/OTP olabilir. Kimseyle paylaşma.")
        if (listOf("iban", "havale", "eft", "ödeme yap", "kart bilg").any { t.contains(it) })
            findings.add("• Para transferi veya ödeme bilgisi talebi var.")
        if (findings.isEmpty()) findings.add("Belirgin şüpheli kalıp bulunmadı. Bu, mesajın güvenli olduğunu kanıtlamaz.")
        return "ÖN İNCELEME\n${findings.joinToString("\n")}\n\nSonuç: Otomatik sezgisel kontrol; kesin tespit değildir."
    }

    private fun showLink() {
        base("Bağlantı kontrolü", "Bir URL girerek yaygın oltalama işaretlerini kontrol et.")
        val field = input("https://ornek.com")
        content.addView(field, LinearLayout.LayoutParams(-1, 58).apply { bottomMargin = 10 })
        val result = TextView(this).apply { setTextColor(white); textSize = 14f }
        content.addView(result)
        action("Bağlantıyı analiz et") {
            val raw = field.text.toString().trim()
            if (raw.isBlank()) { result.text = "Önce bir bağlantı gir."; return@action }
            val normalized = if (raw.startsWith("http")) raw else "https://$raw"
            val uri = runCatching { Uri.parse(normalized) }.getOrNull()
            val host = uri?.host.orEmpty().lowercase(Locale.ROOT)
            val flags = mutableListOf<String>()
            if (uri == null || host.isBlank()) flags.add("• Geçerli bir alan adı okunamadı.")
            if (uri?.scheme != "https") flags.add("• HTTPS kullanılmıyor.")
            if (host.contains("@") || normalized.contains("@")) flags.add("• Adreste kullanıcı adı/karışıklık işareti var.")
            if (host.startsWith("xn--") || host.contains(".xn--")) flags.add("• Punycode alan adı: benzer görünen harfler kullanılabilir.")
            if (Regex("""\d{1,3}(\.\d{1,3}){3}""").matches(host)) flags.add("• Alan adı yerine IP adresi kullanılmış.")
            if (host.count { it == '-' } >= 2 || host.split(".").size >= 5) flags.add("• Alan adı alışılmadık derecede karmaşık.")
            if (flags.isEmpty()) flags.add("Yaygın risk işareti bulunmadı; bu, sitenin güvenli olduğunu doğrulamaz.")
            result.text = "ÖN İNCELEME: $host\n${flags.joinToString("\n")}\n\nLinki açmadık veya çevrimiçi itibar servisine göndermedik."
        }
    }

    private fun showApk() {
        base("APK inceleme", "Bir APK seç; paket adı ve talep ettiği izinleri görüntüle.")
        card("Nasıl çalışır?", "Dosya seçici üzerinden APK okunur. Dosya çalıştırılmaz ve yüklenmez.")
        action("APK dosyası seç") {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE); type = "application/vnd.android.package-archive"
            }
            startActivityForResult(intent, apkPicker)
        }
        card("Not", "İzin listesi tek başına zararlı yazılım tespiti değildir. Bu sürüm imza/ davranış analizi yapmaz.")
    }

    @Deprecated("Use activity result API in a future update")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == apkPicker && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            val report = inspectApk(uri)
            base("APK inceleme sonucu", "Seçilen dosyanın temel paket bilgileri")
            card("Analiz", report)
            action("Ana sayfaya dön") { showHome() }
        }
    }

    private fun inspectApk(uri: Uri): String {
        return try {
            val path = cacheDir.resolve("selected.apk")
            contentResolver.openInputStream(uri)?.use { input -> path.outputStream().use { input.copyTo(it) } }
                ?: return "Dosya okunamadı."
            val info = packageManager.getPackageArchiveInfo(path.absolutePath, PackageManager.GET_PERMISSIONS)
                ?: return "APK paketi okunamadı veya dosya geçerli bir APK değil."
            val perms = info.requestedPermissions?.map { it.name } ?: emptyList()
            val sensitive = perms.filter {
                it.contains("SMS") || it.contains("CONTACTS") || it.contains("RECORD_AUDIO") ||
                it.contains("CAMERA") || it.contains("LOCATION") || it.contains("CALL_LOG")
            }
            "Paket: ${info.packageName}\nSürüm: ${info.versionName ?: "bilinmiyor"}\n" +
                "İzin sayısı: ${perms.size}\n\nİzinler:\n" +
                (perms.joinToString("\n").ifBlank { "İzin listesi bulunamadı." }) +
                "\n\nDikkat gerektiren izinler:\n" +
                (sensitive.joinToString("\n").ifBlank { "Bu basit filtrede eşleşme yok." }) +
                "\n\nBu rapor statik ve sınırlıdır; APK'yı güvenli ilan etmez."
        } catch (e: Exception) { "APK incelenemedi: ${e.message ?: "bilinmeyen hata"}" }
    }

    private fun showDevice() {
        base("Cihaz kontrolü", "Android'in sunduğu temel durum bilgileri.")
        val pm = packageManager
        val version = android.os.Build.VERSION.RELEASE ?: "bilinmiyor"
        card("Android sürümü", version)
        card("Güvenlik yaması", android.os.Build.VERSION.SECURITY_PATCH ?: "Bilgi alınamadı")
        card("Uygulama yükleme", "Bu uygulama bilinmeyen kaynaklardan yükleme iznini değiştirmez. Ayarları yalnızca Android'in kendi ayarlarından kontrol et.")
        card("Sınırlamalar", "Root, bootloader kilidi, gerçek zamanlı zararlı yazılım davranışı ve tüm sistem açıkları bu basit uygulama tarafından doğrulanamaz.")
        action("Android güvenlik ayarlarını aç") {
            runCatching { startActivity(Intent("android.settings.SECURITY_SETTINGS")) }
                .onFailure { Toast.makeText(this, "Ayarlar açılamadı.", Toast.LENGTH_SHORT).show() }
        }
    }
}
