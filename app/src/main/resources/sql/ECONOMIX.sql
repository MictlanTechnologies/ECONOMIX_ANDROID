-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
SHOW WARNINGS;
-- -----------------------------------------------------
-- Schema economix
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `economix` ;

-- -----------------------------------------------------
-- Schema economix
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `economix` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
SHOW WARNINGS;
USE `economix` ;

-- -----------------------------------------------------
-- Table `economix`.`tbl_usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_usuario` (
  `idUsuario` INT NOT NULL AUTO_INCREMENT,
  `perfilUsuario` VARCHAR(50) NOT NULL,
  `contraseñaUsuario` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `economix`.`tbl_gastos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_gastos` (
	`idGastos` INT NOT NULL AUTO_INCREMENT,
  `descripciónGasto` TEXT NOT NULL,
  `artículoGasto` VARCHAR(100) NOT NULL,
  `montoGasto` DECIMAL(10,2) NOT NULL,
  `fechaGastos` DATE NOT NULL,
  `periodoGastos` VARCHAR(50) NOT NULL,
  `idUsuario` INT NOT NULL,
  PRIMARY KEY (`idGastos`),
  CONSTRAINT `tbl_gastos_ibfk_1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`tbl_usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;
CREATE INDEX `idUsuario` ON `economix`.`tbl_gastos` (`idUsuario` ASC) VISIBLE;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `economix`.`tbl_conceptogastos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_conceptogastos` (
  `idConcepto` INT NOT NULL AUTO_INCREMENT,
  `nombreConcepto` VARCHAR(100) NULL DEFAULT NULL,
  `descripciónConcepto` TEXT NULL DEFAULT NULL,
  `precioConcepto` DECIMAL(10,2) NULL DEFAULT NULL,
  `idGastos` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idConcepto`),
  CONSTRAINT `tbl_conceptogastos_ibfk_1`
    FOREIGN KEY (`idGastos`)
    REFERENCES `economix`.`tbl_gastos` (`idGastos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;
CREATE INDEX `idGastos` ON `economix`.`tbl_conceptogastos` (`idGastos` ASC) VISIBLE;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `economix`.`tbl_ingresos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_ingresos` (
  `idIngresos` INT NOT NULL AUTO_INCREMENT,
  `montoIngreso` DECIMAL(10,2) NULL DEFAULT NULL,
  `periodicidadIngreso` VARCHAR(50) NULL DEFAULT NULL,
  `fechaIngresos` DATE NULL DEFAULT NULL,
  `descripcionIngreso` TEXT NULL DEFAULT NULL,
  `idUsuario` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idIngresos`),
  CONSTRAINT `tbl_ingresos_ibfk_1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`tbl_usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;
CREATE INDEX `idUsuario` ON `economix`.`tbl_ingresos` (`idUsuario` ASC) VISIBLE;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `economix`.`tbl_conceptoingresos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_conceptoingresos` (
  `idConcepto` INT NOT NULL AUTO_INCREMENT,
  `nombreConcepto` VARCHAR(100) NULL DEFAULT NULL,
  `descripcionConcepto` TEXT NULL DEFAULT NULL,
  `precioConcepto` DECIMAL(10,2) NULL DEFAULT NULL,
  `idIngresos` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idConcepto`),
  CONSTRAINT `tbl_conceptoingresos_ibfk_1`
    FOREIGN KEY (`idIngresos`)
    REFERENCES `economix`.`tbl_ingresos` (`idIngresos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;
CREATE INDEX `idIngresos` ON `economix`.`tbl_conceptoingresos` (`idIngresos` ASC) VISIBLE;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `economix`.`tbl_contactos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_contactos` (
  `idContactos` INT NOT NULL AUTO_INCREMENT,
  `numCelular` VARCHAR(20) NULL DEFAULT NULL,
  `Correo` VARCHAR(100) NULL DEFAULT NULL,
  `idPersona` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idContactos`),
  CONSTRAINT `tbl_contactos_ibfk_1`
    FOREIGN KEY (`idPersona`)
    REFERENCES `economix`.`tbl_persona` (`idPersona`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;
CREATE INDEX `idPersona` ON `economix`.`tbl_contactos` (`idPersona` ASC) VISIBLE;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `economix`.`tbl_domicilio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_domicilio` (
  `idDomicilio` INT NOT NULL AUTO_INCREMENT,
  `ciudad` VARCHAR(50) NULL DEFAULT NULL,
  `calle` VARCHAR(100) NULL DEFAULT NULL,
  `colonia` VARCHAR(100) NULL DEFAULT NULL,
  `número` VARCHAR(10) NULL DEFAULT NULL,
  `idPersona` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idDomicilio`),
  CONSTRAINT `tbl_domicilio_ibfk_1`
    FOREIGN KEY (`idPersona`)
    REFERENCES `economix`.`tbl_persona` (`idPersona`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;
CREATE INDEX `idPersona` ON `economix`.`tbl_domicilio` (`idPersona` ASC) VISIBLE;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `economix`.`tbl_persona`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_persona` (
  `idPersona` INT NOT NULL AUTO_INCREMENT,
  `nombrePersona` VARCHAR(50) NULL DEFAULT NULL,
  `apellidoP` VARCHAR(50) NULL DEFAULT NULL,
  `apellidoM` VARCHAR(50) NULL DEFAULT NULL,
  `idUsuario` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idPersona`),
  CONSTRAINT `tbl_nombreusuario_ibfk_1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`tbl_usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;
CREATE INDEX `idUsuario` ON `economix`.`tbl_persona` (`idUsuario` ASC) VISIBLE;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `economix`.`tbl_ahorro`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_ahorro` (
  `idAhorro` INT NOT NULL AUTO_INCREMENT,
  `fechaAhorro` DATE NULL DEFAULT NULL,
  `fechaActualizaciónA` DATE NULL DEFAULT NULL,
  `periodoTAhorro` VARCHAR(50) NULL DEFAULT NULL,
  `montoAhorro` DECIMAL(10,2) NULL DEFAULT NULL,
  `idIngresos` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idAhorro`),
  CONSTRAINT `tbl_ahorro_ibfk_1`
    FOREIGN KEY (`idIngresos`)
    REFERENCES `economix`.`tbl_ingresos` (`idIngresos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;
CREATE INDEX `idIngresos` ON `economix`.`tbl_ahorro` (`idIngresos` ASC) VISIBLE;

SHOW WARNINGS;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;