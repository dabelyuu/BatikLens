const { Firestore } = require('@google-cloud/firestore');
const dbHistory = require("../config/databases");

async function storeData(id, predictData, uid) {
    try {
        // add data history ke Firestore
        const predictHistory = dbHistory.collection('usersHistories').doc(uid).collection('history');
        return predictHistory.doc(id).set(predictData);

    } catch (error) {
        throw error;
    }
}
 
module.exports = storeData;