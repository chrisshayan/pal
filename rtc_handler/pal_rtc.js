var express = require('express');
var cors = require('cors')
var bodyParser = require('body-parser');
var post_audio = require('./modules/post_audio');
var app = express();
require("./config");

app.use(cors());
app.use(bodyParser.json({limit: '50mb'}));
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static('uploads'))
// test route
app.get('/', function (req, res) { res.status(200).send('Hello world!') });
app.post('/post_audio', post_audio);

// error handler
app.use(function (err, req, res, next) {
    if (err) {
        console.error(err.stack);
        res.status(400).send(err.message);
    }
    next();
});

app.listen(PORT, function () {
    console.log('Listening on port ' + PORT);
});
