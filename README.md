# What's In Theaters Application Overview
The What's in Theaters application showcases the best practices in building a Watson conversational solution by using the Watson Dialog service. The application allows users to converse with Watson to search for current or upcoming movies by using [themoviedb.org](https://www.themoviedb.org/) database. Here's a [quick demo](http://watson-movieapp-dialog.mybluemix.net/watson-movieapp-dialog/dist/index.html#/).

The application uses a conversational template that you can customize to suit your own application. This domain-independent template will help you to structure your dialogs according to how people naturally converse, thus helping you to bootstrap your future dialogs.

## How it works
Users talk in natural language to the system to find movies that match the search criteria they've specified. The system is built to understand natural language that relates to searching for and selecting movies to watch. For example, saying “I'd like to see a recent R rated drama” causes the system to search the movie repository and to return the names of all R-rated dramas that have been released in the last 30 days.

The system is designed to obtain the following types of information about movies from users before it searches the repository:

  * **Recency**. The system determines whether users want to know about currently playing movies or upcoming movies.
  * **Genre**. The system understands movie genres, such as action, comedy, and horror.
  * **Rating**. The system understands movie rating, such as G, PG-13, and R.

Users can search across all genres and ratings simply by answering "no" to the corresponding questions. Before the system searches the movie repository, it needs to know whether a user prefer current movies or upcoming movies. The system understands variations of text, so users can rephrase their responses, and the system will still process it. For example, the system might ask, "Do you want to watch an upcoming movie or one that's playing tonight?" Users can say "tonight" or "Show me movies playing currently," and the system understands that both answers mean that users want to know about current movies.

## Before you begin
Ensure that you have the following prerequisites before you start:
* An IBM Bluemix account. If you don't have one, sign up for it [here](https://apps.admin.ibmcloud.com/manage/trial/bluemix.html?cm_mmc=WatsonDeveloperCloud-_-LandingSiteGetStarted-_-x-_-CreateAnAccountOnBluemixCLI). For more information about the process, see [Developing Watson applications with Bluemix](http://www.ibm.com/smarterplanet/us/en/ibmwatson/developercloud/doc/getting_started/gs-bluemix.shtml).
* [Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html) 1.7 or later releases
* [Eclipse IDE for Java EE Developers](https://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/marsr)
* [Apache Maven](https://maven.apache.org/download.cgi) 3.1 or later releases
* [Git](https://git-scm.com/downloads)
* [Websphere Liberty Profile server](https://developer.ibm.com/wasdev/downloads/liberty-profile-using-non-eclipse-environments/), if you want to run the app in your local environment
* Request an API key from themoviedb.org. For more info see the [API documentation](https://www.themoviedb.org/documentation/api).

## Setup
In order to run the What's In Theaters app, you need to have a Dialog service instance bound to an app in your IBM Bluemix account. The following steps will guide you through the process. The instructions use Eclipse, but you can use the IDE of your choice.

### Get the project from GitHub
1. Clone the watson-movieapp-dialog repository from GitHub by issuing one of the following commands in your terminal:
   ```
   git clone https://github.com:watson-developer-cloud/movieapp-dialog.git
   ```
   ```
   git clone git@github.com:watson-developer-cloud/movieapp-dialog.git
   ```

2. Add the newly cloned repository to your local Eclipse workspace.

### Set up the Bluemix Environment
#### Creating an App
1. [Log in to Bluemix](https://console.ng.bluemix.net/) and navigate to the *Dashboard* on the top panel.
2. Create your app.
       1. Click **CREATE AN APP**.
       2. Select **WEB**.
      3. Select the starter **Liberty for Java**, and click **CONTINUE**.
      4. Type a unique name for your app, such as `dialog-sample-app`, and click **Finish**.
      5. Select **CF Command Line Interface**. If you do not already have it, click **Download CF Command Line Interface**. This link opens a GitHub repository. Download and install it locally.

#### Adding an instance of the Dialog service
Complete one of the following sets of steps to add an instance of the Dialog service. Bluemix allows you to create a new service instance to bind to your app or to bind to an existing instance. Choose one of the following ways:
1. Creating a new service instance to bind to your app
     1. [Log in to Bluemix](https://console.ng.bluemix.net/) and navigate to the *Dashboard* on the top panel. Find the app that you created in the previous section, and click it.
     2. Click **ADD A SERVICE OR API**.
     3. Select the **Watson** category, and select the **Dialog** service.
     4. Ensure that your app is specified in the **App** dropdown on the right-hand side of the pop-up window under **Add Service**.
     5. Type a unique name for your service in the **Service name** field, such as `dialog-sample-service`.
     6. Click **CREATE**. The **Restage Application** window is displayed.
     7. Click **RESTAGE** to restart your app.

  5. If the app is not started, click **START**.
  6. To view the home page of the app, open [https://yourAppName.mybluemix.net](https://yourAppName.mybluemix.net), where yourAppName is the name of your app.

2. Binding to an existing service instance
     1. [Log in to Bluemix](https://console.ng.bluemix.net/) and navigate to the *Dashboard* on the top panel. Locate and click on the app you created in the previous section.
     2. Click **BIND A SERVICE OR API**.
     3. Select the existing Dialog service that you want to bind to your app, and click **ADD**. The **Restage Application** window is displayed.
     4. Click **RESTAGE** to restart your app.

### Build the app
This project is configured to be built with Maven. To deploy the app, complete the following steps in order:
1. In your Eclipse window, expand the *movieapp-dialog* project that you cloned from GitHub.
2. Right-click the project and select `Maven -> Update Project` from the context menu to update Maven dependencies.
3. Keep the default options, and click **OK**.
4. Navigate to the `movieapp-dialog/src/it/resources/` directory.
5. Open the `server.env` file, and update the following entries:
    * **DIALOG_ID**. Specify the ID value that corresponds to your Dialog service account on Bluemix(the dialog id is a long alpha-numeric string).
    * **TMDB_API_KEY**. Specify the API key you received after you registered for API access on themoviedb.org.
    * **VCAP_SERVICES**. For the **name* attribute, specify the same name that you gave your instance of the Dialog service in the previous section. The following example shows a *VCAP_SERVICES* entry that uses the name `dialog-sample-service`:
  ```
          VCAP_SERVICES= {"dialog": [{"name": “dialog-sample-service”, "label": "dialog”
  ```
6. Switch to the navigator view in Eclipse, right-click the `pom.xml`, and select `Run As -> Maven Install`. Installation of Maven begins. During the installation, the following tasks are done:
    * The JS code is compiled. That is, the various Angular JS files are aggregated, uglified, and compressed. Various other pre-processing is performed on the web code, and the output is copied to the `movieapp-dialog/src/main/webapp/dist` folder in the project.
    * The Java code is compiled, and JUnit tests are executed against the Java code. The compiled Java and JavaScript code and various other artifacts that are required by the web project are copied to a temporary location, and a `.war` file is created.
    * The Maven install instantiates a new Websphere Liberty Profile server, deploys the `.war` file to the server, starts the server, and runs a battery of integration tests against the deployed web application.

This WAR file that resides in */movieapp-dialog/target directory* will be used to deploy the application on Bluemix in the next section.

### Deploy the app
You can run the application on a local server or on Bluemix. Choose one of the following methods, and complete the steps:
#### Deploying the app on your local server in Eclipse
1. Start Eclipse, and click `Window -> Show View -> Servers`.
2. In the **Servers** view, right-click and select `New -> Server`. The *Define a New Server* window is displayed.
3. Select a server, and click **Next**. The *Add and Remove* window is displayed.
4. In the **Available** list, select the project, and click **Add**. The project is added to the runtime configuration for the server in the **Configured** list.
5. Click **Finish**.
6. Start the new server, and open [http://localhost:serverPort/yourAppName/dist/index.html#/](http://localhost:serverPort/yourAppName/dist/index.html#/) in your favorite browser, where yourAppName is the specific name of your app.
7. Chat with What's in Theaters!

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
where *yourUsername* is your Bluemix id, *yourOrg* is your organization name in Bluemix and *yourSpace* is your space name in Bluemix.
5. Deploy the app to Bluemix by running the following command.
   ```
   cf push <yourAppName> -p movieapp-dialog.war
   ```
where, *yourAppName* is the name of your app.
6. Navigate to [Bluemix](https://console.ng.bluemix.net/) to make sure the app is started. If not, click START.
7. To view the home page of the app, open [https://yourAppName.mybluemix.net/movieapp-dialog/dist/index.html#/](https://yourAppName.mybluemix.net/movieapp-dialog/dist/index.html#/), where yourAppName is the specific name of your app.
8. Chat with What's in Theaters!

## Reference information
* [Dialog service documentation](https://dialog-doc-la.mybluemix.net/doc/dialog/index.html): Get an in-depth knowledge of the Dialog service
* [Dialog service API documentation](https://dialog-doc-la.mybluemix.net/apis/): Understand API usage
* [Dialog service tutorial](https://dialog-doc-la.mybluemix.net/doc/dialog/index.html#tutorial_intro): Design your own dialog by using a tutorial, which includes a generic template that you can modify for your own use.
