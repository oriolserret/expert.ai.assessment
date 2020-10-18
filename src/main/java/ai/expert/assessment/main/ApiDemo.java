package ai.expert.assessment.main;

import ai.expert.assessment.utils.FileReader;
import ai.expert.assessment.utils.Globals;
import ai.expert.assessment.utils.PropertiesUtils;
import ai.expert.nlapi.security.Authentication;
import ai.expert.nlapi.security.Authenticator;
import ai.expert.nlapi.security.BasicAuthenticator;
import ai.expert.nlapi.security.Credential;
import ai.expert.nlapi.v1.*;
import ai.expert.nlapi.v1.message.ResponseDocument;
import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


/**
 * @author oserret
 */
public class ApiDemo {

    private static final Logger API_TESTER_LOGGER = Logger.getLogger(ApiDemo.class);

    private String strLogFileConf;
    private String strPropertiesFile;

    private String expertAI_userToken = Globals.VOID_STRING;
    private String expertAI_Url = Globals.VOID_STRING;
    private String expertAI_AuthUrl = Globals.VOID_STRING;
    private String outputFolder = Globals.VOID_STRING;
    private String inputFolder = Globals.VOID_STRING;
    private String expertAI_user = Globals.VOID_STRING;
    private String expertAI_password = Globals.VOID_STRING;

    private final OkHttpClient httpClient;

    public ApiDemo(String strLogFileConfPar, String strPropertiesFilePar) {
        this.httpClient = new OkHttpClient.Builder().readTimeout(Long.parseLong("5000"), TimeUnit.SECONDS).build();
        this.strLogFileConf = strLogFileConfPar;
        this.strPropertiesFile = strPropertiesFilePar;
    }

    public boolean init() {
        if (strPropertiesFile != null) {
            API_TESTER_LOGGER.info("\tBEGIN PROPERTIES FILE LOAD: " + strPropertiesFile);

            setExpertAI_Url(PropertiesUtils.getProperty("expertai.url"));
            API_TESTER_LOGGER.info("\t\texpertai.url: " + getExpertAI_Url());

            setExpertAI_AuthUrl(PropertiesUtils.getProperty("expertai.auth.url"));
            API_TESTER_LOGGER.info("\t\texpertai.auth.url: " + getExpertAI_AuthUrl());

            setInputFolder(PropertiesUtils.getProperty("expert.inputFolder"));
            API_TESTER_LOGGER.info("\t\tinputFolder: " + getInputFolder());

            setOutputFolder(PropertiesUtils.getProperty("expert.outputFolder"));
            API_TESTER_LOGGER.info("\t\toutputFolder: " + getOutputFolder());

            setExpertAI_user(PropertiesUtils.getProperty("expertai.username"));
            API_TESTER_LOGGER.info("\t\texpertAI_user: " + getExpertAI_user());

            setExpertAI_password(PropertiesUtils.getProperty("expertai.password"));
            API_TESTER_LOGGER.info("\t\texpertAI_password: " + getExpertAI_password());

            API_TESTER_LOGGER.info("\tEND PROPERTIES FILE LOAD");
            API_TESTER_LOGGER.info("\t***************");
            API_TESTER_LOGGER.info("\tINIT STATUS: OK");
            API_TESTER_LOGGER.info("\t***************");
            return Boolean.TRUE;

        } else {
            API_TESTER_LOGGER.info("\tProperties file not found: " + strPropertiesFile);
            API_TESTER_LOGGER.info("\t***************");
            API_TESTER_LOGGER.info("\tINIT STATUS: KO");
            API_TESTER_LOGGER.info("\t***************");
            return Boolean.FALSE;
        }
    }

