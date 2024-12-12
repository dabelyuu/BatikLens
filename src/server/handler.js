const predictBatikClass = require('../services/inferenceService');
const storeData = require('../services/storeData');
const { dbMetadata, dbHistory, imgStorage } = require("../config/databases");

const crypto = require('crypto');

const bucketName = "batiks-image";
const bucket = imgStorage.bucket(bucketName);

async function postPredictBatikHandler(request, h) {
  const { uid } = request.auth; // UID pengguna
  const { image } = request.payload;
  const { model } = request.server.app;

  try {
    const { confidenceScore, classIndex } = await predictBatikClass(model, image);
    const idBatik = classIndex + 1; //for sync with metadata, cuz firestore can't save 0 as docs id

    // Ambil metadata dari Firestore
    const batikDoc = dbMetadata.collection('metadata').doc(idBatik.toString());
    const snapshot = await batikDoc.get();

    if (!snapshot.exists) {
        return h.response({
            status: 'fail',
            message: `Metadata untuk batik dengan ID ${idBatik} tidak ditemukan.`,
        }).code(404);
    }

    const metadata = snapshot.data();

    const id = crypto.randomUUID();
    const createdAt = new Date().toISOString();

    const predictData = {
      "id": id,
      "idBatik": idBatik,
      "result": metadata.namaBatik,
      "confidenceScore": confidenceScore,
      "createdAt": createdAt
    };

    await storeData(id, predictData, uid);

    // Mengambil gambar dari Google Cloud Storage
    const [files] = await bucket.getFiles({
      prefix: `batik-${idBatik}`,
    });

    // This is to make signed URL
    const file = files.length > 0 ? bucket.file(files[0].name) : null;
    const signedUrl = file
      ? await file.getSignedUrl({
          action: "read",
          expires: Date.now() + 3600000, // URL berlaku selama 1 jam
        })
      : null;

    const imageInfo = signedUrl
      ? { url: signedUrl }
      : { message: "Gambar tidak ditemukan" };

    const response = h.response({
      status: 'success',
      message: 'Model is predicted successfully. And saved to history',
      predictData,
      metadata,
      "imageUrl": imageInfo
    })
    response.code(201);
    return response;
    
  } catch (error) {
    // This will be handled by the onPreResponse extension in server.js
    throw error;
  }
};

const getMetadataHandler = async (request, h) => {
  const { id } = request.params;

  try {
      const batikDoc = dbMetadata.collection("metadata").doc(id.toString());
      const snapshot = await batikDoc.get();

      if (!snapshot.exists) {
          return h.response({
              success: false,
              message: `Batik dengan ID "${id}" tidak ditemukan.`,
          }).code(404);
      }

      // Mengambil gambar dari Google Cloud Storage
      const [files] = await bucket.getFiles({
        prefix: `batik-${id}`,
      });

      // This is to make signed URL
      const file = files.length > 0 ? bucket.file(files[0].name) : null;
      const signedUrl = file
        ? await file.getSignedUrl({
            action: "read",
            expires: Date.now() + 3600000, // URL berlaku selama 1 jam
          })
        : null;

      const imageInfo = signedUrl
        ? { url: signedUrl }
        : { message: "Gambar tidak ditemukan" };

      return h.response({ 
        success: true, 
        data: snapshot.data(),
        "imageUrl": imageInfo
      }).code(200);

  } catch (error) {
      console.error(error);
      return h.response({ 
        success: false, 
        message: error.message 
      }).code(500);
  }
};

const getHistoryHandler = async (request, h) => {
  const { uid } = request.auth;

  try {
    // Mengambil koleksi history untuk user yang sesuai
    const historyCollection = await dbHistory.collection("usersHistories").doc(uid).collection("history").get();

    if (historyCollection.empty) {
      return h
        .response({
          success: false,
          message: `Tidak ada history untuk user dengan ID ${uid}.`,
        })
        .code(404);
    }

    // user histories will be dumped here
    const historyList = [];

    for (const doc of historyCollection.docs) {
      const historyData = doc.data();

      // Mengambil metadata dari koleksi metadata berdasarkan idBatik
      const metadataDoc = await dbMetadata.collection("metadata").doc(historyData.idBatik.toString()).get();
      const metadata = metadataDoc.exists
        ? metadataDoc.data()
        : { message: "Metadata tidak ditemukan" };

      // Mengambil gambar dari Google Cloud Storage
      const [files] = await bucket.getFiles({
        prefix: `batik-${historyData.idBatik}`,
      });

      // This is to make signed URL
      const file = files.length > 0 ? bucket.file(files[0].name) : null;
      const signedUrl = file
        ? await file.getSignedUrl({
            action: "read",
            expires: Date.now() + 3600000, // URL berlaku selama 1 jam
          })
        : null;

      const imageInfo = signedUrl
        ? { url: signedUrl }
        : { message: "Gambar tidak ditemukan" };

      historyList.push({
        "history": historyData,
        "metadata": metadata,
        "imageUrl": imageInfo,
      });
    }
    return h
      .response({
        success: true,
        data: historyList,
      })
      .code(200);
  } catch(error) {
    console.error("Failed to get history:", error);
    return h
      .response({
        success: false,
        message: "Terjadi kesalahan saat mengambil data history.",
      })
      .code(500);
  }
};


const getAllMetadataHandler = async (request, h) => {
  try {
      // Mengambil semua dokumen dari koleksi "metadata"
      const metadataCollection = await dbMetadata.collection("metadata").get();

      if (metadataCollection.empty) {
          return h.response({
              success: false,
              message: "Tidak ada metadata yang ditemukan."
          }).code(404);
      }

      // List untuk menyimpan semua metadata
      const metadataList = [];

      for (const doc of metadataCollection.docs) {
          const metadata = doc.data();
          const id = doc.id;

          // Mengambil gambar dari Google Cloud Storage
          const [files] = await bucket.getFiles({
              prefix: `batik-${id}`,
          });

          // Membuat signed URL
          const file = files.length > 0 ? bucket.file(files[0].name) : null;
          const signedUrl = file
              ? await file.getSignedUrl({
                  action: "read",
                  expires: Date.now() + 3600000, // URL berlaku selama 1 jam
              })
              : null;

          const imageInfo = signedUrl
              ? { url: signedUrl }
              : { message: "Gambar tidak ditemukan" };

          // Menambahkan metadata ke dalam list
          metadataList.push({
              id,
              ...metadata,
              imageUrl: imageInfo,
          });
      }

      return h.response({
          success: true,
          data: metadataList
      }).code(200);

  } catch (error) {
      console.error(error);
      return h.response({
          success: false,
          message: error.message
      }).code(500);
  }
};

module.exports = { postPredictBatikHandler, getMetadataHandler, getHistoryHandler, getAllMetadataHandler };