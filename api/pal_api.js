var restify = require('restify'),
	glob = require( 'glob' ),
	path = require( 'path' ),
	fs = require('fs');

Firebase = require('firebase');
errors = require('restify-errors');
require("./config.js");
_ = require("./helper.js");

var opt = {
	name: 'pal_api',
	version: '0.0.1'
}
server = restify.createServer(opt);
server.use(restify.acceptParser(server.acceptable));
server.use(restify.queryParser());
server.use(restify.bodyParser());
server.use(restify.fullResponse());

server.pre(function(req, res, next) {
	// console.log(req.method, req.url);
	return next();
})

function unknownMethodHandler(req, res) {
	if (req.method.toLowerCase() === 'options') {
		var allowHeaders = ['Accept', 'Accept-Version', 'Content-Type', 'Api-Version', 'Origin', 'X-Requested-With', 'Authorization']; // added Origin & X-Requested-With
		if (res.methods.indexOf('OPTIONS') === -1) res.methods.push('OPTIONS');

		res.header('Access-Control-Allow-Credentials', true);
			res.header('Access-Control-Allow-Headers', allowHeaders.join(', '));
		res.header('Access-Control-Allow-Methods', res.methods.join(', '));
		res.header('Access-Control-Allow-Origin', req.headers.origin);

		return res.send(204);
	} else {
		return res.send(new restify.MethodNotAllowedError());
	}
}

server.on('MethodNotAllowed', unknownMethodHandler);

glob.sync( './modules/**/*.js' ).forEach( function( file ) {
	require( path.resolve( file ) );
});

server.get('/echo/:name', function (req, res, next) {
	res.send(req.params);
	return next();
});

server.on('uncaughtException', function (req, res, route, err) {
	console.log(err.stack);
	res.send(new errors.InternalError(err.message || ""));
});

config = {};
ref = new Firebase(FIREBASE_ENDPOINT);
ref.authWithCustomToken(FIREBASE_TOKEN, function(error, authData) {
	if (error) {
		console.log("Authentication Failed!", error);
		process.exit(0);
	} else {
		ref.child("config").once("value", function(snap) {
			config = snap.val();
			server.listen(PORT, function () {
				ref.child("config").on("value", function(snap) {
					config = snap.val();
				});
				console.log('%s listening at %s', server.name, server.url);
			});
		});
	}
});
