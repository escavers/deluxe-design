# Actualización visual 1.1.0

Se integraron las 13 imágenes proporcionadas, conservando los archivos originales sin retoque ni generación de variantes.

- Logo en portada, acceso, perfil e icono de la aplicación. En portada y acceso se muestra en un encuadre circular mediante una vista Java, sin alterar el PNG.
- Fondo Porsche en todas las pantallas, con superposiciones oscuras para proteger la legibilidad. Portada y acceso dejan más visible la fotografía.
- Fotografía Porsche con iluminación roja como imagen destacada de Inicio.
- Porsche, BMW M4 y Mustang con fotografías reales en el catálogo y como referencias en proyectos y personalizador.
- Las siete imágenes adicionales forman la galería «Inspiración Deluxe» en Inicio. Cada tarjeta permite abrir la imagen completa; el collage del Mustang se conserva íntegro.
- Los layouts XML muestran recursos reales en sus previsualizaciones.

El logo se conserva exactamente como fue recibido, incluida la palabra «DESING» de la imagen; los textos de la aplicación mantienen «DELUXE DESIGN».

## Imágenes de estilos y ángulos

Los archivos entregados no contienen las 27 vistas finales de los nueve presets. Por ello, el personalizador muestra la fotografía del vehículo con el aviso «FOTO DE REFERENCIA / Estilo y vista finales pendientes». Seleccionar un estilo sigue actualizando su configuración y precio, pero no se inventa una fotografía azul, lateral o trasera.

El aviso también se conserva al exportar una imagen de referencia. Al incorporar un archivo con el nombre final de preset descrito en el manifiesto, la aplicación utilizará ese recurso sin el aviso.

Audi, Volkswagen y las pickups se utilizan como inspiración, no como nuevos modelos del editor. Se mantienen los tres vehículos personalizables acordados. Algunas imágenes suministradas son pequeñas; se preservan sus proporciones, pero pueden mostrar menor nitidez al ampliarlas.

La versión 1.1.0 utiliza la misma firma de depuración que la entrega anterior y conserva Room. Se puede instalar como actualización sin borrar los datos locales.

## Verificación de esta versión

- `assembleDebug`, pruebas unitarias y `lintDebug`: compilación correcta; 5 pruebas unitarias sin fallos; Lint sin errores y con 47 avisos.
- Instalación como actualización en el emulador Android 12/API 31: correcta.
- AndroidJUnitRunner: 5 pruebas instrumentadas sin fallos, 32,986 segundos. Incluyen navegación por las 21 pantallas, controles, persistencia y exportaciones.
- Se revisaron capturas de portada, acceso, inicio, catálogo, personalizador y perfil con los recursos suministrados. Evidencia en `validation/visual-v1.1/`.
- Se conservó la configuración del SDK de Android Studio del usuario; la compilación de validación se realizó en una copia temporal con JDK 11 y el SDK de pruebas.

APK: `DeluxeDesign-visual-debug.apk`. SHA-256:

```text
a3aa511d69656ac1e3d11a352dcf73ceb87318b7715ac4a9ca6e73d4c35b91b8
```
