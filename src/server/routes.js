const { postPredictBatikHandler, getMetadataHandler, getHistoryHandler, getAllMetadataHandler } = require('./handler');
const authenticateUser = require('../middlewares/authenticateUser');
 
const routes = [
  {
    path: '/predict',
    method: 'POST',
    handler: postPredictBatikHandler,
    options: {
      pre: [{ method: authenticateUser }], // Middleware untuk autentikasi
      payload: {
        allow: 'multipart/form-data',
        multipart: true
      }
    }
  },
  {
    path: "/history",
    method: "GET",
    handler: getHistoryHandler,
    options: {
      pre: [{ method: authenticateUser }],
    },
  },
  {
    path: "/batikmetadata/{id}",
    method: "GET",
    handler: getMetadataHandler,
  },
  {
    path: "/allbatiksmetadata",
    method: "GET",
    handler: getAllMetadataHandler,
  }
]
 
module.exports = routes;