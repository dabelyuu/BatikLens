const Hapi = require('@hapi/hapi');
const batikRoutes = require('./routes');

const init = async () => {
    const server = Hapi.server({
        port: 3000,
        host: 'localhost',
    });

    server.route(batikRoutes);

    await server.start();
    console.log(`Server berjalan pada ${server.info.uri}`);
};

process.on('unhandledRejection', (err) => {
    console.error(err);
    process.exit(1);
});

init();
