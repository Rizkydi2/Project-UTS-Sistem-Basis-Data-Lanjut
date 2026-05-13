# Metro Marketers, Inc. — Data Warehouse System

> **Java CLI Application** untuk manajemen data customer dan warehouse berbasis Object-Oriented Programming, dibangun menggunakan Eclipse IDE dengan database object-oriented **db4o**.

---

## Deskripsi Singkat

Sistem manajemen data warehouse untuk perusahaan **Metro Marketers, Inc.** yang memungkinkan pengguna berinteraksi langsung melalui antarmuka **Command Line Interface (CLI)** untuk mengelola data customer, marketing profile, dan konfigurasi platform database.

---

## Deskripsi Lengkap

Metro Marketers Data Warehouse System adalah aplikasi Java berbasis CLI yang dirancang untuk mensimulasikan sistem manajemen data warehouse perusahaan retail. Aplikasi ini menerapkan konsep **Object-Oriented Programming (OOP)** secara menyeluruh, mencakup enkapsulasi, relasi antar objek, dan pemisahan tanggung jawab antar class (separation of concerns).

Aplikasi menggunakan **db4o (Database for Objects)** sebagai engine penyimpanan object database, sehingga data customer dapat disimpan dan dikelola layaknya sistem database nyata tanpa perlu konfigurasi SQL.

Arsitektur aplikasi mengikuti alur hierarkis yang jelas:

```
main (CLI)
  └── DataWarehouse
        └── DatabasePlatform
        └── Db4oManager (db40)
              └── Customer
                    └── MarketingProfile
```

Pengguna berinteraksi hanya melalui `main`, yang mengkoordinasikan `DataWarehouse` dan `Db4oManager`. `DatabasePlatform` tidak pernah diakses langsung dari `main`, melainkan selalu melalui `DataWarehouse` — menerapkan prinsip **encapsulation** dan **single entry point**.

---

## Fitur

### Manajemen Customer (CRUD)
- **Tambah** customer baru dengan input interaktif (ID, nama, email, marketing profile)
- **Lihat** semua customer dalam format tabel, dengan opsi lihat detail per customer
- **Cari** customer berdasarkan Customer ID
- **Update** data customer (nama, email, marketing profile) dengan mempertahankan nilai lama jika tidak diubah
- **Hapus** customer dengan konfirmasi sebelum eksekusi

### Marketing Profile
- Setiap customer memiliki satu marketing profile yang terdiri dari:
  - Segment (Premium / Regular / Bronze)
  - Preferred Channel (Online / Offline / Social Media)
  - Loyalty Score (0–100) dengan kalkulasi tier otomatis (Platinum / Gold / Silver / Bronze)
  - Riwayat pembelian (purchase history) yang dapat diisi bebas

### Data Warehouse Management
- Lihat informasi lengkap warehouse (lokasi, kapasitas, processing power, storage type, backup status)
- Lihat daftar seluruh customer yang terdaftar di warehouse

### Database Platform Management
- Ganti platform database secara dinamis:
  - Oracle Database 19c (Oracle Corporation)
  - Red Brick Warehouse 6.3 (IBM Red Brick)
  - Custom platform (nama dan vendor bebas diisi)
- Perubahan platform dilakukan melalui `DataWarehouse`, bukan langsung — sesuai dengan desain arsitektur

### CLI Interaktif
- Menu berbasis teks dengan navigasi angka
- Validasi input (ID duplikat, angka di luar range, field wajib kosong)
- Pesan feedback `[OK]` dan `[ERR]` untuk setiap aksi
- Data awal (seed data) otomatis dimuat saat aplikasi pertama dijalankan

---

## Struktur Project

```
UTS_SBDL/
├── src/
│   ├── module-info.java
│   └── metro/marketers/
│       ├── main.java               # Entry point + CLI loop
│       ├── customer.java           # Entity customer
│       ├── marketingprofile.java   # Marketing profile customer
│       ├── db40.java               # Lapisan persistensi (Db4oManager)
│       ├── datawarehouse.java      # Manajemen warehouse & gateway ke platform
│       └── databaseplatform.java   # Representasi platform database
├── bin/                            # Compiled .class files
├── src/lib/
│   └── db4o-8.0.249.16098-all-java5.jar
├── .classpath
└── .project
```

---

## Teknologi

| Teknologi | Keterangan |
|-----------|------------|
| Java | Bahasa pemrograman utama |
| Eclipse IDE | Lingkungan pengembangan |
| db4o 8.0 | Object-oriented database engine |
| Java Scanner | Untuk membaca input CLI dari user |

---

## Cara Menjalankan

### Prasyarat
- Java JDK 11 atau lebih baru
- Eclipse IDE (versi 2021 ke atas direkomendasikan)

### Import ke Eclipse
1. Clone atau download repository ini
2. Buka Eclipse → **File → Import → Existing Projects into Workspace**
3. Arahkan ke folder `UTS_SBDL`
4. Klik **Finish**

### Menjalankan Aplikasi
1. Buka file `src/metro/marketers/main.java`
2. Klik kanan → **Run As → Java Application**
3. Aplikasi akan tampil di console Eclipse dan siap menerima input

---

## Contoh Tampilan CLI

```
╔══════════════════════════════════════════════════════════════╗
║       METRO MARKETERS, INC. — DATA WAREHOUSE SYSTEM         ║
║              Command Line Interface (CLI)                    ║
╚══════════════════════════════════════════════════════════════╝

╔══════════════════════════════════════════════════════╗
║           METRO MARKETERS — MAIN MENU               ║
╠══════════════════════════════════════════════════════╣
║  1. Tambah Customer Baru                             ║
║  2. Lihat Semua Customer                             ║
║  3. Cari Customer by ID                              ║
║  4. Update Customer                                  ║
║  5. Hapus Customer                                   ║
║  6. Info Data Warehouse                              ║
║  7. Ganti Database Platform                          ║
║  0. Keluar                                           ║
╚══════════════════════════════════════════════════════╝
  Pilih menu: _
```

---

## Desain OOP

### Prinsip yang Diterapkan

**Encapsulation**
Semua atribut class bersifat `private` dan hanya dapat diakses melalui getter/setter. `DatabasePlatform` tidak dapat diinstansiasi langsung dari `main` — hanya bisa melalui method `initPlatform()` dan `changePlatform()` milik `DataWarehouse`.

**Separation of Concerns**
- `main` → hanya bertanggung jawab atas alur CLI dan interaksi user
- `DataWarehouse` → mengelola koleksi customer dan menjadi gateway ke platform
- `db40` → bertanggung jawab atas operasi CRUD ke object database
- `customer` & `marketingprofile` → murni merepresentasikan data/entity

**Object Composition**
`Customer` memiliki satu `MarketingProfile` (komposisi). `DataWarehouse` memiliki satu `DatabasePlatform` yang dikelola secara internal.

---

## Data Awal (Seed Data)

Saat aplikasi dijalankan pertama kali, tiga customer berikut otomatis tersedia:

| ID | Nama | Segment | Loyalty |
|----|------|---------|---------|
| C001 | Alice Santoso | Premium | 85.0 (Platinum) |
| C002 | Bob Pratama | Regular | 55.0 (Gold) |
| C003 | Carla Wijayanti | Bronze | 30.0 (Bronze) |

---

## Author

Dikembangkan sebagai Kelompok 1 yang berisikan anggota : - Rizky Adinugraha Imansyah
                                                         - Iyaas Nurfath
                                                         - Juan Hafid Setiawan
                                                         - Raffi Pasha Ramadhan
dari tugas UTS mata kuliah **Sistem Basis Data Lanjut (SBDL)**.
