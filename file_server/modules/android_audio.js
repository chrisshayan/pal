var fs = require('fs');
var sys = require('sys');
var crypto = require('crypto');
var exec = require('child_process').exec;

function saveFile(file) {
    var fileRootName = file.name.split('.').shift(),
        fileExtension = file.name.split('.').pop(),
        filePathBase = './uploads/',
        fileRootNameWithBase = filePathBase + fileRootName,
        filePath = fileRootNameWithBase + '.' + fileExtension,
        fileID = 2,
        fileBuffer;

    while (fs.existsSync(filePath)) {
        filePath = fileRootNameWithBase + '(' + fileID + ').' + fileExtension;
        fileID += 1;
    }

    file.contents = file.contents.split(',').pop();
    fileBuffer = new Buffer(file.contents, "base64");
    fs.writeFileSync(filePath, fileBuffer);
}

module.exports = function (req, res) {
    console.log("Receive upload request");
    var fstream;
	req.pipe(req.busboy);
	req.busboy.on('file', function (fieldname, file, filename) {
        console.log("Receive upload request", filename);
		var ext = filename.substring(filename.lastIndexOf("."), filename.length);
		if (ext === ".3gp" || ext === ".mp3" || ext === ".mp4") {
			console.log("Uploading file ... " + filename);
            var des_path = __dirname + '/../uploads/' + filename;
			fstream = fs.createWriteStream(des_path);
			file.pipe(fstream);
			fstream.on('close', function () {
				console.log("Done : " + filename);
				res.json({"url": HOST + filename});
			});
		}
		else {
			res.status(500).send("invalid filetype");
		}
	});
}
