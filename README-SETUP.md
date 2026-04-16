# ECONOMIX — Guía de Configuración Rápida

Este documento complementa el `README.md` principal con instrucciones específicas sobre
las variables de entorno y configuración necesaria para levantar el backend correctamente.

---

## Variables de entorno requeridas

El backend requiere que las siguientes variables estén definidas **en el sistema** antes
de iniciar Spring Boot. Los nombres son exactos y **no pueden contener espacios**.

### 1. `ECONOMIX_JWT_SECRET` (obligatoria)

Secret para firmar y validar tokens JWT. Mínimo 32 caracteres alfanuméricos.

**Windows (persistente, PowerShell como administrador):**
```powershell
setx ECONOMIX_JWT_SECRET "pon-aqui-un-secreto-largo-de-32-o-mas-caracteres"
```

**Linux / macOS (sesión actual):**
```bash
export ECONOMIX_JWT_SECRET=pon-aqui-un-secreto-largo-de-32-o-mas-caracteres
```

**Linux / macOS (permanente — agrega en `~/.bashrc` o `~/.zshrc`):**
```bash
echo 'export ECONOMIX_JWT_SECRET=pon-aqui-un-secreto-largo-de-32-o-mas-caracteres' >> ~/.bashrc
source ~/.bashrc
```

**IntelliJ IDEA:**
Run/Debug Configurations → tu configuración Spring Boot → pestaña **Environment variables**:
```
ECONOMIX_JWT_SECRET=pon-aqui-un-secreto-largo-de-32-o-mas-caracteres
```

---

### 2. `GEMINI_API_KEY` (requerida para funciones de IA con Gemini)

API Key de Google Gemini para las funciones de IA del backend.

> **IMPORTANTE:** El nombre de la variable es `GEMINI_API_KEY` (sin espacios).
> Un nombre como `"GEMINI API KEY"` con espacios es **inválido** en todos los sistemas
> operativos y no puede ser leído por Spring Boot ni por ninguna aplicación.

**Windows (persistente, PowerShell como administrador):**
```powershell
setx GEMINI_API_KEY "tu-api-key-de-gemini-aqui"
```

**Linux / macOS (sesión actual):**
```bash
export GEMINI_API_KEY=tu-api-key-de-gemini-aqui
```

**Linux / macOS (permanente — agrega en `~/.bashrc` o `~/.zshrc`):**
```bash
echo 'export GEMINI_API_KEY=tu-api-key-de-gemini-aqui' >> ~/.bashrc
source ~/.bashrc
```

**IntelliJ IDEA:**
Run/Debug Configurations → tu configuración Spring Boot → pestaña **Environment variables**:
```
GEMINI_API_KEY=tu-api-key-de-gemini-aqui
```

> Después de usar `setx` en Windows, **cierra y vuelve a abrir** la terminal o el IDE
> para que el cambio surta efecto en la sesión actual.

---

## Rutas de la API

Todos los endpoints del backend usan el prefijo `/economix/api` en **minúsculas**.
Spring Security es case-sensitive; asegúrate de que el cliente Android use exactamente:

```
http://<host>:8080/economix/api/...
```

| Grupo            | Prefijo                             |
|------------------|-------------------------------------|
| Test             | `/economix/api/test`                |
| Usuarios         | `/economix/api/usuarios`            |
| Roles            | `/economix/api/roles`               |
| Sesiones         | `/economix/api/sesiones`            |
| Personas         | `/economix/api/personas`            |
| Contactos        | `/economix/api/contactos`           |
| Domicilios       | `/economix/api/domicilios`          |
| Gastos           | `/economix/api/gastos`              |
| Ingresos         | `/economix/api/ingresos`            |
| Categorías gasto | `/economix/api/categorias-gasto`    |
| Conceptos gasto  | `/economix/api/conceptos-gasto`     |
| Conceptos ingreso| `/economix/api/conceptos-ingreso`   |
| Fuentes ingreso  | `/economix/api/fuentes-ingreso`     |
| Presupuestos     | `/economix/api/presupuestos`        |
| Ahorros          | `/economix/api/ahorros`             |
| Mov. ahorro      | `/economix/api/movimientos-ahorro`  |
| Estados          | `/economix/api/estados`             |
| IA / Analítica   | `/economix/api/ai`                  |
| Usuario-Roles    | `/economix/api/usuario-roles`       |

---

## Verificación rápida del backend

Una vez levantado el servidor (`mvn spring-boot:run` desde `economix-backend/`), verifica
que responde con:

```bash
curl http://localhost:8080/economix/api/test/test
# Respuesta esperada: OK
```

---

## Referencia rápida de configuración en `application.properties`

El archivo `economix-backend/src/main/resources/application.properties` lee estas
variables de entorno con valores por defecto vacíos (la app arrancará pero fallará
al intentar usar JWT o Gemini si no están definidas):

```properties
economix.jwt.secret=${ECONOMIX_JWT_SECRET:}
economix.ai.gemini.api-key=${GEMINI_API_KEY:}
```

Para desarrollo local puedes definirlas directamente en ese archivo (nunca en producción):

```properties
# Solo para desarrollo local — NO commitear con valores reales
economix.jwt.secret=mi-secreto-de-desarrollo-local-32chars
economix.ai.gemini.api-key=mi-api-key-de-gemini
```

---

Para más detalles sobre cómo levantar la base de datos y el cliente Android, consulta el
`README.md` en la raíz del repositorio.
