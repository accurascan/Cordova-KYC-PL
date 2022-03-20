const fs = require('fs');
const os = require('os');
const srcPath = __dirname.replace('scripts', '');
//Code for remove license files from Project root
var srcParentPath = __dirname.replace('plugins\\cordova-accura-kyc-pl\\scripts', 'platforms\\android');
var fcDestPath = srcParentPath + '\\app\\src\\main\\assets\\accuraface.license';
var ocrDestPath = srcParentPath + '\\app\\src\\main\\assets\\key.license';
var gridlePath = srcParentPath + "\\app\\build.gradle";
if (['linux', 'darwin'].indexOf(os.platform()) !== -1) {
    srcParentPath = __dirname.replace('plugins/cordova-accura-kyc-pl/scripts', 'platforms/android');
    fcDestPath = srcParentPath + '/app/src/main/assets/accuraface.license';
    ocrDestPath = srcParentPath + '/app/src/main/assets/key.license';
    gridlePath = srcParentPath + "/app/build.gradle";
}
try {
    fs.unlinkSync(fcDestPath);
}catch (e) {

}
try {
    fs.unlinkSync(ocrDestPath);
}catch (e) {

}