    public boolean getToken() {
        boolean tokenOk = Boolean.FALSE;
        Response response;
        RequestBody body;

        JSONObject json = new JSONObject();
        json.put("username", getExpertAI_user());
        json.put("password", getExpertAI_password());

        body = RequestBody.create(Globals.CONTENT, json.toString());

        Request request = new Request.Builder().url(getExpertAI_AuthUrl()).post(body).build();

        try {
            API_TESTER_LOGGER.info("\t\tCalling WS to retrieve the TOKEN");
            API_TESTER_LOGGER.info("\t\tWS-host: " + getExpertAI_AuthUrl());
            response = httpClient.newCall(request).execute();
            API_TESTER_LOGGER.info("\t\tCall executed");
            if (!response.isSuccessful()) {
                API_TESTER_LOGGER.error("\t\tError calling WS for retrieve the TOKEN");
                API_TESTER_LOGGER.info("\t\tTOKEN NOT RETRIEVED");
            } else {
                setExpertAI_userToken(response.body().string());
                response.close();
                API_TESTER_LOGGER.info("\t\tTOKEN RETRIEVED");
                tokenOk = Boolean.TRUE;
            }
        } catch (IOException ex) {
            API_TESTER_LOGGER.error("\t\tError calling WS for retrieve the TOKEN");
            API_TESTER_LOGGER.error(ex.getMessage());
            API_TESTER_LOGGER.info("\t\tTOKEN NOT RETRIEVED");
        }

        return tokenOk;
    }


    public boolean saveAnalyzedFile(JSONObject outputFile, String fileName) {
        boolean savedOk = Boolean.FALSE;
        try {
            Files.write(Paths.get(getOutputFolder() + fileName), Collections.singleton(outputFile.toString(3)));
            savedOk = Boolean.TRUE;
        } catch (JsonProcessingException e) {
            API_TESTER_LOGGER.error(e.getMessage());
        } catch (IOException ex) {
            API_TESTER_LOGGER.error(ex.getMessage());
        }

        return savedOk;
    }

    //Method for setting the authentication credentials - set your credentials here.
    public Authentication createAuthentication() throws Exception {
        Authenticator authenticator = new BasicAuthenticator(new Credential(getExpertAI_user(), getExpertAI_password()));
        return new Authentication(authenticator);
    }

    //Method for selecting the resource to be call by the API; as today, the API provides the IPTC classifier only, and
    //five languages such as English, French, Spanish, German and Italian
    public Categorizer createCategorizer() throws Exception {
        return new Categorizer(CategorizerConfig.builder()
                .withVersion(API.Versions.V1)
                .withTaxonomy(API.Taxonomies.IPTC)
                .withLanguage(API.Languages.en)
                .withAuthentication(createAuthentication())
                .build());
    }

    //Method for selecting the resource to be call by the API; as today, the API provides the standard context only, and
    //five languages such as English, French, Spanish, German and Italian
    public Analyzer createAnalyzer() throws Exception {
        return new Analyzer(AnalyzerConfig.builder()
                .withVersion(API.Versions.V1)
                .withContext(API.Contexts.STANDARD)
                .withLanguage(API.Languages.en)
                .withAuthentication(createAuthentication())
                .build());
    }

