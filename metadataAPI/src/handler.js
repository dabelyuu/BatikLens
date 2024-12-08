const { Firestore } = require("@google-cloud/firestore");

// Handler untuk mengambil data batik berdasarkan ID
const getBatikHandler = async (request, h) => {
    const { id } = request.params;
    const db = new Firestore();

    try {
        const batikDoc = db.collection("batik").doc(id.toString());
        const snapshot = await batikDoc.get();

        if (!snapshot.exists) {
            return h.response({
                success: false,
                message: `Batik dengan ID "${id}" tidak ditemukan.`,
            }).code(404);
        }
        return h.response({ success: true, data: snapshot.data() }).code(200);
    } catch (error) {
        console.error(error);
        return h.response({ success: false, message: error.message }).code(500);
    }
};

module.exports = getBatikHandler;