-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.30 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for aplikasi_gudang
DROP DATABASE IF EXISTS `aplikasi_gudang`;
CREATE DATABASE IF NOT EXISTS `aplikasi_gudang` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `aplikasi_gudang`;

-- Dumping structure for table aplikasi_gudang.barang
DROP TABLE IF EXISTS `barang`;
CREATE TABLE IF NOT EXISTS `barang` (
  `KodeBarang` int NOT NULL AUTO_INCREMENT,
  `NamaBarang` varchar(255) DEFAULT NULL,
  `Harga` decimal(10,0) DEFAULT NULL,
  `JumlahStok` int DEFAULT NULL,
  `TanggalUpdate` date DEFAULT NULL,
  PRIMARY KEY (`KodeBarang`)
) ENGINE=InnoDB AUTO_INCREMENT=258 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table aplikasi_gudang.barang: ~10 rows (approximately)
DELETE FROM `barang`;
INSERT INTO `barang` (`KodeBarang`, `NamaBarang`, `Harga`, `JumlahStok`, `TanggalUpdate`) VALUES
	(2, 'Laptop', 5000000, 6, '2024-01-01'),
	(3, 'Smartphone', 2000000, 20, '2024-01-02'),
	(4, 'Printer', 1000000, 5, '2024-01-03'),
	(5, 'Mouse', 50000, 50, '2024-01-04'),
	(6, 'Keyboard', 80000, 34, '2024-01-05'),
	(7, 'Monitor', 1000000, 16, '2024-01-06'),
	(8, 'Speaker', 200000, NULL, '2024-01-07'),
	(9, 'External HDD', 300000, 12, '2024-01-08'),
	(10, 'Webcam', 150000, 8, '2024-01-09'),
	(11, 'Headset', 120000, 22, '2024-01-10'),
	(256, 'HDMI', 200, 20, '2024-01-07');

-- Dumping structure for table aplikasi_gudang.supplier
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE IF NOT EXISTS `supplier` (
  `IDSupplier` int NOT NULL AUTO_INCREMENT,
  `NamaSupplier` varchar(255) DEFAULT NULL,
  `AlamatSupplier` varchar(255) DEFAULT NULL,
  `NomorTelepon` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`IDSupplier`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table aplikasi_gudang.supplier: ~10 rows (approximately)
DELETE FROM `supplier`;
INSERT INTO `supplier` (`IDSupplier`, `NamaSupplier`, `AlamatSupplier`, `NomorTelepon`) VALUES
	(1, 'Budi', 'Jl. Melati No. 123, Jakarta', '081234567890'),
	(2, 'Arman', 'Jl. Anggrek No. 456, Bandung', '087654321098'),
	(3, 'Ridho', 'Jl. Kenanga No. 789, Surabaya', '081112223344'),
	(4, 'Eva', 'Jl. Mawar No. 101, Yogyakarta', '081223344556'),
	(5, 'Fauzi', 'Jl. Dahlia No. 202, Semarang', '087789012345'),
	(6, 'Gita', 'Jl. Sakura No. 303, Medan', '081234567890'),
	(7, 'Hendra', 'Jl. Tulip No. 404, Makassar', '081112223344'),
	(8, 'Irene', 'Jl. Kamboja No. 505, Palembang', '087654321098'),
	(9, 'Joko', 'Jl. Lavender No. 606, Denpasar', '081223344556'),
	(10, 'Kartika', 'Jl. Lily No. 707, Manado', '087789012345');

-- Dumping structure for table aplikasi_gudang.transaksi
DROP TABLE IF EXISTS `transaksi`;
CREATE TABLE IF NOT EXISTS `transaksi` (
  `IDTransaksi` int NOT NULL AUTO_INCREMENT,
  `TanggalTransaksi` date DEFAULT NULL,
  `JenisTransaksi` varchar(10) DEFAULT NULL,
  `JumlahBarang` int DEFAULT NULL,
  `TotalHarga` decimal(10,0) DEFAULT NULL,
  `IDBarang` int DEFAULT NULL,
  `IDSupplier` int DEFAULT NULL,
  PRIMARY KEY (`IDTransaksi`),
  KEY `IDBarang` (`IDBarang`),
  KEY `IDSupplier` (`IDSupplier`),
  CONSTRAINT `transaksi_ibfk_1` FOREIGN KEY (`IDBarang`) REFERENCES `barang` (`KodeBarang`),
  CONSTRAINT `transaksi_ibfk_2` FOREIGN KEY (`IDSupplier`) REFERENCES `supplier` (`IDSupplier`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table aplikasi_gudang.transaksi: ~10 rows (approximately)
DELETE FROM `transaksi`;
INSERT INTO `transaksi` (`IDTransaksi`, `TanggalTransaksi`, `JenisTransaksi`, `JumlahBarang`, `TotalHarga`, `IDBarang`, `IDSupplier`) VALUES
	(1, '2024-01-07', 'masuk', 1, 200000, 8, 2),
	(4, '2024-01-07', 'masuk', 3, 15000000, 2, 1),
	(5, '2024-01-07', 'masuk', 1, 5000000, 2, 1),
	(6, '2024-01-07', 'masuk', 2, 600000, 9, 8),
	(7, '2024-01-07', 'masuk', 4, 200000, 5, 3),
	(8, '2024-01-07', 'keluar', 6, 30000000, 2, 2),
	(9, '2024-01-07', 'masuk', 1, 5000000, 2, 1),
	(10, '2024-01-07', 'masuk', 2, 10000000, 2, 1),
	(11, '2024-01-07', 'masuk', 6, 480000, 6, 4),
	(12, '2024-01-07', 'masuk', 4, 4000000, 7, 7),
	(20, '2024-01-07', 'keluar', 5, 1000, 256, 6);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
