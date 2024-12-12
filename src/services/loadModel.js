const tf = require('@tensorflow/tfjs-node');
 
async function loadModel() {
    return tf.loadGraphModel('https://storage.googleapis.com/batiklens-model/model/model.json'); //model.json url here
}
 
module.exports = loadModel;
