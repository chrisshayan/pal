var express = require('express'),
    cors = require('cors'),
    bodyParser = require('body-parser'),
    busboy = require('connect-busboy'),
    cloudinary = require('cloudinary'),
    app = express();
require("./config");

cloudinary.config({
    cloud_name: CLOUDINARY_CLOUD_NAME,
    api_key: CLOUDINARY_API_KEY,
    api_secret: CLOUDINARY_API_SECRET
});

app.use(cors());
app.use(bodyParser.json({limit: '50mb'}));
app.use(bodyParser.urlencoded({ extended: true }));
app.use(busboy());

app.get('/', function (req, res) { res.status(200).send('Hello world!') });

app.post('/post_rtc', function(req, res) {
    var body = req.body;
    var filename = body.name;
    var contents = body.contents;
    var type = body.type;

    //save to temp
    contents = contents.split(',').pop();
    var fileBuffer = new Buffer(contents, "base64");
    var temp_filename = "./temp/_" + Date.Now() + filename;
    fs.writeFile(temp_filename, fileBuffer, function(err) {
        if (err) {
            res.status(500).send(err);
        } else {
            cloudinary.uploader.upload_large(temp_filename, function(result) {
                fs.unlink(temp_filename);
                res.status(200).send(result);
            }, {
                public_id: filename,
                resource_type: "video",
                folder: "pal_recorder",
                chunk_size: 6000000
            });
        }
    });
});

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
