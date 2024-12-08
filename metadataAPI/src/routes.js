const getBatikHandler  = require("./handler");

const batikRoutes = [
    {
        method: "GET",
        path: "/metadata/{id}",
        handler: getBatikHandler,
    },
    // {
    //     method: "POST",
    //     path: "/metadata",
    //     options: {
    //         pre: [{ method: validateBatikPayload }],
    //         handler: addBatikHandler,
    //     },
    // },
];

module.exports = batikRoutes;