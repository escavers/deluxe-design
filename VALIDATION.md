# Validación de la entrega

Este informe corresponde a la versión inicial 1.0.0. Los resultados y capturas de la actualización con imágenes 1.1.0 se encuentran en `VISUAL_UPDATE.md` y `validation/visual-v1.1/`.

Fecha: 22 de septiembre de 2026. Versión: 1.0.0, paquete `com.deluxedesign.app`.

## Resultados comprobados

| Comprobación | Resultado |
| --- | --- |
| `assembleDebug` | Correcto; APK generado e instalado |
| `testDebugUnitTest` | 5 pruebas, 0 fallos |
| `assembleDebugAndroidTest` | Correcto |
| Pruebas instrumentadas en Android 12 | 5 pruebas, 0 fallos; 29,856 segundos |
| `lintDebug` | 0 errores; 35 avisos no bloqueantes |
| Firma APK | Verificada con APK Signature Scheme v2 |
| SDK mínimo / objetivo / compilación | 31 / 31 / 31 |
| Lenguaje propio | 56 archivos Java de aplicación; ningún archivo Kotlin |
| Pantallas | 21 destinos con Fragment Java y XML, abiertos durante las pruebas |
| Orientación | Vertical, declarada en el manifiesto |

Entorno: emulador AOSP Android 12/API 31 x86_64, pantalla 402 × 874, densidad 160 dpi. Compilación con JDK 11.0.27, Gradle 7.3.3 y Android Gradle Plugin 7.2.2.

## Cobertura funcional

- Validación de formularios, hash y comprobación de contraseñas con sal, nueve presets y cálculo de importes en centavos.
- Registro, modificación del perfil, cambio de contraseña, acceso correcto e incorrecto, recuperación local y rechazo de reutilización del código.
- Separación de cuentas al modificar favoritos.
- Persistencia Room al cerrar y volver a abrir la base de datos.
- Controles reales de acceso, búsqueda con resultados y sin resultados, favorito, selección de Urban Dark y aviso al salir sin guardar.
- Flujo de controles: catálogo → vehículo → personalización → confirmación → proyecto guardado → cotización.
- Deshacer/rehacer, guardado de proyecto, creación de cotización y apertura de los 21 layouts.
- Recreación de la actividad conservando la selección.
- Exportación PDF, reapertura con PdfRenderer, paginación de notas largas y escritura mediante MediaStore.
- Exportación PNG de 1200 × 800 y lectura posterior del archivo.
- Cierre forzado y reapertura sin Wi-Fi ni datos: sesión y proyectos conservados; aviso de modo local visible.

Los resultados de JUnit y del runner Android están en `validation/unit-tests.xml` y `validation/instrumentation.txt`. Las capturas del emulador están en `validation/screenshots/`; muestran datos generados por las pruebas, que no se incorporan al APK.

## Revisión visual y límites

Se revisaron visualmente las capturas de las 21 pantallas y una cotización PDF renderizada. Los contenidos largos se desplazan verticalmente y la barra inferior queda separada del contenido. Las fotografías, el logo definitivo y los iconos originales siguen pendientes; no se afirma fidelidad visual completa a Figma mientras se utilicen placeholders. El frame P2 no se implementó como pantalla funcional.

Los XML incluyen contenido de referencia, recursos editables y atributos `tools:` para el Layout Editor. Se verificó su compilación e inflación en Android, pero **no se realizó una comprobación manual dentro del Layout Editor de Android Studio**. Tampoco se probó un teléfono físico ni todas las escalas de accesibilidad.

La recreación de actividad y la reapertura tras cierre forzado se comprobaron; no se simuló por separado cada escenario de eliminación del proceso por falta de memoria.

Firebase permanece desactivado. Su código y dependencias compilan, pero no se verificó la conexión a un proyecto real ni se desplegaron reglas sin credenciales. Las acciones de compartir y los intents de mapas están implementados; no se realizó un envío externo ni se validaron todas las aplicaciones receptoras.

Los 35 avisos de Lint son: 21 de simetría RTL, 11 recursos reservados sin uso, uno de reglas de extracción de datos, uno por orientación bloqueada —requisito de esta entrega— y uno de posible sobredibujado. No se ocultaron errores mediante un baseline.

## Particularidades del entorno de compilación

El entorno restringido de Windows produjo una excepción de acceso al cerrar archivos JAR en el compilador integrado. Se utilizó el mismo JDK 11 con `javac` en proceso separado mediante una configuración externa de validación. Gradle terminó correctamente; los errores de código seguían causando fallos de compilación. La configuración externa de firma y las rutas temporales no se incluyen en el ZIP portable.

El acceso al emulador se realizó mediante el protocolo ADB local porque el ejecutable ADB no pudo resolver su directorio de perfil dentro de ese entorno. Se instalaron los APK y se ejecutó AndroidJUnitRunner en el emulador real; los resultados no son simulados.

Para reproducir las pruebas en un entorno Android Studio convencional, utiliza los comandos del README y un emulador API 31 conectado.

## APK entregado

Archivo: `DeluxeDesign-debug.apk`, junto al ZIP del proyecto. Firma de depuración; destinado a evaluación e instalación directa, no a producción.

SHA-256:

```text
b24b19a89a50951f98726524d8d9734ac762f827c927fdaf93a597236dc48dbc
```
