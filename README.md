# DELUXE DESIGN · Android 12

**Actualización 1.1.0:** se integraron las 13 imágenes proporcionadas. Consulta `VISUAL_UPDATE.md` para ver dónde se utilizan y qué vistas finales de presets siguen pendientes. Esta nota sustituye las referencias al estado visual provisional de la entrega inicial que aparecen más abajo.

Aplicación nativa de demostración académica para personalización automotriz. Java 11, Android Views XML, minSdk/targetSdk/compileSdk 31. APK de instalación directa.

## Clonar el repositorio

El trabajo actual está en la rama **`main2`**. La rama `main` es una versión anterior y **no debe usarse**.

```bash
git clone https://github.com/escavers/deluxe-design.git
cd deluxe-design
git checkout main2
```

Si `git branch` muestra `main` después del `clone`, todavía no has hecho el `checkout`. Sin ese paso la compilación falla porque falta `app/google-services.json`.

Alternativa en una sola línea:

```bash
git clone --branch main2 https://github.com/escavers/deluxe-design.git
```

## Requisitos

- **JDK 11** (el JDK 25 que trae Android Studio reciente no sirve para Gradle 7.3.3).
- **Android SDK Platform 31** y **Build Tools 30.0.3**.
- `app/google-services.json`, que ya viene en el repositorio.
- Acceso a la consola de Firebase para **registrar tu SHA-1 de depuración** (obligatorio para el acceso con Google; ver "Acceso con Google").

No necesitas archivo de credenciales ni clave privada de firma: la compilación usa la firma de depuración que genera Android Studio.

## Abrir en Android Studio

1. Abre esta carpeta como proyecto, no solamente la carpeta `app`.
2. Instala Android SDK Platform 31 y Build Tools 30.0.3 desde SDK Manager.
3. En Settings > Build, Execution, Deployment > Build Tools > Gradle selecciona un JDK **11**.
4. Deja que Android Studio configure `local.properties` con la ruta de tu SDK. **No está versionado** (cada máquina tiene la suya) y Android Studio lo crea solo al abrir el proyecto.
5. Sincroniza Gradle. Ejecuta en un teléfono o emulador Android 12/API 31.

La primera sincronización descarga dependencias de internet. Después de instalar, la app sí necesita conexión para usar Firebase.

## Compilar

Con JAVA_HOME apuntando a JDK 11:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebugAndroidTest
.\gradlew.bat connectedDebugAndroidTest
```

**El build usa Firebase por defecto.** `app/build.gradle` activa el proveedor de nube salvo que se pase `-Plocal`, que compila la demo sin backend:

```powershell
.\gradlew.bat assembleDebug -Plocal   # solo demo local, sin Firebase
```

Si falta `app/google-services.json`, el build falla a propósito con un mensaje explicativo en lugar de generar un APK que no funcionaría.

El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`. Las pruebas instrumentadas requieren un dispositivo conectado. El APK de prueba no es la aplicación de usuario.

El APK entregado utiliza una firma de depuración de esta compilación. Android Studio generará su propia firma local. Si aparece un conflicto de firmas al ejecutar tu recompilación, utiliza otro emulador o desinstala primero el APK entregado; **desinstalar elimina sus datos locales**. No se incluye ninguna clave privada de firma en el ZIP.

## Cuentas

**Con Firebase (build por defecto):** regístrate con tu correo real, o entra con **Continuar con Google**. Cada cuenta tiene sus datos en la nube. El correo `demo@deluxedesign.app` **no existe** en este proyecto de Firebase; créalo tú mismo si lo necesitas.

**Solo en el build `-Plocal`:** existe la cuenta de demostración
`demo@deluxedesign.app` / `Demo1234`. Puedes registrar una cuenta local nueva. Las contraseñas locales se almacenan con PBKDF2, sal aleatoria y 60 000 iteraciones; nunca en texto plano. La recuperación local entrega un **código de prueba visible en pantalla**, válido durante diez minutos y durante la sesión. No envía correos ni representa un mecanismo productivo. Con Firebase se utiliza el correo oficial de recuperación.

## Acceso con Google (paso obligatorio)

