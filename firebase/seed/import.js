/*
 * Seed Firestore: vehicles, presets y branches conservando los IDs que la app espera.
 *
 * Uso (desde la raíz del repositorio):
 *   npm install firebase-admin
 *   $env:GOOGLE_APPLICATION_CREDENTIALS="<ruta-a-service-account.json>"
 *   node firebase/seed/import.js <PROJECT_ID>
 *
 * La clave de servicio se genera en Firebase Console →
 * Project settings → Service accounts → Generate new private key.
 * Es un secreto; no se versiona y no entra dentro de la app.
 */
const { initializeApp, cert, getApps } = require('firebase-admin/app');
const { getFirestore } = require('firebase-admin/firestore');
const fs = require('fs');
const path = require('path');

const projectId = process.argv[2] || process.env.FIREBASE_PROJECT_ID;
if (!projectId) {
  console.error('Indica el PROJECT_ID: node firebase/seed/import.js <PROJECT_ID>');
  process.exit(1);
}

if (getApps().length === 0) {
  initializeApp({ credential: cert(process.env.GOOGLE_APPLICATION_CREDENTIALS), projectId });
}
const db = getFirestore();

const FILES = ['vehicles.json', 'presets.json', 'branches.json'];

async function main() {
  for (const file of FILES) {
    const collection = path.basename(file, '.json');
    const raw = JSON.parse(fs.readFileSync(path.join(__dirname, file), 'utf8'));
    let count = 0;
    for (const [id, fields] of Object.entries(raw)) {
      await db.collection(collection).doc(id).set(fields);
      count += 1;
    }
    console.log(`${collection}: ${count} documentos (ids conservados)`);
  }
  console.log('Seed completado.');
  process.exit(0);
}

main().catch((e) => {
  console.error('Error al importar:', e.message);
  console.error('¿Firestore creado? ¿Clave de servicio válida?');
  process.exit(1);
});