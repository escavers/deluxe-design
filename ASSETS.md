# Recursos visuales

## Estado

Se recibieron e integraron 13 imágenes del usuario en la versión 1.1.0. Véase `VISUAL_UPDATE.md` para su distribución y `assets_manifest.json` para el inventario actualizado. Inter se obtuvo de Google Fonts; su licencia se incluye en `licenses/`.

El fondo y la fotografía originales consultados en Figma solo sirvieron como referencia; no representan los nueve presets completos. No se han reutilizado para fingir que existe una combinación terminada.

## Sustitución

Copia fotografías PNG o WebP en `app/src/main/res/drawable-nodpi/`, usando los nombres de `assets_manifest.json`. No cambies la lógica Java. AssetImages busca el recurso por nombre y reemplaza automáticamente el placeholder.

- Fotografías de presets: 1200 × 800 px, proporción 3:2, mismo encuadre para las tres vistas.
- Portada: 804 × 1748 px o superior, vehículo oscuro y espacio para textos.
- Fondo del inicio: 804 × 1748 px o superior.
- Logo: PNG transparente de al menos 512 × 512 px, nombre DELUXE DESIGN corregido.
- Retrato: se elige desde la app; no es obligatorio empaquetarlo.
- Iconos: pendientes de exportación final. La barra utiliza iconos de Android sustitutos.
- Fuente de títulos futurista: pendiente de identificación/exportación. Los títulos actuales usan sans-serif-medium; el cuerpo utiliza Inter.

Las fotografías deben coincidir con los valores documentados en SeedData: pintura, acabado, vinilo, llantas, body kit, faros, accesorios e interior. Cambiar solamente un nombre de archivo no crea una nueva personalización.

## Archivos comunes pendientes

- `brand_cover.webp`: portada y acceso.
- `home_background.webp`: imagen del panel inicial.
- `brand_logo.png`: sustituir el emblema temporal del perfil y configurar el logo principal.

Los 27 archivos finales de vistas de presets están enumerados en el manifiesto. Hasta suministrarlos se muestran las fotografías recibidas como referencias, con una advertencia visible que también aparece en las exportaciones.
