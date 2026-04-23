-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : ven. 24 avr. 2026 à 01:12
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `moneywise_db`
--

-- --------------------------------------------------------

--
-- Structure de la table `alerte`
--

CREATE TABLE `alerte` (
  `id` int(11) NOT NULL,
  `message` varchar(255) NOT NULL,
  `date_alerte` datetime NOT NULL DEFAULT current_timestamp(),
  `type_alerte` enum('SEUIL_80','SEUIL_100') NOT NULL,
  `est_lue` tinyint(1) NOT NULL DEFAULT 0,
  `budget_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `alerte`
--

INSERT INTO `alerte` (`id`, `message`, `date_alerte`, `type_alerte`, `est_lue`, `budget_id`) VALUES
(2, 'Budget \"Transport\" dépassé à 100% !', '2026-03-20 22:13:18', 'SEUIL_100', 1, 5),
(3, 'Budget \"Épargne\" dépassé à 100% !', '2026-04-01 11:20:45', 'SEUIL_100', 1, 12),
(4, 'Budget \"Épargne\" atteint à 80%.', '2026-04-11 20:55:12', 'SEUIL_80', 1, 12),
(5, 'Budget \"Santé\" dépassé à 100% !', '2026-04-23 22:29:04', 'SEUIL_100', 1, 18);

-- --------------------------------------------------------

--
-- Structure de la table `budget`
--

CREATE TABLE `budget` (
  `id` int(11) NOT NULL,
  `montant_max` decimal(15,2) NOT NULL,
  `mois` int(2) NOT NULL,
  `annee` int(4) NOT NULL,
  `est_actif` tinyint(1) NOT NULL DEFAULT 1,
  `utilisateur_id` int(11) NOT NULL,
  `categorie_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `budget`
--

INSERT INTO `budget` (`id`, `montant_max`, `mois`, `annee`, `est_actif`, `utilisateur_id`, `categorie_id`) VALUES
(5, 200000.00, 3, 2026, 1, 2, 2),
(12, 12000000.00, 4, 2026, 1, 2, 8),
(13, 10000.00, 3, 2026, 1, 2, 6),
(14, 99.00, 3, 2026, 1, 2, 1),
(18, 20000.00, 4, 2026, 1, 5, 4),
(20, 2000.00, 5, 2026, 1, 5, 4);

-- --------------------------------------------------------

--
-- Structure de la table `categorie`
--

CREATE TABLE `categorie` (
  `id` int(11) NOT NULL,
  `nom` varchar(80) NOT NULL,
  `icone` varchar(50) DEFAULT NULL,
  `est_systeme` tinyint(1) NOT NULL DEFAULT 0,
  `utilisateur_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `categorie`
--

INSERT INTO `categorie` (`id`, `nom`, `icone`, `est_systeme`, `utilisateur_id`) VALUES
(1, 'Alimentation', 'shopping-cart', 1, NULL),
(2, 'Transport', 'car', 1, NULL),
(3, 'Logement', 'home', 1, NULL),
(4, 'Santé', 'heart-pulse', 1, NULL),
(5, 'Loisirs', 'gamepad', 1, NULL),
(6, 'Éducation', 'graduation-cap', 1, NULL),
(7, 'Salaire', 'briefcase', 1, NULL),
(8, 'Épargne', 'piggy-bank', 1, NULL),
(9, 'Autres', 'ellipsis', 1, NULL),
(12, 'crypto monaie', NULL, 0, 2),
(19, 'crypto', NULL, 0, 5);

-- --------------------------------------------------------

--
-- Structure de la table `export`
--

CREATE TABLE `export` (
  `id` int(11) NOT NULL,
  `format` enum('PDF','EXCEL') NOT NULL,
  `date_export` datetime NOT NULL DEFAULT current_timestamp(),
  `chemin_fichier` varchar(500) NOT NULL,
  `periode_debut` date NOT NULL,
  `periode_fin` date NOT NULL,
  `utilisateur_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `journal_activite`
--

CREATE TABLE `journal_activite` (
  `id` int(11) NOT NULL,
  `utilisateur_id` int(11) DEFAULT NULL,
  `action` varchar(100) NOT NULL,
  `details` text DEFAULT NULL,
  `date_action` datetime NOT NULL DEFAULT current_timestamp(),
  `adresse_ip` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `journal_activite`
--

INSERT INTO `journal_activite` (`id`, `utilisateur_id`, `action`, `details`, `date_action`, `adresse_ip`) VALUES
(1, 2, 'INSCRIPTION', 'Nouvelle inscription', '2026-03-20 01:51:06', NULL),
(2, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 01:52:35', NULL),
(3, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 02:00:36', NULL),
(4, 2, 'DECONNEXION', 'Déconnexion', '2026-03-20 02:01:45', NULL),
(5, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 02:32:11', NULL),
(6, 2, 'DECONNEXION', 'Déconnexion', '2026-03-20 03:10:04', NULL),
(7, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 04:21:00', NULL),
(8, 2, 'AJOUT_TRANSACTION', 'SORTIE : 2000.0 FCFA', '2026-03-20 04:22:55', NULL),
(9, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 04:41:21', NULL),
(10, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 04:48:09', NULL),
(11, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 04:57:41', NULL),
(12, 2, 'AJOUT_TRANSACTION', 'ENTREE : 5000.0 FCFA', '2026-03-20 04:59:19', NULL),
(13, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 05:24:12', NULL),
(14, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 05:26:11', NULL),
(15, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 05:27:33', NULL),
(16, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 05:30:16', NULL),
(17, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 05:39:42', NULL),
(18, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 05:50:55', NULL),
(19, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 06:05:11', NULL),
(20, 2, 'MODIFICATION_PROFIL', 'Profil mis à jour', '2026-03-20 06:09:43', NULL),
(21, 2, 'DECONNEXION', 'Déconnexion', '2026-03-20 06:09:51', NULL),
(22, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 06:10:24', NULL),
(23, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 06:31:52', NULL),
(24, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 06:52:38', NULL),
(25, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 07:07:38', NULL),
(26, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 07:12:04', NULL),
(27, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 20:25:46', NULL),
(28, 2, 'DECONNEXION', 'Déconnexion', '2026-03-20 20:33:00', NULL),
(29, 2, 'CONNEXION', 'Connexion depuis l\'application', '2026-03-20 20:34:04', NULL),
(30, 2, 'AJOUT_TRANSACTION', 'SORTIE : 200.0 FCFA', '2026-03-20 20:42:36', NULL),
(31, 2, 'AJOUT_TRANSACTION', 'SORTIE : 5000.0 FCFA', '2026-03-20 20:49:04', NULL),
(32, 2, 'AJOUT_TRANSACTION', 'ENTREE : 200000.0 FCFA', '2026-03-20 20:55:40', NULL),
(33, 2, 'AJOUT_TRANSACTION', 'SORTIE : 400.0 FCFA', '2026-03-20 21:14:19', NULL),
(34, 2, 'DECONNEXION', 'Déconnexion', '2026-03-20 21:17:26', NULL),
(35, 3, 'INSCRIPTION', 'Nouvelle inscription', '2026-03-20 21:18:05', NULL),
(36, 3, 'DECONNEXION', 'Déconnexion', '2026-03-20 21:18:17', NULL),
(37, 2, 'CONNEXION', 'Connexion', '2026-03-20 21:54:27', NULL),
(38, 2, 'CONNEXION', 'Connexion', '2026-03-20 22:01:31', NULL),
(39, 2, 'AJOUT_TRANSACTION', 'SORTIE : 40000.0 FCFA', '2026-03-20 22:06:25', NULL),
(40, 2, 'AJOUT_TRANSACTION', 'SORTIE : 1000.0 FCFA', '2026-03-20 22:08:37', NULL),
(41, 2, 'AJOUT_TRANSACTION', 'SORTIE : 600.0 FCFA', '2026-03-20 22:12:01', NULL),
(42, 2, 'AJOUT_TRANSACTION', 'SORTIE : 58000.0 FCFA', '2026-03-20 22:13:18', NULL),
(43, 2, 'AJOUT_TRANSACTION', 'ENTREE : 90000.0 FCFA', '2026-03-20 22:23:25', NULL),
(44, 2, 'AJOUT_TRANSACTION', 'ENTREE : 30000.0 FCFA', '2026-03-20 22:23:46', NULL),
(45, 2, 'DECONNEXION', 'Déconnexion', '2026-03-20 22:48:45', NULL),
(46, 2, 'CONNEXION', 'Connexion', '2026-03-20 22:53:44', NULL),
(47, 2, 'CONNEXION', 'Connexion', '2026-03-20 23:02:02', NULL),
(48, 2, 'CONNEXION', 'Connexion', '2026-03-20 23:02:26', NULL),
(49, 2, 'CONNEXION', 'Connexion', '2026-03-20 23:04:04', NULL),
(50, 2, 'CONNEXION', 'Connexion', '2026-03-20 23:19:38', NULL),
(51, 2, 'DECONNEXION', 'Déconnexion', '2026-03-20 23:29:40', NULL),
(52, 2, 'CONNEXION', 'Connexion', '2026-03-20 23:30:06', NULL),
(53, 2, 'CONNEXION', 'Connexion', '2026-03-22 22:49:55', NULL),
(54, 2, 'AJOUT_TRANSACTION', 'ENTREE : 3000.0 FCFA', '2026-03-22 22:52:37', NULL),
(55, 2, 'CONNEXION', 'Connexion', '2026-03-22 22:59:40', NULL),
(56, 2, 'CONNEXION', 'Connexion', '2026-03-22 23:13:08', NULL),
(57, 2, 'AJOUT_TRANSACTION', 'SORTIE : 4000.0 FCFA', '2026-03-22 23:14:15', NULL),
(58, 2, 'CONNEXION', 'Connexion', '2026-03-23 05:11:01', NULL),
(59, 2, 'CONNEXION', 'Connexion', '2026-03-23 05:14:53', NULL),
(60, 2, 'CONNEXION', 'Connexion', '2026-03-23 05:19:13', NULL),
(61, 2, 'CONNEXION', 'Connexion', '2026-03-23 05:33:20', NULL),
(62, 2, 'CONNEXION', 'Connexion', '2026-03-23 05:46:25', NULL),
(63, 2, 'CONNEXION', 'Connexion', '2026-03-23 05:52:13', NULL),
(64, 2, 'CONNEXION', 'Connexion', '2026-03-23 16:21:07', NULL),
(65, 2, 'CONNEXION', 'Connexion', '2026-03-23 16:44:53', NULL),
(66, 2, 'DECONNEXION', 'Déconnexion', '2026-03-23 17:16:46', NULL),
(67, 2, 'CONNEXION', 'Connexion', '2026-03-23 17:17:05', NULL),
(68, 2, 'DECONNEXION', 'Déconnexion', '2026-03-23 18:24:34', NULL),
(69, 2, 'CONNEXION', 'Connexion', '2026-03-25 09:36:28', NULL),
(70, 2, 'CONNEXION', 'Connexion', '2026-03-26 18:49:47', NULL),
(71, 2, 'DECONNEXION', 'Déconnexion', '2026-03-26 18:51:15', NULL),
(72, 2, 'CONNEXION', 'Connexion', '2026-03-29 02:28:38', NULL),
(73, 2, 'DECONNEXION', 'Déconnexion', '2026-03-29 02:45:33', NULL),
(74, 2, 'CONNEXION', 'Connexion', '2026-03-29 23:42:53', NULL),
(75, 2, 'DECONNEXION', 'Déconnexion', '2026-03-29 23:45:40', NULL),
(76, 2, 'CONNEXION', 'Connexion', '2026-03-29 23:46:18', NULL),
(77, 2, 'DECONNEXION', 'Déconnexion', '2026-03-29 23:47:01', NULL),
(78, 2, 'CONNEXION', 'Connexion', '2026-03-30 01:24:49', NULL),
(79, 2, 'DECONNEXION', 'Déconnexion', '2026-03-30 01:26:24', NULL),
(80, 2, 'CONNEXION', 'Connexion', '2026-03-30 01:26:52', NULL),
(81, 2, 'DECONNEXION', 'Déconnexion', '2026-03-30 01:27:09', NULL),
(82, 2, 'CONNEXION', 'Connexion', '2026-03-30 01:28:12', NULL),
(83, 2, 'DECONNEXION', 'Déconnexion', '2026-03-30 01:28:25', NULL),
(84, 2, 'CONNEXION', 'Connexion', '2026-03-31 21:25:52', NULL),
(85, 2, 'AJOUT_TRANSACTION', 'SORTIE : 98765.0 FCFA', '2026-03-31 21:28:35', NULL),
(86, 2, 'DECONNEXION', 'Déconnexion', '2026-03-31 21:47:41', NULL),
(87, NULL, 'INSCRIPTION', 'Nouvelle inscription', '2026-03-31 21:49:03', NULL),
(88, NULL, 'AJOUT_TRANSACTION', 'ENTREE : 9999.0 FCFA', '2026-03-31 21:50:17', NULL),
(89, NULL, 'AJOUT_TRANSACTION', 'ENTREE : 9.99999999E8 FCFA', '2026-03-31 21:51:01', NULL),
(90, NULL, 'AJOUT_TRANSACTION', 'ENTREE : 5.0E11 FCFA', '2026-03-31 21:51:29', NULL),
(91, NULL, 'AJOUT_TRANSACTION', 'SORTIE : 500000.0 FCFA', '2026-03-31 21:55:30', NULL),
(92, 2, 'CONNEXION', 'Connexion', '2026-04-01 10:49:10', NULL),
(93, 2, 'CONNEXION', 'Connexion', '2026-04-01 11:19:08', NULL),
(94, 2, 'AJOUT_TRANSACTION', 'SORTIE : 100000.0 FCFA', '2026-04-01 11:20:45', NULL),
(95, 2, 'CONNEXION', 'Connexion', '2026-04-01 11:29:14', NULL),
(96, 2, 'CONNEXION', 'Connexion', '2026-04-01 11:33:02', NULL),
(97, 2, 'CONNEXION', 'Connexion', '2026-04-07 23:35:56', NULL),
(98, 2, 'CONNEXION', 'Connexion', '2026-04-09 02:50:52', NULL),
(99, 2, 'CONNEXION', 'Connexion', '2026-04-09 15:54:59', NULL),
(100, 2, 'DECONNEXION', 'Déconnexion', '2026-04-09 15:57:07', NULL),
(101, 2, 'CONNEXION', 'Connexion', '2026-04-09 16:16:13', NULL),
(102, 2, 'CONNEXION', 'Connexion', '2026-04-09 16:18:31', NULL),
(103, 2, 'CONNEXION', 'Connexion', '2026-04-09 16:19:29', NULL),
(104, 2, 'CONNEXION', 'Connexion', '2026-04-09 16:49:26', NULL),
(105, 2, 'DECONNEXION', 'Déconnexion', '2026-04-09 16:49:42', NULL),
(106, 2, 'CONNEXION', 'Connexion', '2026-04-09 21:52:20', NULL),
(107, 2, 'DECONNEXION', 'Déconnexion', '2026-04-09 21:52:29', NULL),
(108, 5, 'INSCRIPTION', 'Nouvelle inscription', '2026-04-09 22:34:24', NULL),
(109, 5, 'DECONNEXION', 'Déconnexion', '2026-04-09 22:34:41', NULL),
(110, 5, 'CONNEXION', 'Connexion', '2026-04-09 22:35:17', NULL),
(111, 5, 'DECONNEXION', 'Déconnexion', '2026-04-09 22:35:51', NULL),
(112, 5, 'CONNEXION', 'Connexion', '2026-04-10 00:20:36', NULL),
(113, 5, 'DECONNEXION', 'Déconnexion', '2026-04-10 00:21:09', NULL),
(114, 5, 'CONNEXION', 'Connexion', '2026-04-10 00:39:38', NULL),
(115, 5, 'DECONNEXION', 'Déconnexion', '2026-04-10 00:39:43', NULL),
(116, 6, 'INSCRIPTION', 'Nouvelle inscription', '2026-04-10 00:41:27', NULL),
(117, 6, 'DECONNEXION', 'Déconnexion', '2026-04-10 00:41:30', NULL),
(118, 5, 'CONNEXION', 'Connexion', '2026-04-10 00:58:25', NULL),
(119, 5, 'CONNEXION', 'Connexion', '2026-04-10 01:01:52', NULL),
(120, NULL, 'INSCRIPTION', 'Nouvelle inscription', '2026-04-10 15:34:55', NULL),
(121, NULL, 'AJOUT_TRANSACTION', 'ENTREE : 899.0 FCFA', '2026-04-10 15:36:06', NULL),
(122, NULL, 'DECONNEXION', 'Déconnexion', '2026-04-10 15:37:41', NULL),
(123, 2, 'CONNEXION', 'Connexion', '2026-04-10 16:24:24', NULL),
(124, 5, 'CONNEXION', 'Connexion', '2026-04-10 18:32:41', NULL),
(125, 2, 'CONNEXION', 'Connexion', '2026-04-10 19:15:39', NULL),
(126, 2, 'CONNEXION', 'Connexion', '2026-04-10 19:48:06', NULL),
(127, 2, 'CONNEXION', 'Connexion', '2026-04-10 19:50:06', NULL),
(128, 2, 'CONNEXION', 'Connexion', '2026-04-10 19:52:31', NULL),
(129, 2, 'CONNEXION', 'Connexion', '2026-04-10 22:55:18', NULL),
(130, 2, 'DECONNEXION', 'Déconnexion', '2026-04-10 22:55:28', NULL),
(131, 2, 'CONNEXION', 'Connexion', '2026-04-10 22:57:29', NULL),
(132, 2, 'CONNEXION', 'Connexion', '2026-04-10 23:05:29', NULL),
(133, 2, 'DECONNEXION', 'Déconnexion', '2026-04-10 23:07:19', NULL),
(134, 2, 'CONNEXION', 'Connexion', '2026-04-10 23:10:22', NULL),
(135, 2, 'DECONNEXION', 'Déconnexion', '2026-04-10 23:10:34', NULL),
(136, 2, 'CONNEXION', 'Connexion', '2026-04-10 23:13:07', NULL),
(137, 2, 'DECONNEXION', 'Déconnexion', '2026-04-10 23:13:59', NULL),
(138, 2, 'CONNEXION', 'Connexion', '2026-04-10 23:18:14', NULL),
(139, 2, 'DECONNEXION', 'Déconnexion', '2026-04-10 23:18:41', NULL),
(140, 2, 'CONNEXION', 'Connexion', '2026-04-10 23:19:34', NULL),
(141, 2, 'CONNEXION', 'Connexion', '2026-04-11 00:19:38', NULL),
(142, 2, 'CONNEXION', 'Connexion', '2026-04-11 00:55:09', NULL),
(143, 2, 'CONNEXION', 'Connexion', '2026-04-11 00:55:41', NULL),
(144, 2, 'CONNEXION', 'Connexion', '2026-04-11 00:57:08', NULL),
(145, 2, 'CONNEXION', 'Connexion', '2026-04-11 00:57:37', NULL),
(146, 5, 'CONNEXION', 'Connexion', '2026-04-11 01:49:35', NULL),
(147, 2, 'CONNEXION', 'Connexion', '2026-04-11 01:49:58', NULL),
(148, 2, 'CONNEXION', 'Connexion', '2026-04-11 01:56:28', NULL),
(149, 2, 'CONNEXION', 'Connexion', '2026-04-11 01:57:03', NULL),
(150, 2, 'CONNEXION', 'Connexion', '2026-04-11 01:59:11', NULL),
(151, 2, 'CONNEXION', 'Connexion', '2026-04-11 02:04:06', NULL),
(152, 5, 'CONNEXION', 'Connexion', '2026-04-11 02:06:38', NULL),
(153, 2, 'CONNEXION', 'Connexion', '2026-04-11 02:16:55', NULL),
(154, 2, 'DECONNEXION', 'Déconnexion', '2026-04-11 02:17:31', NULL),
(155, 5, 'CONNEXION', 'Connexion', '2026-04-11 02:17:43', NULL),
(156, 5, 'DECONNEXION', 'Déconnexion', '2026-04-11 02:17:56', NULL),
(157, 2, 'CONNEXION', 'Connexion', '2026-04-11 02:18:24', NULL),
(158, 2, 'DECONNEXION', 'Déconnexion', '2026-04-11 02:18:29', NULL),
(159, 2, 'CONNEXION', 'Connexion', '2026-04-11 02:19:49', NULL),
(160, 2, 'CONNEXION', 'Connexion', '2026-04-11 02:21:14', NULL),
(161, 2, 'CONNEXION', 'Connexion', '2026-04-11 02:22:59', NULL),
(162, 2, 'DECONNEXION', 'Déconnexion', '2026-04-11 02:29:53', NULL),
(163, 5, 'CONNEXION', 'Connexion', '2026-04-11 02:30:02', NULL),
(164, 5, 'DECONNEXION', 'Déconnexion', '2026-04-11 02:30:35', NULL),
(165, 2, 'CONNEXION', 'Connexion', '2026-04-11 02:40:57', NULL),
(166, 2, 'CONNEXION', 'Connexion', '2026-04-11 02:45:10', NULL),
(167, 2, 'CONNEXION', 'Connexion', '2026-04-11 02:53:00', NULL),
(168, 2, 'CONNEXION', 'Connexion', '2026-04-11 02:56:38', NULL),
(169, 2, 'DECONNEXION', 'Déconnexion', '2026-04-11 02:57:41', NULL),
(170, 5, 'CONNEXION', 'Connexion', '2026-04-11 02:57:56', NULL),
(171, 5, 'DECONNEXION', 'Déconnexion', '2026-04-11 02:59:41', NULL),
(172, 5, 'CONNEXION', 'Connexion', '2026-04-11 03:00:12', NULL),
(173, 5, 'AJOUT_TRANSACTION', 'ENTREE : 100000.0 FCFA', '2026-04-11 03:00:38', NULL),
(174, 5, 'DECONNEXION', 'Déconnexion', '2026-04-11 03:01:46', NULL),
(175, 2, 'CONNEXION', 'Connexion', '2026-04-11 03:14:40', NULL),
(176, 2, 'DECONNEXION', 'Déconnexion', '2026-04-11 03:15:44', NULL),
(177, 5, 'CONNEXION', 'Connexion', '2026-04-11 03:15:54', NULL),
(178, 2, 'CONNEXION', 'Connexion', '2026-04-11 03:24:02', NULL),
(179, 2, 'CONNEXION', 'Connexion', '2026-04-11 03:31:35', NULL),
(180, 2, 'DECONNEXION', 'Déconnexion', '2026-04-11 03:32:09', NULL),
(181, 2, 'CONNEXION', 'Connexion', '2026-04-11 03:33:37', NULL),
(182, 2, 'DECONNEXION', 'Déconnexion', '2026-04-11 03:34:17', NULL),
(183, 2, 'CONNEXION', 'Connexion', '2026-04-11 03:34:28', NULL),
(184, 2, 'DECONNEXION', 'Déconnexion', '2026-04-11 03:35:41', NULL),
(185, 2, 'CONNEXION', 'Connexion', '2026-04-11 03:36:19', NULL),
(186, 2, 'DECONNEXION', 'Déconnexion', '2026-04-11 03:36:42', NULL),
(187, 6, 'CONNEXION', 'Connexion', '2026-04-11 03:38:11', NULL),
(188, 2, 'CONNEXION', 'Connexion', '2026-04-11 03:54:01', NULL),
(189, 2, 'DECONNEXION', 'Déconnexion', '2026-04-11 03:57:20', NULL),
(190, 3, 'CONNEXION', 'Connexion', '2026-04-11 03:57:31', NULL),
(191, 2, 'CONNEXION', 'Connexion', '2026-04-11 04:21:58', NULL),
(192, 2, 'CONNEXION', 'Connexion', '2026-04-11 04:37:15', NULL),
(193, 2, 'CONNEXION', 'Connexion', '2026-04-11 04:38:46', NULL),
(194, 2, 'CONNEXION', 'Connexion', '2026-04-11 04:41:50', NULL),
(195, 2, 'CONNEXION', 'Connexion', '2026-04-11 04:44:30', NULL),
(196, 2, 'DECONNEXION', 'Déconnexion', '2026-04-11 20:44:05', NULL),
(197, 5, 'CONNEXION', 'Connexion', '2026-04-11 20:44:58', NULL),
(198, 5, 'DECONNEXION', 'Déconnexion', '2026-04-11 20:45:11', NULL),
(199, 2, 'CONNEXION', 'Connexion', '2026-04-11 20:45:42', NULL),
(200, 2, 'DECONNEXION', 'Déconnexion', '2026-04-11 20:50:30', NULL),
(201, 2, 'CONNEXION', 'Connexion', '2026-04-11 20:51:27', NULL),
(202, 2, 'AJOUT_TRANSACTION', 'ENTREE : 2000.0 FCFA', '2026-04-11 20:55:12', NULL),
(203, 2, 'AJOUT_TRANSACTION', 'ENTREE : 3000.0 FCFA', '2026-04-11 20:55:49', NULL),
(204, 2, 'AJOUT_TRANSACTION', 'SORTIE : 5600.0 FCFA', '2026-04-11 20:56:39', NULL),
(205, 2, 'AJOUT_TRANSACTION', 'ENTREE : 1000000.0 FCFA', '2026-04-11 20:57:03', NULL),
(206, 2, 'AJOUT_TRANSACTION', 'SORTIE : 125000.0 FCFA', '2026-04-11 21:00:24', NULL),
(207, 2, 'CONNEXION', 'Connexion', '2026-04-12 00:04:38', NULL),
(208, 2, 'AJOUT_TRANSACTION', 'SORTIE : 800000.0 FCFA', '2026-04-12 00:08:03', NULL),
(209, 2, 'CONNEXION', 'Connexion', '2026-04-12 00:20:31', NULL),
(210, 2, 'CONNEXION', 'Connexion', '2026-04-12 00:30:05', NULL),
(211, 2, 'AJOUT_TRANSACTION', 'SORTIE : 50000.0 FCFA', '2026-04-12 00:31:50', NULL),
(212, 2, 'CONNEXION', 'Connexion', '2026-04-12 00:38:50', NULL),
(213, 2, 'CONNEXION', 'Connexion', '2026-04-12 00:42:03', NULL),
(214, 2, 'CONNEXION', 'Connexion', '2026-04-12 01:00:43', NULL),
(215, 2, 'CONNEXION', 'Connexion', '2026-04-12 01:08:12', NULL),
(216, 2, 'CONNEXION', 'Connexion', '2026-04-12 01:09:00', NULL),
(217, 2, 'CONNEXION', 'Connexion', '2026-04-12 01:12:35', NULL),
(218, 2, 'CONNEXION', 'Connexion', '2026-04-12 01:17:50', NULL),
(219, 2, 'CONNEXION', 'Connexion', '2026-04-12 01:23:27', NULL),
(220, 2, 'CONNEXION', 'Connexion', '2026-04-12 01:36:11', NULL),
(221, 2, 'CONNEXION', 'Connexion', '2026-04-12 01:41:16', NULL),
(222, 2, 'CONNEXION', 'Connexion', '2026-04-12 01:42:20', NULL),
(223, 2, 'DECONNEXION', 'Déconnexion', '2026-04-12 01:43:56', NULL),
(224, 5, 'CONNEXION', 'Connexion', '2026-04-12 01:44:05', NULL),
(225, 5, 'DECONNEXION', 'Déconnexion', '2026-04-12 01:47:10', NULL),
(226, 2, 'CONNEXION', 'Connexion', '2026-04-12 01:48:19', NULL),
(227, 2, 'DECONNEXION', 'Déconnexion', '2026-04-12 01:48:34', NULL),
(228, 2, 'CONNEXION', 'Connexion', '2026-04-12 01:49:06', NULL),
(229, 2, 'DECONNEXION', 'Déconnexion', '2026-04-12 01:49:11', NULL),
(230, 2, 'CONNEXION', 'Connexion', '2026-04-12 01:49:40', NULL),
(231, 2, 'CONNEXION', 'Connexion', '2026-04-12 02:31:24', NULL),
(232, 2, 'CONNEXION', 'Connexion', '2026-04-12 02:34:53', NULL),
(233, 2, 'CONNEXION', 'Connexion', '2026-04-12 02:39:47', NULL),
(234, 2, 'DECONNEXION', 'Déconnexion', '2026-04-12 02:42:16', NULL),
(235, 5, 'CONNEXION', 'Connexion', '2026-04-12 02:42:27', NULL),
(236, 2, 'CONNEXION', 'Connexion', '2026-04-12 02:48:36', NULL),
(237, 2, 'CONNEXION', 'Connexion', '2026-04-12 03:01:41', NULL),
(238, 2, 'CONNEXION', 'Connexion', '2026-04-12 03:05:04', NULL),
(239, 2, 'CONNEXION', 'Connexion', '2026-04-12 03:08:32', NULL),
(240, 2, 'CONNEXION', 'Connexion', '2026-04-12 03:12:59', NULL),
(241, 2, 'CONNEXION', 'Connexion', '2026-04-12 03:14:40', NULL),
(242, 2, 'CONNEXION', 'Connexion', '2026-04-12 03:17:08', NULL),
(243, 2, 'CONNEXION', 'Connexion', '2026-04-12 03:17:54', NULL),
(244, 2, 'CONNEXION', 'Connexion', '2026-04-12 03:20:01', NULL),
(245, 2, 'CONNEXION', 'Connexion', '2026-04-12 03:25:56', NULL),
(246, 2, 'CONNEXION', 'Connexion', '2026-04-12 03:26:56', NULL),
(247, 2, 'DECONNEXION', 'Déconnexion', '2026-04-12 03:27:27', NULL),
(248, 5, 'CONNEXION', 'Connexion', '2026-04-12 03:27:45', NULL),
(249, 5, 'DECONNEXION', 'Déconnexion', '2026-04-12 03:28:32', NULL),
(250, 2, 'CONNEXION', 'Connexion', '2026-04-12 03:28:44', NULL),
(251, 2, 'DECONNEXION', 'Déconnexion', '2026-04-12 03:29:39', NULL),
(252, 2, 'CONNEXION', 'Connexion', '2026-04-12 03:49:33', NULL),
(253, 2, 'DECONNEXION', 'Déconnexion', '2026-04-12 03:50:09', NULL),
(254, 5, 'CONNEXION', 'Connexion', '2026-04-12 03:50:17', NULL),
(255, 5, 'DECONNEXION', 'Déconnexion', '2026-04-12 03:51:21', NULL),
(256, 2, 'CONNEXION', 'Connexion', '2026-04-12 03:51:32', NULL),
(257, 2, 'AJOUT_TRANSACTION', 'SORTIE : 20000.0 FCFA', '2026-04-12 03:54:19', NULL),
(258, 2, 'CONNEXION', 'Connexion', '2026-04-12 04:00:54', NULL),
(259, 2, 'DECONNEXION', 'Déconnexion', '2026-04-12 04:01:58', NULL),
(260, 5, 'CONNEXION', 'Connexion', '2026-04-12 04:02:07', NULL),
(261, 5, 'DECONNEXION', 'Déconnexion', '2026-04-12 04:02:22', NULL),
(262, 2, 'CONNEXION', 'Connexion', '2026-04-12 04:02:34', NULL),
(263, 2, 'CONNEXION', 'Connexion', '2026-04-12 11:54:18', NULL),
(264, 2, 'DECONNEXION', 'Déconnexion', '2026-04-12 11:55:08', NULL),
(265, 5, 'CONNEXION', 'Connexion', '2026-04-12 11:55:22', NULL),
(266, 5, 'DECONNEXION', 'Déconnexion', '2026-04-12 11:55:57', NULL),
(267, 5, 'CONNEXION', 'Connexion', '2026-04-12 12:53:06', NULL),
(268, 5, 'DECONNEXION', 'Déconnexion', '2026-04-12 12:53:14', NULL),
(269, 2, 'CONNEXION', 'Connexion', '2026-04-12 12:53:30', NULL),
(270, 2, 'DECONNEXION', 'Déconnexion', '2026-04-12 12:53:39', NULL),
(271, 2, 'CONNEXION', 'Connexion', '2026-04-12 12:54:10', NULL),
(272, 2, 'DECONNEXION', 'Déconnexion', '2026-04-12 12:54:18', NULL),
(273, 5, 'CONNEXION', 'Connexion', '2026-04-12 12:54:31', NULL),
(274, 5, 'DECONNEXION', 'Déconnexion', '2026-04-12 12:54:39', NULL),
(275, 2, 'CONNEXION', 'Connexion', '2026-04-12 12:55:47', NULL),
(276, 2, 'DECONNEXION', 'Déconnexion', '2026-04-12 12:55:52', NULL),
(277, 5, 'CONNEXION', 'Connexion', '2026-04-12 12:56:03', NULL),
(278, 5, 'DECONNEXION', 'Déconnexion', '2026-04-12 12:57:22', NULL),
(279, 5, 'CONNEXION', 'Connexion', '2026-04-12 13:00:00', NULL),
(280, 5, 'DECONNEXION', 'Déconnexion', '2026-04-12 13:00:11', NULL),
(281, 8, 'INSCRIPTION', 'Nouvelle inscription', '2026-04-12 13:01:22', NULL),
(282, 2, 'CONNEXION', 'Connexion', '2026-04-12 13:15:41', NULL),
(283, 2, 'DECONNEXION', 'Déconnexion', '2026-04-12 13:16:10', NULL),
(284, 5, 'CONNEXION', 'Connexion', '2026-04-12 13:16:22', NULL),
(285, 5, 'AJOUT_TRANSACTION', 'SORTIE : 75000.0 FCFA', '2026-04-12 13:17:06', NULL),
(286, 5, 'DECONNEXION', 'Déconnexion', '2026-04-12 13:24:17', NULL),
(287, 5, 'CONNEXION', 'Connexion', '2026-04-12 13:25:13', NULL),
(288, 5, 'DECONNEXION', 'Déconnexion', '2026-04-12 13:25:48', NULL),
(289, 2, 'CONNEXION', 'Connexion', '2026-04-12 19:11:10', NULL),
(290, 2, 'DECONNEXION', 'Déconnexion', '2026-04-12 19:11:25', NULL),
(291, 2, 'CONNEXION', 'Connexion', '2026-04-12 19:11:59', NULL),
(292, 2, 'DECONNEXION', 'Déconnexion', '2026-04-12 19:12:12', NULL),
(293, 5, 'CONNEXION', 'Connexion', '2026-04-12 19:12:39', NULL),
(294, 5, 'DECONNEXION', 'Déconnexion', '2026-04-12 19:15:46', NULL),
(295, 2, 'CONNEXION', 'Connexion', '2026-04-12 19:16:34', NULL),
(296, 2, 'CONNEXION', 'Connexion', '2026-04-12 19:25:28', NULL),
(297, 2, 'DECONNEXION', 'Déconnexion', '2026-04-12 19:26:13', NULL),
(298, 2, 'CONNEXION', 'Connexion', '2026-04-12 21:26:23', NULL),
(299, 2, 'DECONNEXION', 'Déconnexion', '2026-04-12 21:27:12', NULL),
(300, 5, 'CONNEXION', 'Connexion', '2026-04-12 21:27:27', NULL),
(301, 2, 'CONNEXION', 'Connexion', '2026-04-12 22:20:39', NULL),
(302, 2, 'DECONNEXION', 'Déconnexion', '2026-04-13 13:55:30', NULL),
(303, 5, 'CONNEXION', 'Connexion', '2026-04-13 14:49:12', NULL),
(304, 5, 'AJOUT_TRANSACTION', 'SORTIE : 2000.0 FCFA', '2026-04-13 14:51:23', NULL),
(305, 5, 'DECONNEXION', 'Déconnexion', '2026-04-13 14:56:51', NULL),
(306, 2, 'CONNEXION', 'Connexion', '2026-04-13 14:57:06', NULL),
(307, 2, 'DECONNEXION', 'Déconnexion', '2026-04-13 15:00:32', NULL),
(308, 2, 'CONNEXION', 'Connexion', '2026-04-21 00:27:07', NULL),
(309, 2, 'DECONNEXION', 'Déconnexion', '2026-04-21 00:45:12', NULL),
(310, 2, 'CONNEXION', 'Connexion', '2026-04-21 00:45:49', NULL),
(311, 2, 'DECONNEXION', 'Déconnexion', '2026-04-21 00:46:05', NULL),
(312, 2, 'CONNEXION', 'Connexion', '2026-04-21 00:46:16', NULL),
(313, 2, 'DECONNEXION', 'Déconnexion', '2026-04-21 00:46:44', NULL),
(314, 5, 'CONNEXION', 'Connexion', '2026-04-21 00:46:52', NULL),
(315, 5, 'DECONNEXION', 'Déconnexion', '2026-04-21 00:49:42', NULL),
(316, 2, 'CONNEXION', 'Connexion', '2026-04-23 22:15:38', NULL),
(317, 2, 'DECONNEXION', 'Déconnexion', '2026-04-23 22:15:50', NULL),
(318, 5, 'CONNEXION', 'Connexion', '2026-04-23 22:16:01', NULL),
(319, 5, 'DECONNEXION', 'Déconnexion', '2026-04-23 22:22:59', NULL),
(320, 2, 'CONNEXION', 'Connexion', '2026-04-23 22:23:28', NULL),
(321, 2, 'DECONNEXION', 'Déconnexion', '2026-04-23 22:26:20', NULL),
(322, 5, 'CONNEXION', 'Connexion', '2026-04-23 22:26:34', NULL),
(323, 5, 'AJOUT_TRANSACTION', 'SORTIE : 10000.0 FCFA', '2026-04-23 22:27:07', NULL),
(324, 5, 'AJOUT_TRANSACTION', 'ENTREE : 2000.0 FCFA', '2026-04-23 22:27:59', NULL),
(325, 5, 'AJOUT_TRANSACTION', 'SORTIE : 8000.0 FCFA', '2026-04-23 22:29:04', NULL),
(326, 5, 'AJOUT_TRANSACTION', 'SORTIE : 1000.0 FCFA', '2026-04-23 22:29:33', NULL),
(327, 5, 'AJOUT_TRANSACTION', 'SORTIE : 2000.0 FCFA', '2026-04-23 22:29:51', NULL),
(328, 5, 'DECONNEXION', 'Déconnexion', '2026-04-23 22:39:38', NULL),
(329, 9, 'INSCRIPTION', 'Nouvelle inscription', '2026-04-23 22:43:35', NULL),
(330, 9, 'CONNEXION', 'Connexion', '2026-04-23 22:47:05', NULL);

-- --------------------------------------------------------

--
-- Structure de la table `question_securite`
--

CREATE TABLE `question_securite` (
  `id` int(11) NOT NULL,
  `question` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `question_securite`
--

INSERT INTO `question_securite` (`id`, `question`) VALUES
(1, 'Quel est le prénom de votre mère ?'),
(2, 'Quel est le nom de votre animal de compagnie d\'enfance ?'),
(3, 'Dans quelle ville êtes-vous né(e) ?'),
(4, 'Quel est le nom de votre école primaire ?'),
(5, 'Quel était le surnom de votre meilleur(e) ami(e) d\'enfance ?'),
(6, 'Quel est le prénom de votre père ?'),
(7, 'Quelle est la marque de votre premier téléphone ?'),
(8, 'Quel est le nom de rue où vous avez grandi ?'),
(9, 'Quel est votre plat préféré d\'enfance ?'),
(10, 'Quel est le prénom de votre grand-mère maternelle ?');

-- --------------------------------------------------------

--
-- Structure de la table `reponse_securite`
--

CREATE TABLE `reponse_securite` (
  `id` int(11) NOT NULL,
  `utilisateur_id` int(11) NOT NULL,
  `question_id` int(11) NOT NULL,
  `reponse` varchar(255) NOT NULL COMMENT 'Réponse hashée avec BCrypt'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `reponse_securite`
--

INSERT INTO `reponse_securite` (`id`, `utilisateur_id`, `question_id`, `reponse`) VALUES
(1, 5, 1, '$2a$10$dCDrf2X9RjWONjVctluE0e/K/CwljtNxphOO/vHxaAlzG4dT7fajG'),
(2, 5, 7, '$2a$10$R9JfEi1rWmxsnobs5mBmDehZehN4ri/gOAja6knODvlSdms8tB1/a'),
(3, 5, 10, '$2a$10$mqpe6i5B1lzEZn/yMXMKzOhX6AoKCyHa05WfC8I/22xGkS8jHmE7G'),
(4, 6, 3, '$2a$10$QhNxQGttUGSGaHagshtDLegvFlbEdZK5jdF.jOkw7eeJyQsbnTc0W'),
(5, 6, 2, '$2a$10$nyiYukk.zACWYGkHJjajsuPizOtKXzPsgmnLvlEuii1j50h6iEpOe'),
(6, 6, 7, '$2a$10$spWF1Gm4bWnoWN6SwsxaA.qFFl9KSYwKrlOzSM1aPLS4KU6rLWy1u'),
(10, 8, 1, '$2a$10$aJHZCJT35kqrPxnhty0csOESU6yeial0zG8ZGOlHHMrOHEvB57JNa'),
(11, 8, 2, '$2a$10$iWZryGqLtR6KHpu8NtlV9uaFzI/Jubq4FlZ64u94YA5unFML1qtPK'),
(12, 8, 6, '$2a$10$R9rG8kGLoT5tx2dBvV.wLOSSD29aDICU60NOkJX.6jlqKNOhJvvdG'),
(13, 9, 1, '$2a$10$t48Q3VhKAvwZrLiLcUcQvOkf3zMQHBS7TbJJfkbxxZyhxDO870A42'),
(14, 9, 4, '$2a$10$7Vqi6TMjCjmixvJs1jqVoeQFmyC/0bKrk2tUYb8xL14rLttBhZrMK'),
(15, 9, 9, '$2a$10$AiZ6vu/SX2YhUJnUfByetORsqlr0jWqfNhZA9SC..dhbVjMn3mF5G');

-- --------------------------------------------------------

--
-- Structure de la table `transaction`
--

CREATE TABLE `transaction` (
  `id` int(11) NOT NULL,
  `montant` decimal(15,2) NOT NULL,
  `type` enum('ENTREE','SORTIE') NOT NULL,
  `date_transaction` date NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `utilisateur_id` int(11) NOT NULL,
  `categorie_id` int(11) NOT NULL,
  `date_saisie` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `transaction`
--

INSERT INTO `transaction` (`id`, `montant`, `type`, `date_transaction`, `description`, `utilisateur_id`, `categorie_id`, `date_saisie`) VALUES
(5, 200000.00, 'ENTREE', '2026-03-20', 'salaire', 2, 7, '2026-03-20 20:55:40'),
(6, 400.00, 'SORTIE', '2026-03-20', '', 2, 2, '2026-03-20 21:14:19'),
(7, 40000.00, 'SORTIE', '2026-03-20', '', 2, 2, '2026-03-20 22:06:25'),
(8, 1000.00, 'SORTIE', '2026-03-20', '', 2, 2, '2026-03-20 22:08:37'),
(9, 600.00, 'SORTIE', '2026-03-20', '', 2, 2, '2026-03-20 22:12:01'),
(10, 58000.00, 'SORTIE', '2026-03-20', '', 2, 2, '2026-03-20 22:13:18'),
(11, 90000.00, 'ENTREE', '2026-03-20', '', 2, 3, '2026-03-20 22:23:25'),
(12, 30000.00, 'ENTREE', '2026-03-20', '', 2, 7, '2026-03-20 22:23:46'),
(13, 3000.00, 'ENTREE', '2026-03-22', '', 2, 6, '2026-03-22 22:52:37'),
(14, 4000.00, 'SORTIE', '2026-03-22', '', 2, 5, '2026-03-22 23:14:15'),
(15, 98765.00, 'SORTIE', '2026-03-31', '', 2, 8, '2026-03-31 21:28:35'),
(20, 100000.00, 'SORTIE', '2026-04-01', '', 2, 8, '2026-04-01 11:20:45'),
(22, 100000.00, 'ENTREE', '2026-04-11', '', 5, 2, '2026-04-11 03:00:38'),
(23, 2000.00, 'ENTREE', '2026-04-11', '', 2, 9, '2026-04-11 20:55:12'),
(24, 3000.00, 'ENTREE', '2026-04-11', '', 2, 9, '2026-04-11 20:55:49'),
(25, 5600.00, 'SORTIE', '2026-04-11', '', 2, 2, '2026-04-11 20:56:39'),
(26, 1000000.00, 'ENTREE', '2026-04-11', '', 2, 1, '2026-04-11 20:57:03'),
(27, 125000.00, 'SORTIE', '2026-04-11', '', 2, 8, '2026-04-11 21:00:24'),
(28, 800000.00, 'SORTIE', '2026-04-12', '', 2, 8, '2026-04-12 00:08:03'),
(29, 50000.00, 'SORTIE', '2026-04-12', '', 2, 8, '2026-04-12 00:31:50'),
(30, 20000.00, 'SORTIE', '2026-04-12', '', 2, 8, '2026-04-12 03:54:19'),
(31, 75000.00, 'SORTIE', '2026-04-12', '', 5, 1, '2026-04-12 13:17:06'),
(32, 2000.00, 'SORTIE', '2026-04-13', '', 5, 4, '2026-04-13 14:51:23'),
(33, 10000.00, 'SORTIE', '2026-04-23', '', 5, 4, '2026-04-23 22:27:07'),
(34, 2000.00, 'ENTREE', '2026-04-23', '', 5, 4, '2026-04-23 22:27:59'),
(35, 8000.00, 'SORTIE', '2026-04-23', '', 5, 4, '2026-04-23 22:29:04'),
(36, 1000.00, 'SORTIE', '2026-04-23', '', 5, 4, '2026-04-23 22:29:33'),
(37, 2000.00, 'SORTIE', '2026-04-23', '', 5, 4, '2026-04-23 22:29:51');

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

CREATE TABLE `utilisateur` (
  `id` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `mot_de_passe` varchar(255) NOT NULL,
  `date_inscription` date NOT NULL DEFAULT curdate(),
  `est_actif` tinyint(1) NOT NULL DEFAULT 1,
  `est_admin` tinyint(1) NOT NULL DEFAULT 0,
  `niveau_acces` int(2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `utilisateur`
--

INSERT INTO `utilisateur` (`id`, `nom`, `email`, `mot_de_passe`, `date_inscription`, `est_actif`, `est_admin`, `niveau_acces`) VALUES
(1, 'Administrateur', 'admin@moneywise.local', '$2a$12$PLACEHOLDER_HASH_BCRYPT', '2026-03-20', 1, 1, 9),
(2, 'mohamed lamarane diallo', 'mohamed.diallo@gmail.com', '$2a$12$BLZVOC7YmgWhL97WDDagXO5aEgTt8LfXEOVsO3tLQcZRN98vIIydG', '2026-03-20', 1, 1, 9),
(3, 'lama', 'lama@gmail.com', '$2a$12$eA5WLHEo.lB8WUIb7fJASul7lNIfOFsJrS6HRlffNJS6NibuxVpzS', '2026-03-20', 1, 0, NULL),
(5, 'diallo', 'diallo@gmail.com', '$2a$12$e8TjXndCSRVklewVmtix.OsYtfAWjp0j346EdRfGUEvwcEhQwhWYq', '2026-04-09', 1, 0, NULL),
(6, 'ousmane', 'ousmane@gmail.com', '$2a$12$k5pk0hafPN6c0fSrRrF5GOPBI5rPvACVX9SUXbfp7.vUp.9U6wJAu', '2026-04-10', 1, 0, NULL),
(8, 'moussa', 'moussa@gmail.com', '$2a$12$FpC2N55.N6KjIPvrWtk/1ehU4l8AP1vncVd3RNm/6MR5aIzu0tKAy', '2026-04-12', 1, 0, NULL),
(9, 'pagaye', 'pagaye@gmail.com', '$2a$12$/Jtw2zDjUpo7aSS443OBtegwD1lDS.O1r1gpSEX82y5P5inzdarPm', '2026-04-23', 1, 0, NULL);

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `alerte`
--
ALTER TABLE `alerte`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_alerte_non_lue` (`budget_id`,`est_lue`);

--
-- Index pour la table `budget`
--
ALTER TABLE `budget`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_budget_user_cat_periode` (`utilisateur_id`,`categorie_id`,`mois`,`annee`),
  ADD KEY `fk_budget_categorie` (`categorie_id`),
  ADD KEY `idx_budget_utilisateur_actif` (`utilisateur_id`,`est_actif`);

--
-- Index pour la table `categorie`
--
ALTER TABLE `categorie`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_categorie_utilisateur` (`utilisateur_id`);

--
-- Index pour la table `export`
--
ALTER TABLE `export`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_export_utilisateur` (`utilisateur_id`);

--
-- Index pour la table `journal_activite`
--
ALTER TABLE `journal_activite`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_journal_utilisateur` (`utilisateur_id`),
  ADD KEY `idx_journal_date` (`date_action`);

--
-- Index pour la table `question_securite`
--
ALTER TABLE `question_securite`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `reponse_securite`
--
ALTER TABLE `reponse_securite`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_user_question` (`utilisateur_id`,`question_id`),
  ADD KEY `fk_rep_question` (`question_id`);

--
-- Index pour la table `transaction`
--
ALTER TABLE `transaction`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_transaction_utilisateur` (`utilisateur_id`),
  ADD KEY `idx_transaction_categorie` (`categorie_id`),
  ADD KEY `idx_transaction_date` (`date_transaction`),
  ADD KEY `idx_transaction_user_date` (`utilisateur_id`,`date_transaction`);

--
-- Index pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_utilisateur_email` (`email`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `alerte`
--
ALTER TABLE `alerte`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT pour la table `budget`
--
ALTER TABLE `budget`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT pour la table `categorie`
--
ALTER TABLE `categorie`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT pour la table `export`
--
ALTER TABLE `export`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `journal_activite`
--
ALTER TABLE `journal_activite`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=331;

--
-- AUTO_INCREMENT pour la table `question_securite`
--
ALTER TABLE `question_securite`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT pour la table `reponse_securite`
--
ALTER TABLE `reponse_securite`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT pour la table `transaction`
--
ALTER TABLE `transaction`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=38;

--
-- AUTO_INCREMENT pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `alerte`
--
ALTER TABLE `alerte`
  ADD CONSTRAINT `fk_alerte_budget` FOREIGN KEY (`budget_id`) REFERENCES `budget` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `budget`
--
ALTER TABLE `budget`
  ADD CONSTRAINT `fk_budget_categorie` FOREIGN KEY (`categorie_id`) REFERENCES `categorie` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_budget_utilisateur` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `categorie`
--
ALTER TABLE `categorie`
  ADD CONSTRAINT `fk_categorie_utilisateur` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `export`
--
ALTER TABLE `export`
  ADD CONSTRAINT `fk_export_utilisateur` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `journal_activite`
--
ALTER TABLE `journal_activite`
  ADD CONSTRAINT `fk_journal_utilisateur` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Contraintes pour la table `reponse_securite`
--
ALTER TABLE `reponse_securite`
  ADD CONSTRAINT `fk_rep_question` FOREIGN KEY (`question_id`) REFERENCES `question_securite` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_rep_utilisateur` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `transaction`
--
ALTER TABLE `transaction`
  ADD CONSTRAINT `fk_transaction_categorie` FOREIGN KEY (`categorie_id`) REFERENCES `categorie` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_transaction_utilisateur` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
