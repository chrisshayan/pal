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
    var fstream;
	req.pipe(req.busboy);
	req.busboy.on('file', function (fieldname, file, filename) {
		var ext = filename.substring(filename.lastIndexOf("."), filename.length);
		if (ext === ".3gp") {
			console.log("Uploading file ... " + filename);
			fstream = fs.createWriteStream(__dirname + '/../uploads/' + filename);
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


/*
function(req, res){
	console.log("Received file:\n" + JSON.stringify(req.files));

	var photoDir = __dirname+"/photos/";
	var thumbnailsDir = __dirname+"/photos/thumbnails/";
	var photoName = req.files.source.name;

	fs.rename(
		req.files.source.path,
		photoDir+photoName,
		function(err){
			if(err != null){
				console.log(err)
				res.send({error:"Server Writting No Good"});
			} else {
				im.resize(
					{
						srcData:fs.readFileSync(photoDir+photoName, 'binary'),
						width:256
					},
					function(err, stdout, stderr){
						if(err != null){
							console.log('stdout : '+stdout)

							res.send({error:"Resizeing No Good"});
						} else {
							//console.log('ELSE stdout : '+stdout)
							fs.writeFileSync(thumbnailsDir+"thumb_"+photoName, stdout, 'binary');
							res.send("Ok");
						}
					}
				);
			}
		}
	);
});
*/
