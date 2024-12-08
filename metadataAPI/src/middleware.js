//middleware
const validateBatikPayload = (request, h) => {
    const { id, nama_batik, asal_batik, sejarah_batik } = request.payload;

    if (!id || !nama_batik || !asal_batik || !sejarah_batik) {
        return h.response({
            success: false,
            message: "Semua field (id, nama_batik, asal_batik, sejarah_batik) harus diisi.",
        }).code(400).takeover();
    }

    if (typeof id !== "number") {
        return h.response({
            success: false,
            message: "ID harus berupa angka.",
        }).code(400).takeover();
    }

return h.continue;
};

module.exports = validateBatikPayload;
