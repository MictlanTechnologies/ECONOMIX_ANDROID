# ECONOMIX Android + Backend: guía de inicialización paso a paso

Este README explica **cómo levantar el servidor (backend)** y **cómo configurar el frontend Android** después de la refactorización de IA.

---

## 1) Qué cambió (resumen rápido)

Ahora la app Android:
- **Ya no llama a Gemini directamente**.
- **Toda la IA se consume desde el backend autenticado (JWT)**.
- Usa `BASE_URL` inyectada por Gradle (`BuildConfig.BASE_URL`), no IP hardcodeada en código.

Por eso, para que funcione chat/predicciones, primero debe estar arriba el backend.

---

## 2) Requisitos previos

### Backend
- Java **17**
- Maven (o usar wrapper si lo agregan después)
- MySQL 8+

### Android
- Android Studio (última estable recomendada)
- SDK de Android instalado
- Emulador o dispositivo físico

---

## 3) Levantar base de datos MySQL

1. Crear base:

```sql
CREATE DATABASE economix;
```

2. (Opcional pero recomendado) importar script inicial:

Archivo:
- `app/src/main/resources/sql/ECONOMIX.sql`

Ejemplo:

```bash
mysql -u root -p economix < app/src/main/resources/sql/ECONOMIX.sql
```

3. Verificar credenciales del backend en:
- `economix-backend/src/main/resources/application.properties`

Por defecto:
- URL: `jdbc:mysql://localhost:3306/economix?useSSL=false&serverTimezone=UTC`
- user: `root`
- password: `changeme`
- puerto backend: `8080`

Si tu MySQL usa otro usuario/clave, cambia ese archivo antes de arrancar.

---

## 4) Levantar backend (Spring Boot)

Desde la raíz del repo:

```bash
cd economix-backend
mvn spring-boot:run
```

Si todo va bien, el backend queda en:
- `http://localhost:8080/`

> Mantén esta terminal abierta mientras uses la app.

---


## 4.1) Configurar secreto JWT (obligatorio en tu backend)

Si al arrancar ves este error:

`Missing required property economix.jwt.secret (env: ECONOMIX_JWT_SECRET)`

debes definir la variable de entorno **ECONOMIX_JWT_SECRET** antes de iniciar Spring Boot.

### Windows (PowerShell, solo sesión actual)

```powershell
$env:ECONOMIX_JWT_SECRET = "pon-aqui-un-secreto-largo-de-32+caracteres"
```

### Windows (persistente en tu usuario)

```powershell
setx ECONOMIX_JWT_SECRET "pon-aqui-un-secreto-largo-de-32+caracteres"
```

Luego cierra y vuelve a abrir la terminal/IDE.

### IntelliJ IDEA (recomendado)

Run/Debug Configurations → tu configuración de Spring Boot → **Environment variables**:

```
ECONOMIX_JWT_SECRET=pon-aqui-un-secreto-largo-de-32+caracteres
```

Después reinicia la app.

> Nota: los warnings de `MySQL8Dialect` en logs no son el fallo que tumba el servidor; el bloqueo real es la ausencia del secret JWT.

---

## 5) Configurar BASE_URL del Android

En `gradle.properties` ya existen estas propiedades:

```properties
ECONOMIX_BASE_URL_DEBUG=http://10.0.2.2:8080/
ECONOMIX_BASE_URL_RELEASE=https://api.economix.example/
```

### Qué valor usar
- **Emulador Android Studio**: `http://10.0.2.2:8080/` ✅
- **Dispositivo físico en tu Wi‑Fi**: cambia a `http://<IP_DE_TU_PC>:8080/`

Ejemplo:

```properties
ECONOMIX_BASE_URL_DEBUG=http://192.168.1.73:8080/
```

> Importante: debe terminar en `/`.

---

## 6) Ejecutar app Android

Desde Android Studio:
1. Abrir el proyecto `ECONOMIX_ANDROID`.
2. Sincronizar Gradle.
3. Ejecutar configuración `app` en emulador/dispositivo.

Flujo esperado:
1. Login
2. Sesión JWT
3. Navegación (incluye **Predicciones**)
4. Predicciones IA (backend)
5. Chat IA (backend)

---

## 7) Validación funcional rápida

### Predicciones
- En bottom nav abre **Predicciones**.
- Pulsa **Analizar**.
- Debes ver estados útiles: loading/success/insufficient data/errores.

### Chat
- Desde menú abre chat.
- Envía una pregunta.
- El botón se deshabilita mientras responde.
- Si hay 401, redirige a login.

---

## 8) Problemas comunes

### 1) "No conecta al backend"
- Revisa que backend esté corriendo en `:8080`.
- Revisa `ECONOMIX_BASE_URL_DEBUG`.
- En dispositivo físico, usa IP LAN de tu PC (no `10.0.2.2`).

### 2) Error de MySQL al arrancar backend
- Verifica usuario/clave en `application.properties`.
- Confirma que la DB `economix` exista.

### 3) 401 en chat/predicciones
- Tu sesión JWT expiró o no existe.
- Inicia sesión de nuevo.

---

## 9) Comandos útiles

### Backend
```bash
cd economix-backend
mvn spring-boot:run
```

### Android (CLI)
```bash
./gradlew :app:assembleDebug
```

---

## 10) Nota sobre seguridad

- No agregues API keys de IA en Android.
- La app **no debe** contener llaves de proveedor IA.
- Toda la orquestación IA debe quedarse en backend autenticado.