    public void startWithWrapper() {

        API_TESTER_LOGGER.info("");
        API_TESTER_LOGGER.info("\t-- 2.1 --");
        API_TESTER_LOGGER.info("\tBEGIN FILE READING PROCESS");
        FileReader reader = new FileReader();
        reader.readFiles(getInputFolder());
        API_TESTER_LOGGER.info("\tEND FILE READING PROCESS");
        API_TESTER_LOGGER.info("\tTOTAL PROCESSING FILES: " + reader.getFiles().size());

        try {

            Categorizer categorizer = createCategorizer();
            ResponseDocument categorization = null;

            API_TESTER_LOGGER.info("");
            API_TESTER_LOGGER.info("\t-- 2.2 --");
            API_TESTER_LOGGER.info("\tBEGIN FILE ANALYSIS (CATEGORIZATION)");
            for (File file : reader.getFiles()) {
                String jsonTxt = Globals.VOID_STRING;
                JSONObject jsonObj;

                try {

                    if (file.getName().contains(".json")) {
                        jsonTxt = new String(Files.readAllBytes(Paths.get(file.getPath())));
                        jsonObj = new JSONObject(jsonTxt);
                        jsonObj = jsonObj.getJSONObject("document");

                        //Perform the IPTC classification and store it into a Response Object
                        categorization = categorizer.categorize(jsonObj.getString("text"));
                    } else {
                        jsonTxt = new String(Files.readAllBytes(Paths.get(file.getPath())));

                        if(jsonTxt.length() > 10000){
                            jsonTxt = jsonTxt.substring(0,10000);
                        }
                        //Perform the IPTC classification and store it into a Response Object
                        categorization = categorizer.categorize(jsonTxt);
                    }
                    API_TESTER_LOGGER.info("\t\tSaving categorization: " + file.getName() + Globals.ANALYSIS_TYPE_CAT + Globals.OUTPUT_EXTENSION);
                    saveAnalyzedFile(new JSONObject(categorization.toJSON()), file.getName() + Globals.ANALYSIS_TYPE_CAT + Globals.OUTPUT_EXTENSION);

                } catch (IOException ex) {
                    API_TESTER_LOGGER.error(ex.getMessage());
                } catch (NullPointerException exNull){
                    API_TESTER_LOGGER.error(exNull.getMessage());
                }
            }
            API_TESTER_LOGGER.info("\tEND FILE ANALYSIS (CATEGORIZATION)");

            try {

                //Perform the IPTC classification and store it into a Response Object
                Analyzer analyzer = createAnalyzer();
                ResponseDocument extraction = null;

                API_TESTER_LOGGER.info("");
                API_TESTER_LOGGER.info("\t-- 2.3 --");
                API_TESTER_LOGGER.info("\tBEGIN FILE ANALYSIS (EXTRACTION)");
                for (File file : reader.getFiles()) {
                    String jsonTxt = Globals.VOID_STRING;
                    JSONObject jsonObj;

                    try {

                        if (file.getName().contains(".json")) {

                            jsonTxt = new String(Files.readAllBytes(Paths.get(file.getPath())));
                            jsonObj = new JSONObject(jsonTxt);
                            jsonObj = jsonObj.getJSONObject("document");
                            // Disambiguation Analisys
                            extraction = analyzer.disambiguation(jsonObj.getString("text"));
                            // Relevants Analisys
                            extraction = analyzer.relevants(jsonObj.getString("text"));
                            // Entities Analisys
                            extraction = analyzer.entities(jsonObj.getString("text"));
                            // Full Analisys
                            extraction = analyzer.analyze(jsonObj.getString("text"));

                        } else {
                            jsonTxt = new String(Files.readAllBytes(Paths.get(file.getPath())));

                            if(jsonTxt.length() > 10000){
                                jsonTxt = jsonTxt.substring(0,10000);
                            }

                            // Disambiguation Analisys
                            extraction = analyzer.disambiguation(jsonTxt);
                            // Relevants Analisys
                            extraction = analyzer.relevants(jsonTxt);
                            // Entities Analisys
                            extraction = analyzer.entities(jsonTxt);
                            // Full Analisys
                            extraction = analyzer.analyze(jsonTxt);
                        }
                        API_TESTER_LOGGER.info("\t\tSaving extraction: " + file.getName() + Globals.ANALYSIS_TYPE_EXT_DIS + Globals.OUTPUT_EXTENSION);
                        saveAnalyzedFile(new JSONObject(extraction.toJSON()), file.getName() + Globals.ANALYSIS_TYPE_EXT_DIS + Globals.OUTPUT_EXTENSION);
                        API_TESTER_LOGGER.info("\t\tSaving extraction: " + file.getName() + Globals.ANALYSIS_TYPE_EXT_REL + Globals.OUTPUT_EXTENSION);
                        saveAnalyzedFile(new JSONObject(extraction.toJSON()), file.getName() + Globals.ANALYSIS_TYPE_EXT_REL + Globals.OUTPUT_EXTENSION);
                        API_TESTER_LOGGER.info("\t\tSaving extraction: " + file.getName() + Globals.ANALYSIS_TYPE_EXT_ENT + Globals.OUTPUT_EXTENSION);
                        saveAnalyzedFile(new JSONObject(extraction.toJSON()), file.getName() + Globals.ANALYSIS_TYPE_EXT_ENT + Globals.OUTPUT_EXTENSION);
                        API_TESTER_LOGGER.info("\t\tSaving extraction: " + file.getName() + Globals.ANALYSIS_TYPE_EXT_FULL + Globals.OUTPUT_EXTENSION);
                        saveAnalyzedFile(new JSONObject(extraction.toJSON()), file.getName() + Globals.ANALYSIS_TYPE_EXT_FULL + Globals.OUTPUT_EXTENSION);

                    } catch (IOException ex) {
                        API_TESTER_LOGGER.error(ex.getMessage());
                    } catch (NullPointerException exNull){
                        API_TESTER_LOGGER.error(exNull.getMessage());
                    }
                }
                API_TESTER_LOGGER.info("\tEND FILE ANALYSIS (EXTRACTION)");


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * @return the expertAI_token
     */
    public String getExpertAI_userToken() {
        return expertAI_userToken;
    }

    /**
     * @param expertAI_userToken the expertAI_token to set
     */
    public void setExpertAI_userToken(String expertAI_userToken) {
        this.expertAI_userToken = expertAI_userToken;
    }

    /**
     * @return the expertAI_Url
     */
    public String getExpertAI_Url() {
        return expertAI_Url;
    }

    /**
     * @param expertAI_Url the expertAI_Url to set
     */
    public void setExpertAI_Url(String expertAI_Url) {
        this.expertAI_Url = expertAI_Url;
    }

    /**
     * @return the outputFolder
     */
    public String getOutputFolder() {
        return outputFolder;
    }

    /**
     * @param outputFolder the outputFolder to set
     */
    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    /**
     * @return the strLogFileConf
     */
    public String getStrLogFileConf() {
        return strLogFileConf;
    }

    /**
     * @param strLogFileConf the strLogFileConf to set
     */
    public void setStrLogFileConf(String strLogFileConf) {
        this.strLogFileConf = strLogFileConf;
    }

    /**
     * @return the strPropertiesFile
     */
    public String getStrPropertiesFile() {
        return strPropertiesFile;
    }

    /**
     * @param strPropertiesFile the strPropertiesFile to set
     */
    public void setStrPropertiesFile(String strPropertiesFile) {
        this.strPropertiesFile = strPropertiesFile;
    }

    /**
     * @return the inputFolder
     */
    public String getInputFolder() {
        return inputFolder;
    }

    /**
     * @param inputFolder the inputFolder to set
     */
    public void setInputFolder(String inputFolder) {
        this.inputFolder = inputFolder;
    }

    /**
     * @return the user
     */
    public String getExpertAI_user() {
        return expertAI_user;
    }

    /**
     * @param user the user to set
     */
    public void setExpertAI_user(String user) {
        this.expertAI_user = user;
    }

    /**
     * @return the password
     */
    public String getExpertAI_password() {
        return expertAI_password;
    }

    /**
     * @param password the password to set
     */
    public void setExpertAI_password(String password) {
        this.expertAI_password = password;
    }

    /**
     * @return the expertAI__AuthUrl
     */
    public String getExpertAI_AuthUrl() {
        return expertAI_AuthUrl;
    }

    /**
     * @param expertAI_AuthUrl the expertAI__AuthUrl to set
     */
    public void setExpertAI_AuthUrl(String expertAI_AuthUrl) {
        this.expertAI_AuthUrl = expertAI_AuthUrl;
    }

}
