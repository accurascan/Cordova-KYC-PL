const fs = require('fs');
const os = require('os');
const srcPath = __dirname.replace('scripts', '');
//Code for copy license files into Project root
var srcParentPath = __dirname.replace('plugins\\cordova-accura-kyc-pl\\scripts', 'platforms\\android');
var fcPath = srcPath + 'src\\android\\accuraface.license';
var ocrPath = srcPath + 'src\\android\\key.license';
var fcDestPath = srcParentPath + '\\app\\src\\main\\assets\\accuraface.license';
var ocrDestPath = srcParentPath + '\\app\\src\\main\\assets\\key.license';
var gridlePath = srcParentPath + "\\app\\build.gradle";
if (['linux', 'darwin'].indexOf(os.platform()) !== -1) {
    srcParentPath = __dirname.replace('plugins/cordova-accura-kyc-pl/scripts', 'platforms/android');
    fcPath = srcPath + 'src/android/accuraface.license';
    ocrPath = srcPath + 'src/android/key.license';
    fcDestPath = srcParentPath + '/app/src/main/assets/accuraface.license';
    ocrDestPath = srcParentPath + '/app/src/main/assets/key.license';
    gridlePath = srcParentPath + "/app/build.gradle";
}

fs.copyFileSync(fcPath, fcDestPath);
fs.copyFileSync(ocrPath, ocrDestPath);
