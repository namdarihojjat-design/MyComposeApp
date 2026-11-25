package ir.timzo.photoshop.twa

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Base64
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Dark luxury tech theme with Gold accents
            val darkTheme = darkColorScheme(
                primary = Color(0xFFFFD54F),
                onPrimary = Color(0xFF1E1E24),
                surface = Color(0xFF16181D),
                background = Color(0xFF0F1014),
                surfaceVariant = Color(0xFF22252D)
            )

            MaterialTheme(colorScheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StudioMainScreen()
                }
            }
        }
    }
}

private enum class AppShape(val code: String, val title: String, val desc: String) {
    Square("square", "مربع", "1024x1024"),
    Landscape("landscape", "افقی", "1536x1024"),
    Portrait("portrait", "عمودی", "1024x1536")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioMainScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val client = remember {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    var isEditMode by remember { mutableStateOf(false) }
    var prompt by remember { mutableStateOf("") }
    var selectedShape by remember { mutableStateOf(AppShape.Square) }
    var isTransparent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("آماده دریافت ایده شما...") }

    var selectedImageB64 by remember { mutableStateOf<String?>(null) }
    var outputBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    statusText = "در حال بارگذاری تصویر انتخاب شده..."
                    val input: InputStream? = ctx.contentResolver.openInputStream(uri)
                    val bytes = input?.readBytes() ?: ByteArray(0)
                    val b64 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Base64.getEncoder().encodeToString(bytes)
                    } else {
                        android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                    }
                    selectedImageB64 = "data:image/png;base64,$b64"
                    statusText = "تصویر با موفقیت بارگذاری شد ✨"
                } catch (e: Exception) {
                    statusText = "خطا در خواندن فایل: ${e.message}"
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        // 🕌 هدر معنوی، دعایی و شیک
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF2C2416), Color(0xFF16181D))
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFD54F))
                        )
                        Text(
                            text = "یا علی مدد • استودیو هوشمند تیمزو",
                            color = Color(0xFFFFD54F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "«السلام علیک یا امیرالمؤمنین و یا اباعبدالله الحسین»\nاللهم عجّل لولیک الفرج و اقضِ حوائجنا بالقرآن.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // 🎨 انتخاب مد کاربری (تولید / ویرایش)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (!isEditMode) Color(0xFFFFD54F) else Color.Transparent)
                    .clickable { isEditMode = false }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "تولید تصویر جدید",
                    fontWeight = FontWeight.Bold,
                    color = if (!isEditMode) Color(0xFF1E1E24) else Color.White
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isEditMode) Color(0xFFFFD54F) else Color.Transparent)
                    .clickable { isEditMode = true }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "ویرایش و برش تصویر",
                    fontWeight = FontWeight.Bold,
                    color = if (isEditMode) Color(0xFF1E1E24) else Color.White
                )
            }
        }

        // 📷 بخش آپلود در صورت انتخاب ویرایش
        AnimatedVisibility(visible = isEditMode) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("انتخاب عکس پایه:", fontWeight = FontWeight.SemiBold, color = Color.White)
                    Button(
                        onClick = { pickImageLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E323D))
                    ) {
                        Text(if (selectedImageB64 != null) "✓ تصویر انتخاب شد (تغییر)" else "+ بارگذاری عکس از گالری")
                    }
                }
            }
        }

        // ✍️ بخش پرامپت (متن ایده)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("ایده و توصیف تصویر:", fontWeight = FontWeight.SemiBold, color = Color.White)

                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("مثلاً: لوگوی طلایی و مدرن عقاب برای شرکت تکنولوژی، کیفیت فوق‌العاده 8k...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFD54F),
                        unfocusedBorderColor = Color.DarkGray
                    )
                )

                // انتخاب ابعاد تصویر
                Text("ابعاد خروجی:", fontSize = 13.sp, color = Color.Gray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppShape.values().forEach { shapeItem ->
                        val isSel = selectedShape == shapeItem
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 1.dp,
                                    color = if (isSel) Color(0xFFFFD54F) else Color.DarkGray,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .background(if (isSel) Color(0x22FFD54F) else Color.Transparent)
                                .clickable { selectedShape = shapeItem }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(shapeItem.title, fontWeight = FontWeight.Bold, color = if (isSel) Color(0xFFFFD54F) else Color.White, fontSize = 12.sp)
                                Text(shapeItem.desc, fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                // سوییچ پس‌زمینه شفاف
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("پس‌زمینه بدون بک‌گراند (شفاف / PNG)", fontSize = 13.sp, color = Color.White)
                        Text("مخصوص استیکر، لوگو و برش اشیاء", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = isTransparent,
                        onCheckedChange = { isTransparent = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD54F))
                    )
                }
            }
        }

        // 🚀 دکمه اجرای اصلی
        Button(
            onClick = {
                if (prompt.isBlank()) {
                    statusText = "لطفاً توضیحی برای تصویر بنویسید!"
                    return@Button
                }
                if (isEditMode && selectedImageB64 == null) {
                    statusText = "لطفاً ابتدا عکس را از گالری انتخاب کنید!"
                    return@Button
                }

                scope.launch(Dispatchers.IO) {
                    try {
                        isLoading = true
                        statusText = "هوش مصنوعی در حال پردازش تصویر است (لطفاً کمی صبر کنید)..."
                        outputBitmap = null

                        val finalPrompt = if (isTransparent && !prompt.contains("cutout", ignoreCase = true)) {
                            "cutout, $prompt"
                        } else {
                            prompt
                        }

                        val payload = JSONObject().apply {
                            put("mode", if (isEditMode) "edit" else "generate")
                            put("prompt", finalPrompt)
                            put("shape", selectedShape.code)
                            put("transparent", isTransparent)
                            if (isEditMode) {
                                put("image", selectedImageB64)
                            }
                        }.toString()

                        val reqBody = payload.toRequestBody("application/json; charset=utf-8".toMediaType())
                        val request = Request.Builder()
                            .url("https://aiimagesmith.com/api/image")
                            .post(reqBody)
                            .build()

                        client.newCall(request).execute().use { resp ->
                            if (!resp.isSuccessful) {
                                throw Exception("کد خطا: ${resp.code}")
                            }
                            val resString = resp.body?.string() ?: ""
                            val jsonObj = JSONObject(resString)
                            val b64Result = jsonObj.getString("image")

                            val commaIdx = b64Result.indexOf(',')
                            val pureB64 = if (commaIdx >= 0) b64Result.substring(commaIdx + 1) else b64Result
                            val decodedBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Base64.getDecoder().decode(pureB64)
                            } else {
                                android.util.Base64.decode(pureB64, android.util.Base64.DEFAULT)
                            }

                            outputBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                            statusText = "تصویر با موفقیت خلق شد! 🎉"
                        }
                    } catch (e: Exception) {
                        statusText = "خطا در عملیات: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F))
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF1E1E24), modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(10.dp))
                Text("در حال پردازش...", color = Color(0xFF1E1E24), fontWeight = FontWeight.Bold)
            } else {
                Text(
                    if (!isEditMode) "✨ خلق شگفت‌انگیز تصویر" else "🎨 اعمال ویرایش روی تصویر",
                    color = Color(0xFF1E1E24),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        // ℹ️ متن وضعیت
        Text(
            text = statusText,
            color = if (statusText.startsWith("خطا")) Color(0xFFFF6B6B) else Color.LightGray,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // 🖼️ کارت نمایش خروجی نهایی
        outputBitmap?.let { bmp ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("خروجی هوش مصنوعی:", fontWeight = FontWeight.Bold, color = Color.White)

                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Result Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                    )

                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                try {
                                    val stream = ByteArrayOutputStream()
                                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                                    val pngBytes = stream.toByteArray()

                                    val values = ContentValues().apply {
                                        put(MediaStore.Images.Media.DISPLAY_NAME, "Timzo_${System.currentTimeMillis()}.png")
                                        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/TimzoStudio")
                                        }
                                    }
                                    val uri = ctx.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                                    uri?.let {
                                        ctx.contentResolver.openOutputStream(it)?.use { out ->
                                            out.write(pngBytes)
                                        }
                                        statusText = "تصویر در گالری ذخیره شد! 💾"
                                    }
                                } catch (e: Exception) {
                                    statusText = "خطا در ذخیره تصویر: ${e.message}"
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ذخیره باکیفیت در گالری گوشی 📥", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
