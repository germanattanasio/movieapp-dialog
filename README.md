# What's In Theaters Application Overview
The What's in Theaters application showcases the best practices to follow to build a Watson conversational solution by using the Watson Dialog service. The application allows users to converse with Watson to search for current or upcoming movies by using [themoviedb.org](https://www.themoviedb.org/) database.

The application uses a conversational template that you can customize for your own application. This domain-independent template will help you to structure your dialogs according to how people naturally converse, thus helping you to bootstrap your future dialogs.

This application is provided in two configurations:
* **Configuration 1 (Dialog service)**: This version uses the Watson Dialog service to chat with users. Here's a [quick demo](http://watson-movieapp-dialog.mybluemix.net/watson-movieapp-dialog/dist/index.html#/).
* **Configuration 2 (Dialog service and Watson Natural Language Classifier service)**: This version uses the Dialog service to chat with users and the [Classifier service](http://www.ibm.com/smarterplanet/us/en/ibmwatson/developercloud/nl-classifier.html?cm_mc_uid=80306055592314351838101&cm_mc_sid_50200000=1445221614) to help identify user intent. Here's a [demo](http://watson-movieapp-nlcdialog.mybluemix.net/watson-movieapp-dialog/dist/index.html#/).

## How the app works
Users talk in natural language to the system to find movies that match the search criteria they've specified. The system is built to understand natural language that relates to searching for and selecting movies to watch. For example, saying "I'd like to see a recent R rated drama" causes the system to search the movie repository and to return the names of all R-rated dramas that have been released in the last 30 days.

The system is designed to obtain the following types of information about movies from users before it searches the repository:

  * **Recency**: The system determines whether users want to know about currently playing movies or upcoming movies.
  * **Genre**: The system understands movie genres, such as action, comedy, and horror.
  * **Rating**: The system understands movie rating, such as G, PG-13, and R.

Users can search across all genres and ratings simply by answering "no" to the corresponding questions. Before the system searches the movie repository, it needs to know whether a user prefers current movies or upcoming movies. The system understands variations of text, so users can rephrase their responses, and the system will still process it. For example, the system might ask, "Do you want to watch an upcoming movie or one that's playing tonight?" Users can say "tonight" or "Show me movies playing currently," and the system understands that both answers mean that users want to know about current movies.

## Using the Dialog service and the Classifier service

The dialog service uses expert rules to match the user utterance to an intent thus typically yielding high accuracy. The classifier service on the other hand is a statistical system which gives a high recall. So the combination of dialog and classifier, gives a high precision, high accuracy system.

The Dialog service only uses the classifier intent when control goes to the default response in the dialog. For a given input sentence, the trained classifier responds with a list of intent classes and confidence scores for each class. Dialog only uses the top two classes to decide how to respond to the user. The following checks are performed by the Dialog service:

1. The USER_INTENT from the Classifier service is considered valid when class(0).confidence >= upper_confidence_threshold.
2. Ask user to confirm the USER_INTENT when upper_confidence_threshold >= class(0).confidence > lower_confidence_threshold.
3. Ask user to disambiguate between USER_INTENT(0) and USER_INTENT(1) when class(0).confidence + class(1).confidence > upper_confidence_threshold.
4. Reply with the default response when none of the previous checks are true.

where, class(0) is the top class and class(0).confidence is the respective confidence score. Similarly, class(1) is the second best class and class(1).confidence is the respective confidence score.
In these checks, **upper_confidence_threshold** and **lower_confidence_threshold** are floats 0 - 1, and their values are obtained by running cross-validation tests with the classifier on a given data set.

The What's In Theaters app is designed to allow developers to switch between the two configurations simply by adding the Classifier service and modifying the dialog to incorporate feedback from the classifier. The setup process for each configuration is explained in the following tasks.
## Before you begin
Ensure that you have the following prerequisites before you start:
* An IBM Bluemix account. If you don't have one, sign up for it [here](https://apps.admin.ibmcloud.com/manage/trial/bluemix.html?cm_mmc=WatsonDeveloperCloud-_-LandingSiteGetStarted-_-x-_-CreateAnAccountOnBluemixCLI). For more information about the process, see [Developing Watson applications with Bluemix](http://www.ibm.com/smarterplanet/us/en/ibmwatson/developercloud/doc/getting_started/gs-bluemix.shtml).
* [Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html) 1.7 or later releases
* [Eclipse IDE for Java EE Developers](https://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/marsr)
* [Apache Maven](https://maven.apache.org/download.cgi) 3.1 or later releases
* [Git](https://git-scm.com/downloads)
* [Websphere Liberty Profile server](https://developer.ibm.com/wasdev/downloads/liberty-profile-using-non-eclipse-environments/), if you want to run the app in your local environment
* Request an API key from [themoviedb.org](https://www.themoviedb.org/documentation/api): A user account is required before you request an API key from themoviedb.org. This API key will be used later to make requests to the movie database service.

## Setup
In order to run the What's In Theaters app, you need to have a Dialog service instance bound to an app in your IBM Bluemix account. The following steps will guide you through the process. The instructions use Eclipse, but you can use the IDE of your choice. If you want to setup Configuration 1, add only the Dialog service. To set up Configuration 2, add the Dialog and Classifier services, and switch between the two configurations by changing some environment variables.

### Get the project from GitHub
1. Clone the movieapp-dialog repository from GitHub by issuing one of the following commands in your terminal:
   ```
   git clone https://github.com/watson-developer-cloud/movieapp-dialog.git
   ```
   ```
   git clone git@github.com:watson-developer-cloud/movieapp-dialog.git
   ```

2. Add the newly cloned repository to your local Eclipse workspace.

### Set up the Bluemix Environment
#### Create an App
1. [Log in to Bluemix](https://console.ng.bluemix.net/) and navigate to the *Dashboard* on the top panel.
2. Create your app.
      1. Click **CREATE AN APP**.
      2. Select **WEB**.
      3. Select the starter **Liberty for Java**, and click **CONTINUE**.
      4. Type a unique name for your app, such as `dialog-sample-app`, and click **Finish**.
      5. Select **CF Command Line Interface**. If you do not already have it, click **Download CF Command Line Interface**. This link opens a GitHub repository. Download and install it locally.

#### Add Watson services
Complete one of the following sets of steps to add an instance of the Watson Dialog service. Bluemix allows you to create a new service instance to bind to your app or to bind to an existing instance. Choose one of the following ways:  

**Adding new services to bind to your app**
  1. [Log in to Bluemix](https://console.ng.bluemix.net/) and navigate to the *Dashboard* on the top panel. Find the app that you created in the previous section, and click it.
  2. Click **ADD A SERVICE OR API**.
  3. Select the **Watson** category, and select the **Dialog** service.
  4. Ensure that your app is specified in the **App** dropdown on the right-hand side of the pop-up window under **Add Service**.
  5. Type a unique name for your service in the **Service name** field, such as `dialog-sample-service`.
  6. Click **CREATE**. The **Restage Application** window is displayed.
  7. Click **RESTAGE** to restart your app. If the app is not started, click **START**.
  8. To add the Classifier service, click **Overview**, and repeat steps 2 - 7. In step 3, select the **Natural Language Classifier** service. In step 5, specify a unique name for the Classifier service.

**Binding to an existing service instance**
  1. [Log in to Bluemix](https://console.ng.bluemix.net/) and navigate to the *Dashboard* on the top panel. Locate and click on the app you created in the previous section.
  2. Click **BIND A SERVICE OR API**.
  3. Select the instance of the service that you want to bind to your app, and click **ADD**. The **Restage Application** window is displayed.
  4. Click **RESTAGE** to restart your app.  

#### Uploading a dialog file
After you bind an instance of the Dialog service to the app, you can use the credentials you received in the previous step to upload a dialog file. The dialog files for this application are in the project at `/movieapp-dialog/src/main/resources/dialog_files`. For Configuration 1, use `movieapp-dialog-file.xml`. For Configuration 2, use `movieapp-dialog+classifier-file.xml`.

Use the following command to upload this file to Bluemix:
```
curl -X POST -F "file=@*dialogFile*" -F "name=*dialogName*" https://gateway.watsonplatform.net/dialog-beta/api/v1/dialogs -u "*username*:*password*"
```
where, *dialogFile* is the file name of the XML dialog file to upload, *dialogName* is a unique name you give to the dialog to upload, and the *username** and **password* are the credentials you obtained in the previous step. 

After the file is uploaded, a message similar to the following message is displayed:
```
{"dialog_id": "4ff28b26-63ef-492f-902d-86a467f50040"}
```
**Important**: Include the @ symbol before the *dialogFile* variable.

#### Training a classifier
After you bind an instance of the Classifier service to the app, you must train a classifier by using this service. You can use the sample training set packaged with the project at `/movieapp-dialog/src/main/resources/classifier_files`. You can upload the `train.csv` file by using the curl command that is specified in the [Classifier service documentation](http://www.ibm.com/smarterplanet/us/en/ibmwatson/developercloud/apis/#!/natural-language-classifier/) and the Classifier service credentials you obtained in the previous step. The classifier is immediately in Training status. After the status changes to Available, the classifier is ready for use.

#### Setting up environment variables in Bluemix
To run the What's in Theaters application on Bluemix, more environment variables are required:  
  1. **DIALOG_ID**: The dialog ID you obtained when you uploaded your dialog file on Bluemix in the previous section **Uploading a dialog file**. 
  2. **TMDB_API_KEY**: The API key obtained from themoviedb.org.
  3. **CLASSIFIER_ID**: The classifier ID you obtained when you uploaded the training data on Bluemix in the previous section **Training a lassifier**.

Navigate to the application dashboard in Bluemix. Locate and click your app. Navigate to the **Environment Variables** section. Switch to the **USER-DEFINED** tab. Add the new environment variables as specified above.

To view the home page of the app, open [https://*yourAppName*.mybluemix.net](https://yourAppName.mybluemix.net), where *yourAppName* is the name of your app.

### Building the app
This project is configured to be built with Maven. To deploy the app, complete the following steps in order:
  1. In your Eclipse window, expand the *movieapp-dialog* project that you cloned from GitHub.
  2. Right-click the project and select `Maven -> Update Project` from the context menu to update Maven dependencies.
  3. Keep the default options, and click **OK**.
  4. Navigate to the location of your default deployment server. For Websphere Liberty, it might resemble the following path: `../LibertyRuntime/usr/servers/*server-name*`. Open the `server.env` file or create it, if it does not exist, and update the following entries:
    * **VCAP_SERVICES**: This entry should contain a JSON object obtained from the *Environment Variables* section of your application on Bluemix. When entering the JSON in the server.env file make sure it is formatted to be in one line.
    * **DIALOG_ID**: Specify the ID that corresponds to your Dialog service account on Bluemix. The dialog ID is an alphanumeric string.
    * **TMDB_API_KEY**: Specify the API key you received after you registered for API access on themoviedb.org.
    * **CLASSIFIER_ID**: Specify the ID that corresponds to your classifier on Bluemix. The Classifier ID is an alphanumeric string with hyphens).
**Important**: The DIALOG_ID, TMDB_API_KEY, and CLASSIFIER_ID are not JSON structures. The contents of the `server.env` file resemble the following example:
    ```
    VCAP_SERVICES={"dialog": [ { "name": "dialog-service", "label": "dialog", "plan": "standard", "credentials": { "url": "https://sampleplatformurl.net/samplePath", "username": "username","password": "password"  }  } ], "natural_language_classifier": [ { "name": "classifier-service", "label": "natural_language_classifier", "plan": "standard", "credentials": { "url": "https://sampleplatformurl.net/samplePath", "username": "username","password": "password"  }  } ]}
    DIALOG_ID=dialog_id
    TMDB_API_KEY=tmdb_api_key
    CLASSIFIER_ID=classifier_id
    ```
**Important**: To use Configuration 1, change the contents of the `server.env` file to reflect only the Dialog service with the correct dialog_id. The contents resemble the following example:
    ```
    VCAP_SERVICES={"dialog": [ { "name": "dialog-service", "label": "dialog", "plan": "standard", "credentials": { "url": "https://sampleplatformurl.net/samplePath", "username": "username","password": "password"  }  } ]}
    DIALOG_ID=dialog_id
    TMDB_API_KEY=tmdb_api_key
    ```
  5. Switch to the navigator view in Eclipse, right-click the `pom.xml`, and select `Run As -> Maven Install`. Installation of Maven begins. During the installation, the following tasks are done:
    * The JS code is compiled. That is, the various Angular JS files are aggregated, uglified, and compressed. Various other preprocessing is performed on the web code, and the output is copied to the `movieapp-dialog/src/main/webapp/dist` folder in the project.
    * The Java code is compiled, and JUnit tests are executed against the Java code. The compiled Java and JavaScript code and various other artifacts that are required by the web project are copied to a temporary location, and a `.war` file is created.
    * The Maven installation instantiates a new Websphere Liberty Profile server, deploys the `.war` file to the server, starts the server, and runs a battery of integration tests against the deployed web application.

This WAR file that resides in `/movieapp-dialog/target directory` is used to deploy the application on Bluemix in the next section.

### Deploying the app
You can run the application on a local server or on Bluemix. Choose one of the following methods, and complete the steps:
#### Deploying the app on your local server in Eclipse
1. Start Eclipse, and click `Window -> Show View -> Servers`.
2. In the **Servers** view, right-click and select `New -> Server`. The *Define a New Server* window is displayed.
3. Select the **WebSphere Application Server Liberty Profile**, and click **Next**.  
4. Configure the server with the default settings.  
5. In the **Available** list in the **Add and Remove** dialog, select the *movieapp-dialog* project, and click **Add >**. The project is added to the runtime configuration for the server in the **Configured** list.
6. Click **Finish**.
7. Copy the *server.env* file which was edited previously from *movieapp-dialog/src/it/resources/server.env* to the root folder of the newly defined server (i.e. *wlp/usr/defaultserver/server.env*).  
8. Start the new server, and open [http://localhost:serverPort/watson-movieapp-dialog/dist/index.html#/](http://localhost:serverPort/watson-movieapp-dialog/dist/index.html#/) in your favorite browser, where *serverPort* is the port on which your local server is running.
9. Chat with What's in Theaters!

#### Deploying the app on the Websphere Liberty Profile in Bluemix
Deploy the WAR file that you built in the previous section by using Cloud Foundry commands.
1. Open the command prompt.
2. Navigate to the directory that contains the WAR file you that you generated by running the following command in the terminal:
   ```
   cd movieapp-dialog/target
   ```

3. Connect to Bluemix by running the following command:
   ```
   cf api https://api.ng.bluemix.net
   ```

4. Log in to Bluemix by running the following command,
   ```
   cf login -u <yourUsername> -o <yourOrg> -s <yourSpace>
   ```
where *yourUsername* is your Bluemix ID, *yourOrg* is your organization name in Bluemix, and *yourSpace* is your space name in Bluemix.
5. Deploy the app to Bluemix by running the following command.
   ```
   cf push <yourAppName> -p watson-movieapp-dialog.war
   ```
where, *yourAppName* is the name of your app.
6. Navigate to [Bluemix](https://console.ng.bluemix.net/) to make sure the app is started. If it is not started, click **START**.
7. To view the home page of the app, open [https://yourAppName.mybluemix.net/watson-movieapp-dialog/dist/index.html#/](https://yourAppName.mybluemix.net/watson-movieapp-dialog/dist/index.html#/), where *yourAppName* is the specific name of your app.
8. Chat with What's in Theaters!

## Automation
Automation is designed both to run as a full suite of regression tests and to run a subset of regression tests for continuous integration process.  Updates to dialog will need reciprocating changes to the JSON files (see below).

### Setting up for full regression
Use`src/it/resources/testConfig.properties` to control the server under test, browser type 
(Firefox and Chrome are supported), and the Selenium Grid server to include, if you have one in your environment.

### Running the full regression suite
To run the full regression suite you can execute GUI_TestSuite and Rest_TestSuite Junit Test suites directly in your Eclipse environment. 

### Managing changes to the dialog
The dialog questions and answers from the .xml file (see **Uploading a dialog file** above) map directly to question answer arrays in JSON files that are used by the automation. These files are located in the `src/it/resources/questions` directory. When you change dialog, you will need to do reciprocating changes to the JSON configuration files to allow the regression test suite to pass.

## Reference information
* [Dialog service documentation](https://dialog-doc-la.mybluemix.net/doc/dialog/index.html): Get an in-depth knowledge of the Dialog service
* [Dialog service API documentation](https://dialog-doc-la.mybluemix.net/apis/): Understand API usage
* [Natural Language Classifier](http://www.ibm.com/smarterplanet/us/en/ibmwatson/developercloud/doc/nl-classifier/)
* [Natural Language Classifier API documentation](http://www.ibm.com/smarterplanet/us/en/ibmwatson/developercloud/natural-language-classifier/api/v1/)
* [Dialog service tutorial](https://dialog-doc-la.mybluemix.net/doc/dialog/index.html#tutorial_intro): Design your own dialog by using a tutorial, which includes a generic template that you can modify for your own use. The Conversation Analysis Template comes bundled with this project and can be found at */movieapp-dialog/src/main/resources/dialog_files/CA_Trans_Template.xml*.
* [Natural conversation tutorial](http://www.ibm.com/smarterplanet/us/en/ibmwatson/developercloud/doc/dialog/#tutorial_advanced): The What's In Theaters app uses a natural conversation template as the basis for the dialog. To design your own dialog in natural conversation, complete this tutorial. See the template here: `/movieapp-dialog/src/main/resources/dialog_files/CA_Trans_Template.xml`.