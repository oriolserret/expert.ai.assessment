# Assesment
Skeleton code for an assesment using expert.ai API

The first step is for this exercise is go to the following link [expert.ai](https://developer.expert.ai/ui/login) and create a user and a password to get access to the API.

Once you created the user, you can check the following link [expert.ai API documentation](https://docs.expert.ai/nlapi/v1/) to be familiar with the API.

Inside the lib folder of the project sources, you can find a jar file (nlapi-java-sdk-1.0.0-RC.1.jar) containing the Java Client for the [expert.ai](https://developer.expert.ai/) Natural Language API. Leverage Natural Language technology from your Java apps.

## Launcher.java

This class contains the code to launch the java program to test the API.

## ApiDemo.java

This class contains the initialization of the properties, the token aquisition and a principal function to use the 5 different calls present in the JDK.

##### public void startWithWrapper() {**//TODO**}
  1. Categorization
  2. Disambiguation Analisys
  3. Relevants Analisys
  4. Entities Analisys
  5. Full Analisys
  
## UTILS Folder

This folder contain different classes as to be used as utility for the project
  
  - **FileCopier.java** -> This class is used to copy files from a folder to another
  - **FileReader.java** -> This class is used to read n files from a folder  
  - **Globals.java** -> Class containing the global values used in the application
  - **PropertiesUtils.java** -> Class used to iteract with the properties file

## USEFULL INFORMATION TO USE THE JAVA CLIENT

### Document Analisys

You can get the result of the deep linguistic analysis applied to your text as follows

```java

import ai.expert.nlapi.security.Authentication;
import ai.expert.nlapi.security.Authenticator;
import ai.expert.nlapi.security.BasicAuthenticator;
import ai.expert.nlapi.security.Credential;
import ai.expert.nlapi.v1.API;
import ai.expert.nlapi.v1.Analyzer;
import ai.expert.nlapi.v1.AnalyzerConfig;

public class AnalisysTest {

    static StringBuilder sb = new StringBuilder();

    // Sample text to be analyzed
    static {
        sb.append("Michael Jordan was one of the best basketball players of all time.");
        sb.append("Scoring was Jordan's stand-out skill, but he still holds a defensive NBA record, with eight steals in a half.");  
    }

    public static String getSampleText() {
        return sb.toString();
    }
    
    //Method for setting the authentication credentials - set your credentials here.
    public static Authentication createAuthentication() throws Exception {
        Authenticator authenticator = new BasicAuthenticator(new Credential("PUT HERE YOUR USERNAME", " PUT HERE YOUR PASSWORD"));
        return new Authentication(authenticator);
    }

    //Method for selecting the resource to be call by the API; as today, the API provides the standard context only, and  
    //five languages such as English, French, Spanish, German and Italian
    public static Analyzer createAnalyzer() throws Exception {
        return new Analyzer(AnalyzerConfig.builder()
                                          .withVersion(API.Versions.V1)
                                          .withContext(API.Contexts.STANDARD)
                                          .withLanguage(API.Languages.en)
                                          .withAuthentication(createAuthentication())
                                          .build());
    }

    public static void main(String[] args) {
        try {
            Analyzer analyzer = createAnalyzer();
            ResponseDocument response = null;
            
            // Disambiguation Analisys
            response = analyzer.disambiguation(getSampleText());
            response.prettyPrint();

            // Relevants Analisys
            response = analyzer.relevants(getSampleText());
            response.prettyPrint();

            // Entities Analisys
            response = analyzer.entities(getSampleText());
            response.prettyPrint();
            
            // Full Analisys
            response = analyzer.analyze(getSampleText());
            response.prettyPrint();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}

```


### Document Classification

or to run a document classification with respect to the [IPTC Media Topic taxonomy](https://iptc.org/standards/media-topics/)

```java

package ai.expert.nlapi.v1.test;

import ai.expert.nlapi.security.Authentication;
import ai.expert.nlapi.security.Authenticator;
import ai.expert.nlapi.security.BasicAuthenticator;
import ai.expert.nlapi.security.Credential;
import ai.expert.nlapi.v1.API;
import ai.expert.nlapi.v1.Categorizer;
import ai.expert.nlapi.v1.CategorizerConfig;
import ai.expert.nlapi.v1.message.ResponseDocument;

public class CategorizationTest {

    static StringBuilder sb = new StringBuilder();
    
    // Sample text to be analyzed
    static {
        sb.append("Michael Jordan was one of the best basketball players of all time.");
        sb.append("Scoring was Jordan's stand-out skill, but he still holds a defensive NBA record, with eight steals in a half.");  
    }

    public static String getSampleText() {
        return sb.toString();
    }

    //Method for setting the authentication credentials - set your credentials here.
    public static Authentication createAuthentication() throws Exception {
        Authenticator authenticator = new BasicAuthenticator(new Credential("PUT HERE YOUR USERNAME", " PUT HERE YOUR PASSWORD"));
        return new Authentication(authenticator);
    }
    
    //Method for selecting the resource to be call by the API; as today, the API provides the IPTC classifier only, and 
    //five languages such as English, French, Spanish, German and Italian
    public static Categorizer createCategorizer() throws Exception {
        return new Categorizer(CategorizerConfig.builder()
                                                .withVersion(API.Versions.V1)
                                                .withTaxonomy(API.Taxonomies.IPTC)
                                                .withLanguage(API.Languages.en)
                                                .withAuthentication(createAuthentication())
                                                .build());
    }

    public static void main(String[] args) {
        try {
            Categorizer categorizer = createCategorizer();
            
            //Perform the IPTC classification and store it into a Response Object
            ResponseDocument categorization = categorizer.categorize(getSampleText());
            categorization.prettyPrint();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}

```
