# 🧩 Sudoku Lite Edition
Sudoku adalah game Sudoku interaktif berbasis **Java Swing** dengan desain modern, fitur lengkap, dan sistem permainan yang fleksibel untuk dikembangkan lebih lanjut. Dirancang secara modular dengan beberapa kelas utama yang menangani UI, generator, logika permainan, dan manajemen puzzle.

---

## 🚀 Fitur Utama

### 🎮 **1. Main Menu & Difficulty Selection**
- Halaman utama dengan opsi:
  - **Play**
  - **Quit**
- Pemilihan tingkat kesulitan:
  - Easy
  - Medium
  - Hard

### 🔢 **2. Dynamic Sudoku Generator**
- Menghasilkan puzzle Sudoku yang valid.
- Generator membuat solusi lengkap → lalu melakukan penghapusan angka sesuai tingkat kesulitan.
- Puzzle dan solusi dikemas dalam `GeneratedSudoku`.

### 🧠 **3. Hint System (Full Helper)**
- Memberikan angka yang benar pada sel yang dipilih.
- Mengambil data dari solusi asli generator.
- Membantu pemain menyelesaikan puzzle tanpa error.

### ⏱️ **4. Count-Up Timer**
- Timer otomatis mulai dari **00:00** saat game dimulai.
- Menggunakan `javax.swing.Timer`.
- Real-time update.

### ❌ **5. Error Tracking**
- Menampilkan jumlah kesalahan pemain.
- Bertambah setiap kali pemain memasukkan angka yang salah.

### 🏆 **6. Win Detection**
- Sistem otomatis mendeteksi ketika seluruh grid telah terisi dengan benar.
- Menampilkan pop-up kemenangan.

### 💾 **7. Save & Load Progress (Coming Soon)**
- Struktur kode sudah disiapkan:
  - Penyimpanan puzzle
  - State permainan
  - Waktu
  - Error count

---

## 🏗️ Rancangan Kelas

### **📌 MainMenu.java**
- Menampilkan menu awal.
- Menyediakan tombol:
  - Play → ke DifficultyMenu
  - Quit → keluar aplikasi
- Menggunakan desain UI modern.

---

### **📌 DifficultyMenu.java**
- Menampilkan interface pemilihan tingkat kesulitan.
- Menggunakan gradient background.
- Meneruskan difficulty ke `SudokuGenerator` dan `Sudoku`.

---

### **📌 GeneratedSudoku.java**
Struktur sederhana berisi:
- `String[] puzzle` → puzzle final siap dimainkan  
- `String[] solution` → solusi lengkap puzzle  

Dipakai oleh `Sudoku.java`.

---

### **📌 SudokuGenerator.java**
Fungsi:
- Membuat board Sudoku terisi penuh (valid).
- Menghapus angka berdasarkan tingkat kesulitan.
- Menghasilkan objek `GeneratedSudoku`.

---

### **📌 Sudoku.java (Core Game Engine)**
Kelas inti yang menangani gameplay:

#### 🔹 **GUI**
- Grid 9 × 9
- Panel angka 1–9
- Panel informasi (timer, error counter)

#### 🔹 **Gameplay Logic**
- Klik angka → klik tile kosong → validasi otomatis.
- Kesalahan dihitung jika input salah.

#### 🔹 **Hint System**
- Menempatkan angka benar pada sel terpilih.
- Berdasarkan solusi puzzle asli.

#### 🔹 **Count-Up Timer**
- Timer berjalan sejak game dimulai hingga selesai.

#### 🔹 **Win Detection**
- Memeriksa apakah semua tile telah terisi dengan benar.
- Memunculkan pesan kemenangan.

#### 🔹 **Save/Load (Disiapkan)**
- Struktur sudah disiapkan untuk penyimpanan:
  - State grid
  - Time
  - Errors
  - Puzzle & solution

---

## 📦 Teknologi yang Digunakan
- **Java 17+**
- **Java Swing (GUI)**
- **OOP modular design**
- **Sudoku Generator (backtracking)**

---

## 📜 Lisensi
Proyek ini bebas digunakan untuk keperluan pembelajaran, modifikasi, atau pengembangan lebih lanjut.

---
 <3

