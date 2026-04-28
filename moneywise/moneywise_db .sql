-- phpMyAdmin SQL Dump
-- Base de données : `moneywise_db`
-- Version complète avec données fictives

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

-- --------------------------------------------------------

--
-- Table `categorie`
--

CREATE TABLE `categorie` (
  `id` int(11) NOT NULL,
  `nom` varchar(80) NOT NULL,
  `icone` varchar(50) DEFAULT NULL,
  `est_systeme` tinyint(1) NOT NULL DEFAULT 0,
  `utilisateur_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
(10, 'Crypto', 'bitcoin', 0, 1),
(11, 'Shopping', 'shopping-bag', 0, 2);

-- --------------------------------------------------------

--
-- Table `utilisateur`
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

INSERT INTO `utilisateur` (`id`, `nom`, `email`, `mot_de_passe`, `date_inscription`, `est_actif`, `est_admin`, `niveau_acces`) VALUES
(1, 'Lamarana Diallo', 'admin@gmail.com', '$2a$12$a6076m.8x7rTEakXJHipPeJ.8zPIrTkeUIGuq2H8tsS.LoAgxZwIa', '2026-01-15', 1, 1, 9),
(2, 'Pagaye Ba', 'user@gmail.com', '$2a$12$a6076m.8x7rTEakXJHipPeJ.8zPIrTkeUIGuq2H8tsS.LoAgxZwIa', '2026-02-20', 1, 0, NULL);

-- --------------------------------------------------------

--
-- Table `budget`
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

INSERT INTO `budget` (`id`, `montant_max`, `mois`, `annee`, `est_actif`, `utilisateur_id`, `categorie_id`) VALUES
(1, 300000.00, 4, 2026, 1, 1, 1),
(2, 150000.00, 4, 2026, 1, 1, 2),
(3, 500000.00, 4, 2026, 1, 1, 3),
(4, 100000.00, 4, 2026, 1, 1, 4),
(5, 200000.00, 4, 2026, 1, 1, 5),
(6, 200000.00, 4, 2026, 1, 2, 1),
(7, 80000.00, 4, 2026, 1, 2, 2),
(8, 350000.00, 4, 2026, 1, 2, 3),
(9, 50000.00, 4, 2026, 1, 2, 4),
(10, 100000.00, 4, 2026, 1, 2, 5);

-- --------------------------------------------------------

--
-- Table `transaction`
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

-- Transactions pour l'admin (id 1)
INSERT INTO `transaction` (`id`, `montant`, `type`, `date_transaction`, `description`, `utilisateur_id`, `categorie_id`, `date_saisie`) VALUES
(1, 850000.00, 'ENTREE', '2026-04-05', 'Salaire mensuel', 1, 7, '2026-04-05 08:30:00'),
(2, 25000.00, 'SORTIE', '2026-04-02', 'Courses supermarché', 1, 1, '2026-04-02 18:20:00'),
(3, 35000.00, 'SORTIE', '2026-04-03', 'Restaurant', 1, 1, '2026-04-03 20:15:00'),
(4, 12000.00, 'SORTIE', '2026-04-04', 'Essence voiture', 1, 2, '2026-04-04 09:45:00'),
(5, 150000.00, 'SORTIE', '2026-04-01', 'Loyer', 1, 3, '2026-04-01 10:00:00'),
(6, 30000.00, 'SORTIE', '2026-04-10', 'Consultation médecin', 1, 4, '2026-04-10 14:30:00'),
(7, 45000.00, 'SORTIE', '2026-04-12', 'Cinéma et sortie', 1, 5, '2026-04-12 21:00:00'),
(8, 50000.00, 'SORTIE', '2026-04-15', 'Épargne mensuelle', 1, 8, '2026-04-15 09:00:00'),
(9, 150000.00, 'ENTREE', '2026-04-20', 'Freelance', 1, 9, '2026-04-20 16:00:00'),
(10, 7500.00, 'SORTIE', '2026-04-18', 'Achat Bitcoin', 1, 10, '2026-04-18 11:30:00'),
(11, 32000.00, 'SORTIE', '2026-04-22', 'Courses', 1, 1, '2026-04-22 19:00:00'),
(12, 8000.00, 'SORTIE', '2026-04-25', 'Uber', 1, 2, '2026-04-25 08:00:00');

-- Transactions pour l'utilisateur simple (id 2)
INSERT INTO `transaction` (`id`, `montant`, `type`, `date_transaction`, `description`, `utilisateur_id`, `categorie_id`, `date_saisie`) VALUES
(13, 450000.00, 'ENTREE', '2026-04-05', 'Salaire', 2, 7, '2026-04-05 09:00:00'),
(14, 65000.00, 'SORTIE', '2026-04-01', 'Courses', 2, 1, '2026-04-01 17:30:00'),
(15, 15000.00, 'SORTIE', '2026-04-03', 'Transport en commun', 2, 2, '2026-04-03 07:45:00'),
(16, 200000.00, 'SORTIE', '2026-04-01', 'Loyer', 2, 3, '2026-04-01 10:30:00'),
(17, 25000.00, 'SORTIE', '2026-04-08', 'Pharmacie', 2, 4, '2026-04-08 15:00:00'),
(18, 35000.00, 'SORTIE', '2026-04-14', 'Restaurant et bar', 2, 5, '2026-04-14 22:00:00'),
(19, 30000.00, 'SORTIE', '2026-04-15', 'Vêtements', 2, 11, '2026-04-15 14:20:00'),
(20, 75000.00, 'SORTIE', '2026-04-20', 'Électroménager', 2, 9, '2026-04-20 12:00:00'),
(21, 28000.00, 'SORTIE', '2026-04-22', 'Courses', 2, 1, '2026-04-22 18:45:00'),
(22, 10000.00, 'SORTIE', '2026-04-24', 'Essence', 2, 2, '2026-04-24 08:15:00');

-- --------------------------------------------------------

--
-- Table `alerte`
--

CREATE TABLE `alerte` (
  `id` int(11) NOT NULL,
  `message` varchar(255) NOT NULL,
  `date_alerte` datetime NOT NULL DEFAULT current_timestamp(),
  `type_alerte` enum('SEUIL_80','SEUIL_100') NOT NULL,
  `est_lue` tinyint(1) NOT NULL DEFAULT 0,
  `budget_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `alerte` (`id`, `message`, `date_alerte`, `type_alerte`, `est_lue`, `budget_id`) VALUES
(1, 'Budget Alimentation atteint 80%', '2026-04-20 20:00:00', 'SEUIL_80', 0, 1),
(2, 'Budget Transport dépassé 100%', '2026-04-25 08:00:00', 'SEUIL_100', 0, 2),
(3, 'Budget Alimentation (User) atteint 85%', '2026-04-22 19:00:00', 'SEUIL_80', 0, 6),
(4, 'Budget Logement (User) à 57%', '2026-04-15 10:00:00', 'SEUIL_80', 1, 8);

-- --------------------------------------------------------

--
-- Table `journal_activite`
--

CREATE TABLE `journal_activite` (
  `id` int(11) NOT NULL,
  `utilisateur_id` int(11) DEFAULT NULL,
  `action` varchar(100) NOT NULL,
  `details` text DEFAULT NULL,
  `date_action` datetime NOT NULL DEFAULT current_timestamp(),
  `adresse_ip` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `journal_activite` (`id`, `utilisateur_id`, `action`, `details`, `date_action`, `adresse_ip`) VALUES
(1, 1, 'INSCRIPTION', 'Création compte admin', '2026-01-15 10:00:00', '192.168.1.1'),
(2, 1, 'CONNEXION', 'Connexion réussie', '2026-04-05 08:25:00', '192.168.1.1'),
(3, 1, 'AJOUT_TRANSACTION', 'Salaire mensuel: 850000 FCFA', '2026-04-05 08:30:00', '192.168.1.1'),
(4, 1, 'AJOUT_BUDGET', 'Budget Alimentation: 300000 FCFA', '2026-04-01 09:00:00', '192.168.1.1'),
(5, 1, 'MODIFICATION_PROFIL', 'Mise à jour du profil', '2026-04-10 11:00:00', '192.168.1.1'),
(6, 1, 'DECONNEXION', 'Déconnexion', '2026-04-25 20:00:00', '192.168.1.1'),
(7, 2, 'INSCRIPTION', 'Création compte user', '2026-02-20 14:30:00', '192.168.1.2'),
(8, 2, 'CONNEXION', 'Connexion réussie', '2026-04-01 09:00:00', '192.168.1.2'),
(9, 2, 'AJOUT_TRANSACTION', 'Salaire: 450000 FCFA', '2026-04-05 09:00:00', '192.168.1.2'),
(10, 2, 'AJOUT_BUDGET', 'Budget Alimentation: 200000 FCFA', '2026-04-01 10:00:00', '192.168.1.2'),
(11, 2, 'DECONNEXION', 'Déconnexion', '2026-04-24 20:00:00', '192.168.1.2');

-- --------------------------------------------------------

--
-- Table `reponse_securite`
--

CREATE TABLE `reponse_securite` (
  `id` int(11) NOT NULL,
  `utilisateur_id` int(11) NOT NULL,
  `question_id` int(11) NOT NULL,
  `reponse` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `reponse_securite` (`id`, `utilisateur_id`, `question_id`, `reponse`) VALUES
(1, 1, 1, '$2a$12$examplehashpourmaman'),
(2, 1, 3, '$2a$12$examplehashpourville'),
(3, 1, 7, '$2a$12$examplehashpourtelephone'),
(4, 2, 1, '$2a$12$examplehashpourmaman'),
(5, 2, 2, '$2a$12$examplehashpouranimal'),
(6, 2, 10, '$2a$12$examplehashpourgrandmere');

-- --------------------------------------------------------

--
-- Table `question_securite`
--

CREATE TABLE `question_securite` (
  `id` int(11) NOT NULL,
  `question` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
-- Table `export`
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

INSERT INTO `export` (`id`, `format`, `date_export`, `chemin_fichier`, `periode_debut`, `periode_fin`, `utilisateur_id`) VALUES
(1, 'PDF', '2026-04-20 10:00:00', '/exports/admin_avril_2026.pdf', '2026-04-01', '2026-04-20', 1),
(2, 'EXCEL', '2026-04-22 14:00:00', '/exports/user_avril_2026.xlsx', '2026-04-01', '2026-04-22', 2);

-- --------------------------------------------------------

--
-- Index et contraintes
--

ALTER TABLE `alerte`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_alerte_budget` (`budget_id`);

ALTER TABLE `budget`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_budget_user_cat_periode` (`utilisateur_id`,`categorie_id`,`mois`,`annee`),
  ADD KEY `fk_budget_categorie` (`categorie_id`),
  ADD KEY `fk_budget_utilisateur` (`utilisateur_id`);

ALTER TABLE `categorie`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_categorie_utilisateur` (`utilisateur_id`);

ALTER TABLE `export`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_export_utilisateur` (`utilisateur_id`);

ALTER TABLE `journal_activite`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_journal_utilisateur` (`utilisateur_id`);

ALTER TABLE `question_securite`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `reponse_securite`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_user_question` (`utilisateur_id`,`question_id`),
  ADD KEY `fk_rep_question` (`question_id`);

ALTER TABLE `transaction`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_transaction_categorie` (`categorie_id`),
  ADD KEY `fk_transaction_utilisateur` (`utilisateur_id`);

ALTER TABLE `utilisateur`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_utilisateur_email` (`email`);

ALTER TABLE `alerte`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
ALTER TABLE `budget`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
ALTER TABLE `categorie`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
ALTER TABLE `export`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
ALTER TABLE `journal_activite`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
ALTER TABLE `question_securite`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
ALTER TABLE `reponse_securite`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
ALTER TABLE `transaction`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;
ALTER TABLE `utilisateur`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

ALTER TABLE `alerte`
  ADD CONSTRAINT `fk_alerte_budget` FOREIGN KEY (`budget_id`) REFERENCES `budget` (`id`) ON DELETE CASCADE;

ALTER TABLE `budget`
  ADD CONSTRAINT `fk_budget_categorie` FOREIGN KEY (`categorie_id`) REFERENCES `categorie` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_budget_utilisateur` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE;

ALTER TABLE `categorie`
  ADD CONSTRAINT `fk_categorie_utilisateur` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE;

ALTER TABLE `export`
  ADD CONSTRAINT `fk_export_utilisateur` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE;

ALTER TABLE `journal_activite`
  ADD CONSTRAINT `fk_journal_utilisateur` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`) ON DELETE SET NULL;

ALTER TABLE `reponse_securite`
  ADD CONSTRAINT `fk_rep_question` FOREIGN KEY (`question_id`) REFERENCES `question_securite` (`id`),
  ADD CONSTRAINT `fk_rep_utilisateur` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE;

ALTER TABLE `transaction`
  ADD CONSTRAINT `fk_transaction_categorie` FOREIGN KEY (`categorie_id`) REFERENCES `categorie` (`id`),
  ADD CONSTRAINT `fk_transaction_utilisateur` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE;

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;