El botón «Continuar con Google» solo aparece si la consola de Firebase conoce tu SHA-1. Sin él, el acceso por correo funciona igual.

`app/google-services.json` llega con un único certificado registrado, que es el de la máquina que lo generó. El keystore de depuración se crea por equipo, así que **el tuyo es distinto y hay que añadirlo**. Si te saltas este paso, la app abre y permite registrarse por correo, pero el botón de Google simplemente no aparecerá.

1. Obtén tu SHA-1 de depuración:

   ```powershell
   keytool -list -v -keystore "$env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android
   ```

2. En [Firebase Console](https://console.firebase.google.com/) → **Project settings** (engranaje) → **Your apps** → tu app Android → **Add fingerprint**, y pega el SHA-1.
3. Espera 2–5 minutos y vuelve a descargar `google-services.json` (**Download latest config**) para reemplazar el del repositorio.

En Google Cloud Console, restringe la clave API por **paquete Android + SHA-1** para que no pueda usarse desde otras apps.

La configuración completa del backend está en `firebase/SETUP.md`.

## Layout Editor

Abre `app/src/main/res/layout/fragment_*.xml` y elige Design o Split. Selecciona API 31, tema Theme.Deluxe y orientación vertical. Las listas incluyen datos `tools:` y las pantallas tienen contenido de referencia para previsualizarlas sin iniciar sesión.

- `activity_main.xml`: contenedor de navegación y barra inferior.
- `fragment_*.xml`: 21 pantallas funcionales.
- `item_card.xml`: tarjeta reutilizada por las listas.
- `values/themes.xml`, `colors.xml`, `dimens.xml`: tema y medidas.
- `values/strings.xml`: textos de los layouts centralizados en español.
- `drawable/bg_*.xml`: fondos, gradientes y bordes.

Los placeholders XML permiten ver el espacio reservado para fotografías. La aplicación muestra, además, una ilustración marcada **RECURSO VISUAL PENDIENTE**. Los textos, botones y campos son vistas editables; no son capturas planas del prototipo.

## Estructura

```text
app/src/main/java/com/deluxedesign/app/
  MainActivity.java                 Navegación y confirmación de salida
  DeluxeApplication.java            Selección del proveedor de datos
  ui/
    AppViewModel.java               Estado compartido y flujo de trabajo
    BaseFragment.java               Observación del estado y acciones comunes
    auth/ home/ catalog/ customizer/ projects/ quotes/ profile/
    common/                        Tarjetas y adaptadores
  domain/model/                    Entidades del dominio
  repository/                      Contratos de acceso a datos
  data/local/                      Room, datos iniciales y autenticación demo
  data/firebase/                   Implementación opcional en la nube
  util/                            Validación, imágenes, precios y exportaciones
```

La actividad contiene el NavHostFragment. Cada pantalla tiene un Fragment Java y un XML con ViewBinding. Los ViewModels conservan la selección mediante SavedStateHandle. Room conserva usuarios, vehículos, estilos, proyectos, cotizaciones, favoritos, sucursales y notificaciones después de cerrar la aplicación.

## Flujos implementados

- Portada, acceso, registro, recuperación, restablecimiento y cambio de contraseña.
- Inicio con proyectos recientes.
- Catálogo con búsqueda por texto y filtros de marca (Porsche, BMW, Ford), año y categoría. La categoría "Deportivos" cubre deportivos, cupés y muscle cars.
- Ficha del vehículo con especificaciones, tres ángulos y favoritos.
- Editor 2D con selección de estilo completo, categorías, deshacer y rehacer.
- Resumen, nombre y guardado del proyecto.
- Galería, compartir texto del proyecto y exportar su vista como PNG.
- Listado de proyectos y detalle, filtros Activos/Entregados/Borradores y edición.
- Nueva cotización, estados, detalle de costos, descarga y compartir PDF.
- Perfil editable, fotografía elegida con el selector de Android y favoritos.
- Sucursales con intent geo y notificaciones que se pueden marcar como leídas.

El estado de avance del taller y la aprobación de cotizaciones son datos de demostración. No hay un panel administrativo ni un servicio remoto de cambios automáticos. Los clientes no pueden aprobar sus propias cotizaciones.

## Editor por estilos completos

Porsche GT3 RS, BMW M4 Competition y Mustang GT tienen los estilos Racing Red, Urban Dark y Street Blue. Cada estilo define pintura, acabado, vinilos, llantas, body kit, faros, accesorios e interior.

Seleccionar otra opción cambia **todo el preset**. Las categorías muestran estilos completos disponibles; no mezclan configuraciones para las cuales no existe una imagen. El precio y el resumen se actualizan conjuntamente. Las imágenes finales de los nueve estilos deben ser suministradas: véase `ASSETS.md`.

Los importes son ficticios, expresados en USD. Se almacenan como centavos enteros para evitar errores de redondeo. El desglose reparte el importe entre pintura, vinilos/accesorios, llantas/body kit y mano de obra.

## Firebase

Firebase es el proveedor de datos **predeterminado** de la app. `app/google-services.json` viene en el repositorio, pero el proyecto de la consola necesita estos pasos (solo una vez):

1. Registra tu **SHA-1 de depuración** en *Project settings → Your apps* (obligatorio para Google Sign-In). Ver "Acceso con Google" más arriba.
2. Habilita Email/Password **y Google** en Authentication, Firestore y Storage.
3. Publica las reglas de `firebase/firestore.rules` y `firebase/storage.rules`. No se despliegan automáticamente.
4. Importa el catálogo inicial: `node firebase/seed/import.js deluxe-design-32e6d` (usa `firebase/seed/*.json` y conserva los IDs que la app espera).
5. Restringe la clave API por paquete + SHA-1 en Google Cloud Console.

Solo si quieres trabajar sin backend: `.\gradlew.bat assembleDebug -Plocal`.

Guía completa paso a paso: `firebase/SETUP.md`. Contrato de datos y reglas: `firebase/DATA.md`.

La compilación falla con un mensaje claro si solicitas Firebase sin su archivo. Auth, consultas Firestore, proyectos, cotizaciones, favoritos, perfil y subida del avatar tienen implementación Java. Los datos locales no se migran automáticamente. Las imágenes del catálogo y presets siguen siendo recursos empaquetados; las carpetas remotas se reservan para una futura distribución de imágenes. Las notificaciones remotas se leen de Firestore; no se incorpora FCM ni un panel para enviarlas.

Nunca coloques claves de cuenta de servicio ni credenciales de administrador dentro de la app.

## Pruebas

- `DomainTest`: validación, contraseñas, integridad de nueve estilos y exactitud del total.
- `RoomPersistenceTest`: cierre/reapertura de la base y persistencia.
- `WorkflowTest`: acceso, estilo, deshacer/rehacer, proyecto, cotización, navegación por todos los layouts y recreación.
- `RepositoryFlowTest`: registro, perfil, contraseña, recuperación de un solo uso y separación de cuentas.
- `UiInteractionTest`: controles reales de acceso, búsqueda, favorito, estilo, confirmación de salida, proyecto y cotización.
- `ExportTest`: lectura/renderizado del PDF, paginación de notas largas y escritura PDF/PNG mediante MediaStore.

Las capturas instrumentadas se habilitan con `-Pandroid.testInstrumentationRunnerArguments.screenshots=true`. Los resultados reales de esta entrega se describen en `VALIDATION.md`.

## Límites de esta entrega

La fidelidad estructural se basa en el prototipo revisado y el contexto técnico del editor obtenido de Figma. Las fotografías definitivas, el logo corregido y los iconos originales todavía no han sido proporcionados; los placeholders y los iconos de sistema no deben confundirse con recursos finales de marca.

La aplicación usa el esquema oscuro, tarjetas redondeadas, acentos rojos y gradientes del prototipo. La adaptación usa dp/sp, desplazamiento vertical y zonas táctiles de al menos 48 dp, por lo que no conserva posiciones absolutas que pudieran recortar el contenido.

El código propio de la aplicación y sus pruebas es Java. Algunas dependencias AndroidX contienen código Kotlin internamente; no hay plugin Kotlin, archivos `.kt` ni Compose en el proyecto.

No incluye pagos, inteligencia artificial, 3D ni publicación en Google Play. El target 31 es el solicitado para instalación directa.
