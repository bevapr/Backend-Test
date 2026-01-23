# Backend Test

## Gambaran Umum
Proyek ini mensimulasikan sistem pembayaran berbasis microservice.  
Untuk kesederhanaan, semua microservice diimplementasikan dalam satu proyek Spring Boot, tapi masing-masing service terdapat di package-package terpisah, sbb;

1. **Payment Service** (`com.example.payment`)
    - Mengelola pembuatan pembayaran
    - Idempotent: mencegah double charge dengan `transactionId` sebagai key unik
    - Memanggil Order dan Notification service setelah pembayaran berhasil

2. **Order Service** (`com.example.order`)
    - Mengelola pembuatan order
    - Idempotent: status hanya diubah jika berbeda dari status saat ini
    - Mencatat semua operasi untuk traceability

3. **Notification Service** (`com.example.notification`)
    - Mengirim notifikasi ke pengguna
    - Idempotent: memastikan satu pesan hanya dikirim sekali per `userId + message`
    - Mencatat setiap notifikasi yang dikirim atau dilewati

---

## Asumsi
- Single project tapi terstruktur sebagai 3 microservice terpisah (package berbeda)
- Alur `Order -> Paymment -> Notification` disimulasikan menggunakan panggilan HTTP via RestTemplate
- `RestTemplate` dikonfigurasi dengan timeout koneksi dan read untuk mensimulasikan masalah jaringan
- Idempotency menjamin:
    - PaymentService tidak melakukan double charge walaupun callback diterima berkali-kali
    - OrderService tidak menimpa status yang sudah sama
    - NotificationService tidak mengirim pesan ganda

- Logging ditambahkan di semua service untuk tracing:
    - Pembuatan Payment
    - Update status Order
    - Pengiriman Notification

---

### **2. Permaslahan External Service**

**a) Retry tidak boleh dilakukan jika:**
- Operasi tidak idempotent, karena bisa menimbulkan efek samping ganda
- Error bersifat permanent / fatal (cth: 400 (Bad Request))
- Retry bisa membebani service serta memperparah outage

**b) Perbedaan retry dan circuit breaker:**
- Retry = mencoba kembali request yang gagal sementara waktu
- Circuit Breaker = memutus panggilan ke service yang terus gagal agar tidak overload, lalu mencoba lagi setelah cooldown

**c) Risiko retry tanpa jitter/backoff:**
- Request menumpuk -> thundering herd
- Service bisa overload -> latency meningkat, outage

---

### **3. Sistem 8 microservice, user: “data hilang”**

**a) Informasi minimum di log:**
- Timestamp / waktu
- TraceId / requestId
- Service name & action / method
- Status / outcome (success / error)
- Error message & stack trace jika gagal
- Input parameter / identifier (transactionId, userId, dll.)

**b) Menelusuri request dari awal sampai akhir:**
- Pakai traceId / correlationId antar service
- Lacak log di setiap service dengan traceId yang sama

**c) Dampak traceId tidak konsisten:**
- Tidak bisa trace request end-to-end
- Debugging sulit
- Sulit mengetahui service mana yang gagal atau hilang

---

### **4. Event di Kafka / RabbitMQ**

**a) Agar consumer idempotent:**
- Simpan eventId / messageId 
- Jangan mengubah state jika event sudah pernah diproses

**b) Ordering dijamin oleh:**
- Kafka -> per partition
- RabbitMQ -> per queue 

**c) Dampak ordering tidak terjaga:**
- Data bisa tidak konsisten (cth: update stock / balance)
- Event bergantung urutan bisa gagal atau menghasilkan state salah

---

### **5. Traffic tinggi dan query DB mahal**

**a) Kapan cache di-invalidate:**
- Saat data update / delete
- Saat TTL habis
- Saat ada cache inconsistency

**b) Boleh dijadikan source of truth?**
- Tidak, cache itu hanya read-through / read-only
- Database tetap source of truth

**c) Mencegah cache stampede:**
- TTL acak / jitter
- Lock / mutex saat cache miss
- Write-behind / refresh-ahead untuk pre-populate cache

---

### **6. – Komunikasi antar microservice via HTTP**

**a) API key cukup?**
- Tidak cukup, API key bisa bocor 
- Perlu mekanisme autentikasi yang lebih aman

**b) Mencegah service palsu akses API internal:**
- Gunakan mutual TLS / mTLS
- JWT dengan signature + short expiry
- Batasi network internal (VPC / private subnet / firewall)

**c) Risiko JWT tanpa expiry pendek:**
- Token bisa digunakan selamanya jika dicuri -> service palsu dapat akses API
- Menurunkan keamanan sistem -> data leak / breach
