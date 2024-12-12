const Hapi = require('@hapi/hapi');
const routes = require('../server/routes');
const InputError = require('../exceptions/InputError');
const loadModel = require('../services/loadModel');

const admin = require('firebase-admin');
const serviceAccount = JSON.parse(process.env.FIREBASE_ADMIN_SDK_KEY); //firebase sevice account token here to use the SDK

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

(async () => {

    const server = Hapi.server({
        port: 8080,
        host: '0.0.0.0',
        routes: {
            cors: {
              origin: ['*'],
            },
        },
    })

    const model = await loadModel();
    server.app.model = model;

    server.route(routes);  

    server.ext('onPreResponse', function (request, h) {
        const response = request.response;
        
        if (response instanceof InputError) {
            const newResponse = h.response({
                status: 'fail',
                message: `${response.message}`
            })
            newResponse.code(response.statusCode)
            return newResponse;
        }
        if (response.isBoom) {
            const newResponse = h.response({
                status: 'fail',
                message: response.message
            });
            newResponse.code(response.output.statusCode);
            return newResponse;
        }
        return h.continue;
    });

    await server.start();
    console.log(`Server start at: ${server.info.uri}`);
})();