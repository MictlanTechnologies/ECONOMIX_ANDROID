-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema economix
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema economix
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `economix` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `economix` ;

-- -----------------------------------------------------
-- Table `economix`.`tbl_usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_usuario` (
  `idUsuario` INT NOT NULL AUTO_INCREMENT,
  `perfilUsuario` VARCHAR(50) NOT NULL,
  `correo` VARCHAR(120) NOT NULL,
  `contraseñaUsuario` VARCHAR(100) NOT NULL,
  `fechaRegistro` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado` ENUM('ACTIVO', 'BLOQUEADO', 'ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (`idUsuario`),
  UNIQUE INDEX `u_usuario_correo` (`correo` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `economix`.`tbl_ahorro`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_ahorro` (
  `idAhorro` INT NOT NULL AUTO_INCREMENT,
  `idUsuario` INT NOT NULL,
  `nombreObjetivo` VARCHAR(100) NOT NULL,
  `descripcionObjetivo` TEXT NULL DEFAULT NULL,
  `meta` DECIMAL(10,2) NOT NULL,
  `montoAhorrado` DECIMAL(10,2) NOT NULL DEFAULT '0.00',
  `fechaLimite` DATE NULL DEFAULT NULL,
  PRIMARY KEY (`idAhorro`),
  INDEX `idx_ahorro_usuario` (`idUsuario` ASC) VISIBLE,
  CONSTRAINT `fk_ahorro_usuario`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`tbl_usuario` (`idUsuario`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `economix`.`tbl_categoria_gasto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_categoria_gasto` (
  `idCategoria` INT NOT NULL AUTO_INCREMENT,
  `idUsuario` INT NOT NULL,
  `nombreCategoria` VARCHAR(80) NOT NULL,
  `descripcion` TEXT NULL DEFAULT NULL,
  `color` CHAR(7) NULL DEFAULT NULL,
  PRIMARY KEY (`idCategoria`),
  UNIQUE INDEX `u_categoria_usuario_nombre` (`idUsuario` ASC, `nombreCategoria` ASC) VISIBLE,
  INDEX `idx_categoria_usuario` (`idUsuario` ASC) VISIBLE,
  CONSTRAINT `fk_categoria_usuario`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`tbl_usuario` (`idUsuario`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `economix`.`tbl_presupuesto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_presupuesto` (
  `idPresupuesto` INT NOT NULL AUTO_INCREMENT,
  `idUsuario` INT NOT NULL,
  `idCategoria` INT NULL DEFAULT NULL,
  `categoria` VARCHAR(40) NULL DEFAULT NULL,
  `montoMaximo` DECIMAL(12,2) NOT NULL,
  `montoGastado` DECIMAL(12,2) NOT NULL DEFAULT '0.00',
  `mes` TINYINT UNSIGNED NOT NULL,
  `anio` SMALLINT UNSIGNED NOT NULL,
  PRIMARY KEY (`idPresupuesto`),
  UNIQUE INDEX `u_presupuesto_usr_mes_anio_cat` (`idUsuario` ASC, `mes` ASC, `anio` ASC, `categoria` ASC) VISIBLE,
  INDEX `fk_presupuesto_categoria` (`idCategoria` ASC) VISIBLE,
  INDEX `idx_presupuesto_usuario` (`idUsuario` ASC) VISIBLE,
  CONSTRAINT `fk_presupuesto_categoria`
    FOREIGN KEY (`idCategoria`)
    REFERENCES `economix`.`tbl_categoria_gasto` (`idCategoria`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_presupuesto_usuario`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`tbl_usuario` (`idUsuario`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `economix`.`tbl_gastos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_gastos` (
  `idGastos` INT NOT NULL AUTO_INCREMENT,
  `idUsuario` INT NOT NULL,
  `idCategoria` INT NULL DEFAULT NULL,
  `idPresupuesto` INT NULL DEFAULT NULL,
  `descripcionGasto` TEXT NOT NULL,
  `articuloGasto` VARCHAR(100) NOT NULL,
  `montoGasto` DECIMAL(10,2) NOT NULL,
  `fechaGastos` DATE NOT NULL,
  `periodoGastos` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`idGastos`),
  INDEX `fk_gastos_presupuesto` (`idPresupuesto` ASC) VISIBLE,
  INDEX `idx_gastos_usuario` (`idUsuario` ASC) VISIBLE,
  INDEX `idx_gastos_categoria` (`idCategoria` ASC) VISIBLE,
  CONSTRAINT `fk_gastos_categoria`
    FOREIGN KEY (`idCategoria`)
    REFERENCES `economix`.`tbl_categoria_gasto` (`idCategoria`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_gastos_presupuesto`
    FOREIGN KEY (`idPresupuesto`)
    REFERENCES `economix`.`tbl_presupuesto` (`idPresupuesto`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_gastos_usuario`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`tbl_usuario` (`idUsuario`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `economix`.`tbl_conceptogastos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_conceptogastos` (
  `idConcepto` INT NOT NULL AUTO_INCREMENT,
  `nombreConcepto` VARCHAR(100) NULL DEFAULT NULL,
  `descripcionConcepto` TEXT NULL DEFAULT NULL,
  `precioConcepto` DECIMAL(10,2) NULL DEFAULT NULL,
  `idGastos` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idConcepto`),
  INDEX `idx_concepto_gasto` (`idGastos` ASC) VISIBLE,
  CONSTRAINT `fk_concepto_gasto`
    FOREIGN KEY (`idGastos`)
    REFERENCES `economix`.`tbl_gastos` (`idGastos`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `economix`.`tbl_fuente_ingreso`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_fuente_ingreso` (
  `idFuente` INT NOT NULL AUTO_INCREMENT,
  `idUsuario` INT NOT NULL,
  `nombreFuente` VARCHAR(80) NOT NULL,
  `descripcion` TEXT NULL DEFAULT NULL,
  `activo` TINYINT(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`idFuente`),
  INDEX `idx_fuente_usuario` (`idUsuario` ASC) VISIBLE,
  CONSTRAINT `fk_fuente_usuario`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`tbl_usuario` (`idUsuario`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `economix`.`tbl_ingresos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_ingresos` (
  `idIngresos` INT NOT NULL AUTO_INCREMENT,
  `idUsuario` INT NOT NULL,
  `idFuente` INT NULL DEFAULT NULL,
  `montoIngreso` DECIMAL(10,2) NOT NULL,
  `periodicidadIngreso` VARCHAR(50) NULL DEFAULT NULL,
  `fechaIngresos` DATE NOT NULL,
  `descripcionIngreso` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`idIngresos`),
  INDEX `fk_ingresos_fuente` (`idFuente` ASC) VISIBLE,
  INDEX `idx_ingresos_usuario` (`idUsuario` ASC) VISIBLE,
  CONSTRAINT `fk_ingresos_fuente`
    FOREIGN KEY (`idFuente`)
    REFERENCES `economix`.`tbl_fuente_ingreso` (`idFuente`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_ingresos_usuario`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`tbl_usuario` (`idUsuario`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


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
  INDEX `idx_concepto_ingreso` (`idIngresos` ASC) VISIBLE,
  CONSTRAINT `fk_concepto_ingreso`
    FOREIGN KEY (`idIngresos`)
    REFERENCES `economix`.`tbl_ingresos` (`idIngresos`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `economix`.`tbl_persona`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_persona` (
  `idPersona` INT NOT NULL AUTO_INCREMENT,
  `nombrePersona` VARCHAR(50) NULL DEFAULT NULL,
  `apellidoP` VARCHAR(50) NULL DEFAULT NULL,
  `apellidoM` VARCHAR(50) NULL DEFAULT NULL,
  `fechaNacimiento` DATE NULL DEFAULT NULL,
  `idUsuario` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idPersona`),
  INDEX `idx_persona_usuario` (`idUsuario` ASC) VISIBLE,
  CONSTRAINT `fk_persona_usuario`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`tbl_usuario` (`idUsuario`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `economix`.`tbl_contactos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_contactos` (
  `idContactos` INT NOT NULL AUTO_INCREMENT,
  `numCelular` VARCHAR(20) NULL DEFAULT NULL,
  `correoAlterno` VARCHAR(100) NULL DEFAULT NULL,
  `idPersona` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idContactos`),
  INDEX `idx_contacto_persona` (`idPersona` ASC) VISIBLE,
  CONSTRAINT `fk_contactos_persona`
    FOREIGN KEY (`idPersona`)
    REFERENCES `economix`.`tbl_persona` (`idPersona`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `economix`.`tbl_domicilio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_domicilio` (
  `idDomicilio` INT NOT NULL AUTO_INCREMENT,
  `ciudad` VARCHAR(50) NULL DEFAULT NULL,
  `calle` VARCHAR(100) NULL DEFAULT NULL,
  `colonia` VARCHAR(100) NULL DEFAULT NULL,
  `numero` VARCHAR(10) NULL DEFAULT NULL,
  `codigoPostal` VARCHAR(10) NULL DEFAULT NULL,
  `idPersona` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idDomicilio`),
  INDEX `idx_domicilio_persona` (`idPersona` ASC) VISIBLE,
  CONSTRAINT `fk_domicilio_persona`
    FOREIGN KEY (`idPersona`)
    REFERENCES `economix`.`tbl_persona` (`idPersona`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `economix`.`tbl_movimiento_ahorro`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_movimiento_ahorro` (
  `idMovimiento` INT NOT NULL AUTO_INCREMENT,
  `idAhorro` INT NOT NULL,
  `idUsuario` INT NOT NULL,
  `tipoMovimiento` ENUM('APORTE', 'RETIRO') NOT NULL,
  `monto` DECIMAL(10,2) NOT NULL,
  `fechaMovimiento` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `nota` VARCHAR(150) NULL DEFAULT NULL,
  PRIMARY KEY (`idMovimiento`),
  INDEX `fk_mov_ahorro_objetivo` (`idAhorro` ASC) VISIBLE,
  INDEX `idx_mov_ahorro_usuario` (`idUsuario` ASC) VISIBLE,
  CONSTRAINT `fk_mov_ahorro_objetivo`
    FOREIGN KEY (`idAhorro`)
    REFERENCES `economix`.`tbl_ahorro` (`idAhorro`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_mov_ahorro_usuario`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`tbl_usuario` (`idUsuario`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `economix`.`tbl_roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_roles` (
  `idRol` TINYINT NOT NULL AUTO_INCREMENT,
  `nombreRol` VARCHAR(40) NOT NULL,
  `descripcion` VARCHAR(150) NULL DEFAULT NULL,
  PRIMARY KEY (`idRol`),
  UNIQUE INDEX `u_roles_nombre` (`nombreRol` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `economix`.`tbl_sesion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_sesion` (
  `idSesion` BIGINT NOT NULL AUTO_INCREMENT,
  `idUsuario` INT NOT NULL,
  `token` CHAR(64) NOT NULL,
  `ipOrigen` VARCHAR(45) NULL DEFAULT NULL,
  `dispositivo` VARCHAR(120) NULL DEFAULT NULL,
  `fechaInicio` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fechaUltimoAcceso` DATETIME NULL DEFAULT NULL,
  `vigente` TINYINT(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`idSesion`),
  UNIQUE INDEX `u_sesion_token` (`token` ASC) VISIBLE,
  INDEX `fk_sesion_usuario` (`idUsuario` ASC) VISIBLE,
  CONSTRAINT `fk_sesion_usuario`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`tbl_usuario` (`idUsuario`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `economix`.`tbl_usuario_roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`tbl_usuario_roles` (
  `idUsuarioRol` INT NOT NULL AUTO_INCREMENT,
  `idUsuario` INT NOT NULL,
  `idRol` TINYINT NOT NULL,
  PRIMARY KEY (`idUsuarioRol`),
  UNIQUE INDEX `u_usuario_rol` (`idUsuario` ASC, `idRol` ASC) VISIBLE,
  INDEX `fk_usuario_roles_rol` (`idRol` ASC) VISIBLE,
  CONSTRAINT `fk_usuario_roles_rol`
    FOREIGN KEY (`idRol`)
    REFERENCES `economix`.`tbl_roles` (`idRol`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_usuario_roles_usuario`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`tbl_usuario` (`idUsuario`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
