# DELUXE DESING — Android nativo

Implementación Java del prototipo de **DELUXE DESING**, preparada para Android Studio y Android 12.

## Requisitos

- Android Studio Arctic Fox o posterior.
- JDK 17.
- Android SDK Platform 31 y Build Tools 31.x.

## Abrir el proyecto

1. En Android Studio, selecciona **Open**.
2. Elige esta carpeta: `DeluxeDesing v4`.
3. Instala Android SDK Platform 31 si Android Studio lo solicita.
4. Sincroniza Gradle y ejecuta en un emulador o dispositivo con Android 6.0+.

## Cobertura del prototipo

- Portada, inicio de sesión, registro y recuperación/actualización de contraseña.
- Inicio con accesos rápidos y proyectos recientes.
- Catálogo, filtros, ficha técnica y favoritos visuales.
- Editor de vehículo con color, acabados, categorías, historial de deshacer/rehacer y guardado.
- Confirmación, proyecto listo, proyectos activos/entregados/borradores y detalle de proyecto.
- Cotizaciones, creación de cotización, desglose y total.
- Perfil, vehículos, sucursales, soporte y notificaciones.
- Barra inferior persistente con las cinco secciones del diseño.

La previsualización del auto es un `VehicleView` dibujado de forma nativa, lo que hace funcional el cambio de color sin incluir fotografías de terceros. Para una réplica visual píxel a píxel de las fotografías de Figma, exporta y agrega los archivos originales como recursos en `app/src/main/res/drawable-nodpi/`.

## Arquitectura

La aplicación está escrita al 100 % en Java. `MainActivity` contiene el flujo navegable y los componentes de interfaz nativos; no requiere Kotlin ni dependencias de terceros. Los colores de marca están en `app/src/main/res/values/colors.xml` y el objetivo de compilación es API 31.
