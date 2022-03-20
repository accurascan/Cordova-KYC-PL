package accura.kyc.plugin;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import com.accurascan.ocr.mrz.model.BarcodeFormat;
import com.accurascan.ocr.mrz.model.ContryModel;
import com.accurascan.ocr.mrz.util.AccuraLog;
import com.docrecog.scan.RecogEngine;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import com.androidnetworking.AndroidNetworking;

public class ACCURAService extends CordovaPlugin {
    public static Bitmap face1 = null;
    public static Bitmap face2 = null;
    public static CallbackContext faceCL = null;
    public static CallbackContext ocrCL = null;
    public static boolean ocrCLProcess = false;
    public static boolean isLivenessGetVideo = false;
    public static String livenessVideo = "";
    public static final String CAMERA = Manifest.permission.CAMERA;
    public static final String WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final int SEARCH_REQ_CODE = 0;
    private static final String TAG = OcrActivity.class.getSimpleName();
    public static JSONObject messagesConf = null;
    
    public ACCURAService() {
        super();
    }
    public static String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }

    public static Bitmap getBitmap(ContentResolver cr, Uri url)
            throws FileNotFoundException, IOException {
        InputStream input = cr.openInputStream(url);
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        return bitmap;
    }

    public static Bitmap getBase64ToBitmap(String base64Image) {

        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public static String getImageUri(Bitmap bitmap, String name, String path) {
        OutputStream fOut = null;
        File file = new File(path, getSaltString() + "_" + name + ".jpg");
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush(); // Not really required
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "file://"+file.getAbsolutePath();
    }
    protected void getCameraPermission()
    {
        cordova.requestPermission(this, ACCURAService.SEARCH_REQ_CODE, CAMERA);
    }
    protected void getWritePermission()
    {
        cordova.requestPermission(this, ACCURAService.SEARCH_REQ_CODE, WRITE);
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        for(int r:grantResults)
        {
            if(r == PackageManager.PERMISSION_DENIED)
            {
                this.pCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Permission Not Granted. App cannot be used"));
                return;
            }
        }
        if (requestCode == SEARCH_REQ_CODE) {
            execute(pAction, pArgs, pCallbackContext);
        }
    }
    CallbackContext pCallbackContext = null;
    JSONArray pArgs = null;
    String pAction = null;
    public int R(String name, String type){
       return cordova.getActivity().getResources().getIdentifier(name, type, cordova.getActivity().getPackageName());
    }
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if(!cordova.hasPermission(CAMERA))
        {
            pCallbackContext = callbackContext;
            pArgs = args;
            pAction = action;
            getCameraPermission();
            return true;
        }

        //Code for get license info from SDK.
        if (action.equals("getMetadata")) {
            ocrCL = callbackContext;
            RecogEngine recogEngine = new RecogEngine();
            AccuraLog.enableLogs(false);
            recogEngine.setDialog(false);
            JSONObject results = new JSONObject();
            RecogEngine.SDKModel sdkModel = recogEngine.initEngine(cordova.getContext());
            if (sdkModel.i >= 0) {
                AndroidNetworking.initialize(cordova.getContext(), UnsafeOkHttpClient.getUnsafeOkHttpClient());
//                results.put("sdk_version", recogEngine.getVersion());
                results.put("isValid", true);
                // if OCR enable then get card list
                if (sdkModel.isOCREnable) {
                    results.put("isOCR", true);
                    List<ContryModel> modelList = recogEngine.getCardList(cordova.getActivity());
                    JSONArray countries = new JSONArray();
                    for (int i = 0; i < modelList.size(); i++) {
                        JSONObject country = new JSONObject();
                        country.put("name", modelList.get(i).getCountry_name());
                        country.put("id", modelList.get(i).getCountry_id());
                        JSONArray cards = new JSONArray();
                        List<ContryModel.CardModel> cardList = modelList.get(i).getCards();
                        for (int j = 0; j < cardList.size(); j++) {
                            JSONObject card = new JSONObject();
                            card.put("name", cardList.get(j).getCard_name());
                            card.put("id", cardList.get(j).getCard_id());
                            card.put("type", cardList.get(j).getCard_type());
                            cards.put(card);
                        }
                        country.put("cards", cards);
                        countries.put(country);
                    }
                    results.put("countries", countries);
                }
                results.put("isOCREnable", sdkModel.isOCREnable);
                results.put("isBarcode", sdkModel.isAllBarcodeEnable);
                if (sdkModel.isAllBarcodeEnable) {
                    List<BarcodeFormat> CODE_NAMES = BarcodeFormat.getList();
                    JSONArray barcodes = new JSONArray();
                    for (int i = 0; i < CODE_NAMES.size(); i++) {
                        JSONObject barcode = new JSONObject();
                        barcode.put("name", CODE_NAMES.get(i).barcodeTitle);
                        barcode.put("type", CODE_NAMES.get(i).formatsType);
                        barcodes.put(barcode);
                    }
                    results.put("barcodes", barcodes);
                }
                results.put("isBankCard", sdkModel.isBankCardEnable);
                results.put("isMRZ", sdkModel.isMRZEnable);

            } else {
                results.put("isValid", false);
            }
            callbackContext.success(results);
            return true;
        }
        //Code for celan face match.
        if (action.equals("cleanFaceMatch")) {
            ACCURAService.face1 = null;
            ACCURAService.face2 = null;
            ACCURAService.isLivenessGetVideo = false;
            ACCURAService.livenessVideo = "";
            return true;
        }
        JSONObject accuraConf = args.getJSONObject(0);

        if (accuraConf.has("enableLogs")) {
            boolean isLogEnable = accuraConf.getBoolean("enableLogs");
            if (isLogEnable) {
                if(!cordova.hasPermission(WRITE))
                {
                    pCallbackContext = callbackContext;
                    pArgs = args;
                    pAction = action;
                    getWritePermission();
                    return true;
                }
            }
        }

        //Code for setup config & messages into scanning window.
        if (action.equals("setupAccuraConfig")) {

            JSONObject messagesConf = args.getJSONObject(0);
            ACCURAService.messagesConf = messagesConf;
            callbackContext.success("Messages setup successfully");
            return true;
        }
        
        //Code for OCR documents scanning with country & card selection.
        if (action.equals("startOcrWithCard")) {
            int country = args.getInt(1);
            int card = args.getInt(2);
            String cardName = args.getString(3);
            int cardType = args.getInt(4);
            String appOrientation = args.getString(5);
            Intent myIntent = new Intent(cordova.getActivity(), OcrActivity.class);
            if (ACCURAService.messagesConf != null) {
              myIntent = addDefaultConfigs(myIntent, ACCURAService.messagesConf);
            }
            myIntent = addDefaultConfigs(myIntent, accuraConf);
            myIntent.putExtra("app_orientation", appOrientation);
            myIntent.putExtra("type", "ocr");
            myIntent.putExtra("country_id", country);
            myIntent.putExtra("card_id", card);
            myIntent.putExtra("card_name", cardName);
            myIntent.putExtra("card_type", cardType);
            ocrCL = callbackContext;
            cordova.getActivity().startActivity(myIntent);
            return true;
        }

        //Code for MRZ document scanning.
        if (action.equals("startMRZ")) {
            String type = args.getString(1);
            String countryList = args.getString(2);
            String appOrientation = args.getString(3);
            Intent myIntent = new Intent(cordova.getActivity(), accura.kyc.plugin.OcrActivity.class);
            if (ACCURAService.messagesConf != null) {
              myIntent = addDefaultConfigs(myIntent, ACCURAService.messagesConf);
            }
            myIntent = addDefaultConfigs(myIntent, accuraConf);
            myIntent.putExtra("type", "mrz");
            myIntent.putExtra("country-list", countryList);
            myIntent.putExtra("sub-type", type);
            myIntent.putExtra("app_orientation", appOrientation);
            ocrCL = callbackContext;
            cordova.getActivity().startActivity(myIntent);
            return true;
        }

        //Code for Bankcard scanning.
        if (action.equals("startBankCard")) {
            Intent myIntent = new Intent(cordova.getActivity(), OcrActivity.class);
            String appOrientation = args.getString(1);
            if (ACCURAService.messagesConf != null) {
              myIntent = addDefaultConfigs(myIntent, ACCURAService.messagesConf);
            }
            myIntent = addDefaultConfigs(myIntent, accuraConf);
            myIntent.putExtra("type", "bankcard");
            myIntent.putExtra("app_orientation", appOrientation);
            ocrCL = callbackContext;
            cordova.getActivity().startActivity(myIntent);
            return true;
        }

        //Code for Barcode scanning.
        if (action.equals("startBarcode")) {
            String type = args.getString(1);
            String appOrientation = args.getString(2);
            Intent myIntent = new Intent(cordova.getActivity(), OcrActivity.class);
            if (ACCURAService.messagesConf != null) {
              myIntent = addDefaultConfigs(myIntent, ACCURAService.messagesConf);
            }
            myIntent = addDefaultConfigs(myIntent, accuraConf);
            myIntent.putExtra("type", "barcode");
            myIntent.putExtra("sub-type", type);
            myIntent.putExtra("app_orientation", appOrientation);
            ocrCL = callbackContext;
            cordova.getActivity().startActivity(myIntent);
            return true;
        }

        //Code for check face match.
        if (action.equals("startFaceMatch")) {
            JSONObject config = args.getJSONObject(1);
            String appOrientation = args.getString(2);
            faceCL = callbackContext;
            Intent intent = new Intent(cordova.getActivity(), FaceMatchActivity.class);
            intent = addDefaultConfigs(intent, accuraConf);
            intent = addDefaultConfigs(intent, config);
            intent.putExtra("app_orientation", appOrientation);
            intent.putExtra("type", "fm");
            cordova.getActivity().startActivity(intent);
            return true;
        }

        //Code for check liveness.
        if (action.equals("startLiveness")) {
            faceCL = callbackContext;
            Intent intent = new Intent(cordova.getActivity(), FaceMatchActivity.class);
            JSONObject config = args.getJSONObject(1);
            String appOrientation = args.getString(2);
            intent = addDefaultConfigs(intent, accuraConf);
            intent = addDefaultConfigs(intent, config);
            intent.putExtra("app_orientation", appOrientation);
            intent.putExtra("type", "lv");
            cordova.getActivity().startActivity(intent);
            return true;
        }
        return false;
    }



    public Intent addDefaultConfigs(Intent intent, JSONObject config) {
        Iterator<String> iter = config.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                if (config.get(key) instanceof String) {
                    intent.putExtra(key, config.getString(key));
                }
                if (config.get(key) instanceof Boolean) {
                    intent.putExtra(key, config.getBoolean(key));
                }
                if (config.get(key) instanceof Integer) {
                    intent.putExtra(key, config.getInt(key));
                }
                if (config.get(key) instanceof Double) {
                    intent.putExtra(key, config.getDouble(key));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return intent;
    }
}