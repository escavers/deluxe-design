# Configuración del backend Firebase

**El build usa Firebase por defecto.** `app/build.gradle` activa el proveedor de nube siempre que no se pase el flag `-Plocal`. Todo el código de nube está escrito y operativo: autenticación por correo/contraseña y Google, Firestore (vehículos, estilos, sucursales, favoritos, proyectos, cotizaciones, notificaciones), Storage (avatar de perfil) y cambio/recuperación de contraseña por enlace de correo.

`app/google-services.json` **ya está en el repositorio**, así que no hace falta descargarlo. Solo debes añadir tu SHA-1 (paso 4) para que funcione el acceso con Google.

## 1. Crear el proyecto y registrar la app

Si partes de cero (el repositorio ya incluye el archivo de un proyecto existente):

1. Entra a [Firebase Console](https://console.firebase.google.com/) → "Crear proyecto".
2. Ve a **Project settings** (icono de engranaje) → sección *Your apps* → icono Android.
3. Package name: `com.deluxedesign.app`.
4. Registra los certificados:
   - Depuración: obtén el SHA-1 de tu `debug.keystore` con
     `keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore -alias androiddebugkey -storepass android` (el SHA-1 es **obligatorio** para Google Sign-In).
   - Publicación (cuando la exportes): el SHA-1 del keystore de firma.
5. Descarga `google-services.json` y colócalo en `app/google-services.json`. La referencia `firebase/google-services.json.example` muestra la estructura.

En Google Cloud Console, restringe la clave API por **paquete Android + SHA-1**: así el archivo puede compartirse sin que otras apps consuman tu cuota.

## 2. Compilar

Con `google-services.json` presente, el build normal ya usa la nube:

```powershell
$env:JAVA_HOME="C:\dev\jdk17"
.\gradlew.bat assembleDebug
```

Para compilar la demo sin backend (Room en el dispositivo, cuenta `demo@deluxedesign.app` / `Demo1234`):

```powershell
.\gradlew.bat assembleDebug -Plocal
```

> No existe el flag `-Pfirebase`: la nube es el comportamiento por defecto y `-Plocal` es lo que la desactiva.

El botón **"Continuar con Google"** aparece en el inicio de sesión únicamente en el build de nube Y cuando `google-services.json` incluye el cliente web (`oauth_client`). Esa entrada se genera automáticamente al registrar el SHA‑1 del keystore en la consola; si el archivo descargado sale con `oauth_client: []`, espera 2–5 minutos tras registrar la huella, descarga de nuevo `google-services.json` desde **Project settings → Your apps → <tu app> → Download latest config** y reemplaza `app/google-services.json` (no hace falta recompilar desde cero; basta relanzar el build). Mientras tanto el botón queda oculto y el acceso con correo/contraseña funciona sin cambios.

## 3. Activar los servicios

En la consola de Firebase:

- **Authentication** → *Sign-in method*:
  - Activa **Correo electrónico/contraseña**.
  - Activa **Google** (obligatorio el SHA-1 del paso 1).
  - *Template de correo* de recuperación de contraseña: **Acción de recuperación de contraseña (restablecer)** debe estar habilitada.
- **Firestore Database** → *Create database* (modo de producción). Zona sugerida: `us-central1` (evita cambios de latencia en la app).
- **Storage** → iniciar en el mismo modo.

## 4. Importar el catálogo inicial (seed)

El seed replica `SeedData.java` en Firestore y mantiene los `id` que la app espera (`porsche`, `bmw`, `mustang`, presets y sucursales). La consola Firestore no permite fijar IDs pegando JSON, así que usa el script incluido:

1. En Firebase Console → **Project settings → Service accounts → Generate new private key** para bajar una clave de servicio (es un secreto; no la subas al repo ni la metas en la app).
2. Desde la raíz del repositorio:

```powershell
npm install firebase-admin
$env:GOOGLE_APPLICATION_CREDENTIALS="C:\ruta\a\la-clave-de-servicio.json"
node firebase/seed/import.js <PROJECT_ID>
```

El script lee `firebase/seed/vehicles.json`, `presets.json` y `branches.json` (objetos cuya llave es el ID del documento) y crea las colecciones `vehicles`, `presets` y `branches` con esos IDs.

## 5. Reglas de seguridad

Publica las reglas incluidas:

- **Firestore**: `firebase/firestore.rules`.
- **Storage**: `firebase/storage.rules`.

Resumen de `firestore.rules`:

- `users/{uid}`: lectura/escritura solo del propio usuario (Firebase Auth administra la contraseña).
- `vehicles`, `presets`, `branches`: lectura para usuarios autenticados, escritura solo desde la consola/administración.
- `projects`: el cliente solo crea con estado `Borrador` y avance 0; edita sin cambiar estado ni avance (eso es del taller).
- `quotes`: el cliente crea cotizaciones `Pendiente` sobre su proyecto; solo el taller/aprobación las modifica.
- `favorites`: CRUD del propietario.
- `notifications`: lectura del propietario; solo se permite marcar `read`.

## 6. Verificación end‑to‑end (opcional)

Con el build de nube (el predeterminado) instalado en un dispositivo:

1. Regístrate (o entra por Google); la app crea automáticamente `users/{uid}` y persiste favoritos en `favorites`.
2. Guarda un proyecto → aparece como `Borrador`/avance 0 en `projects`. Crea una cotización → estado `Pendiente`.
3. Prueba el enlace de recuperación de contraseña por correo.

## Notas

- No se escriben catálogos automáticamente desde el cliente (ver `firebase/DATA.md`). El seed se importa una sola vez desde la consola.
- Los avatares se suben a `profiles/{uid}/avatar` (máx. 5 MB, imágenes).
- Las exportaciones/impresiones del presupuesto se guardan localmente.