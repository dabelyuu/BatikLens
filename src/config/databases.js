const { Firestore } = require('@google-cloud/firestore');
const { Storage } = require('@google-cloud/storage');
const serviceAccount = JSON.parse(process.env.FIREBASE_ADMIN_SDK_KEY); //your cab also use firebase service account token here

//firestore databases
const dbMetadata = new Firestore({
    databaseId: "metadata",
});
const dbHistory = new Firestore({
    databaseId: "history",
});

//cloud bucket storage (batik image)
const imgStorage = new Storage({
    projectId: serviceAccount.project_id,
    credentials: serviceAccount,
});

module.exports = {
    dbMetadata,
    dbHistory,
    imgStorage
};
  