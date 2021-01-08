-- phpMyAdmin SQL Dump
-- version 4.9.5
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jan 07, 2021 at 05:54 PM
-- Server version: 10.3.27-MariaDB
-- PHP Version: 7.3.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `estimate_bus`
--

-- --------------------------------------------------------

--
-- Table structure for table `bus`
--

CREATE TABLE `bus` (
  `id` int(11) NOT NULL,
  `lat` double NOT NULL,
  `lng` double NOT NULL,
  `pos` varchar(100) NOT NULL,
  `speed` varchar(50) NOT NULL,
  `time` varchar(50) NOT NULL,
  `message` varchar(100) NOT NULL,
  `now` varchar(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `bus`
--

INSERT INTO `bus` (`id`, `lat`, `lng`, `pos`, `speed`, `time`, `message`, `now`) VALUES
(1, -7.280364513397217, 112.783447265625, 'Jalan Kertajaya Indah', '45 km/J', '02 mnt', 'Bus berangkat dari Halte ITS', '17:31');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `nama` varchar(100) NOT NULL,
  `no_telp` varchar(15) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `photo` varchar(100) DEFAULT NULL,
  `jenis` varchar(10) NOT NULL,
  `verified` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`nama`, `no_telp`, `email`, `password`, `photo`, `jenis`, `verified`) VALUES
('Nino Fachruroz', '081264829353', 'nfach98@gmail.com', 'bc819456266596e4fa51e5dc58dd9bef751597719f6d9b1917edb807dffe929c', NULL, 'driver', 1),
('Alief', '085334614400', 'alief.satria@student.ppns.ac.id', '252bac8d420c4014e7e7e96567001210c08031dcf5250c5d7211890b8b5bc37a', NULL, 'user', 0),
('hilmy', '081234567890', 'hilmy@gmail.com', '12d47b4fc4985f0ea9a90cb262430a6528a0dcf216d2281a55a9eecfe0cb1e82', NULL, 'user', 0),
('alief satria', '085334614400', 'aliefsatria41@gmail.com', '252bac8d420c4014e7e7e96567001210c08031dcf5250c5d7211890b8b5bc37a', NULL, 'driver', 0),
('Natanael', '0895336937930', 'natancr51@gmail.com', '87519c9d69ee5d9c39c8717e3f936d4af9b0dbb6ab3d069bea9d563227e230fa', NULL, 'user', 0),
('Natanael', '0896478256309', 'natantest@gmail.com', '87519c9d69ee5d9c39c8717e3f936d4af9b0dbb6ab3d069bea9d563227e230fa', NULL, 'user', 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bus`
--
ALTER TABLE `bus`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD UNIQUE KEY `idx_un_email` (`email`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
