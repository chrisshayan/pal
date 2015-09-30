var fs = require('fs');
var sys = require('sys');
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
    var body = req.body;
    saveFile({
        name: body.name,
        contents: body.contents,
        type: body.type
    });

    return res.status(200).json({name: body.name, url: HOST + "/" + body.name});
}
