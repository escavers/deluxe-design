# Contrato de datos Firebase

Los documentos usan los campos Java públicos de `domain/model`. El campo `id` debe coincidir con el identificador del documento. Los precios se expresan en centavos de USD y las fechas en milisegundos Unix.

- users/{uid}: id, name, email, phone, avatar. No introducir passwordHash; Firebase Auth administra la contraseña.
- vehicles/{id}: id, name, brand, type, year, engine, transmission, power, traction, weight, image.
- presets/{id}: id, vehicleId, name, front, side, rear, paint, finish, vinyl, wheels, bodyKit, lights, accessories, interior, priceCents.
- branches/{id}: id, name, address, hours, phone, latitude, longitude.
- projects/{id}: id, userId, vehicleId, presetId, name, status, progress, createdAt, estimatedDelivery.
- quotes/{id}: id, userId, projectId, customerName, notes, status, itemsJson, totalCents, createdAt.
- favorites/{uid_vehicleId}: id, userId, vehicleId.
- notifications/{id}: id, userId, title, message, createdAt, read.

itemsJson es un arreglo JSON serializado como cadena, por ejemplo: `[{"label":"Pintura","amountCents":1200000}]`. Conserva el mismo contrato que Room para que los repositorios sean intercambiables.

Los catálogos iniciales se encuentran en SeedData.java mientras que los archivos `seed/*.json` de esta carpeta los replican en Firestore conservando los IDs que la app espera (`porsche`, `bmw`, `mustang`, presets y sucursales). Impórtalos con `node firebase/seed/import.js <PROJECT_ID>` (ver `firebase/SETUP.md`); la consola Firestore no permite fijar IDs al pegar JSON. No se incorporan credenciales privilegiadas ni se escriben catálogos automáticamente desde un cliente.

Los estados de proyecto son Activo, Entregado y Borrador. Una cotización puede ser Pendiente, En revisión o Aprobada. Los cambios de progreso y aprobación se realizan desde un servicio de confianza o Firebase Console; las reglas impiden que el cliente los modifique.

Storage: profiles/{uid}/avatar para fotografía del usuario; vehicles/{vehicleId}/ y projects/{uid}/{projectId}/ quedan reservados. Las exportaciones de esta primera versión se guardan localmente.
