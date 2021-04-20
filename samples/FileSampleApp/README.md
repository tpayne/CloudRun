CloudRun Sample
===============

This is a Maven project for building and deploying a (very) small custom spring application for running
on Cloud Run

This code has been adapted from [here](https://bezkoder.com/spring-boot-file-upload/).

Dependencies
------------
Before you attempt this example, please ensure you have done the following: -
- That you have installed Maven (mvn) and - optionally - GCP cloud cli (gcloud)
- Logged into a terminal window that will allow you to do deployments to a valid K8 cluster
- Have your Kubernetes context set to a system you have permission to deploy to

Fully Automated Build/Deploy
----------------------------
To do a fully automated build and deploy, press the button below. This will run the process directly on
GCP - although you will need to answer some questions.

[<img src="https://storage.googleapis.com/cloudrun/button.svg" alt="Run on Google Cloud" height="30">][run_button_auto]

Build Instructions on Local Machine
-----------------------------------
To run this sample on your local machine do the following.

First, find out your current GCP project...

    % gcloud config get-value project 
    % gcloud info
    
The following commands will build and deploy the application...

    % mvn compile jib:dockerBuild -Dgcp.projectId=$(gcloud config get-value project)
    % docker images gcr.io/investdemo-300915/samples.cloudrun-fileapp
    REPOSITORY                                          TAG       IMAGE ID       CREATED        SIZE
    gcr.io/investdemo-300915/samples.cloudrun-fileapp   latest    1ae6f324852b   51 years ago   148MB

If you wish to test (or change the app) on your local system, then you can either use the 
Docker image or run the app directly using...

    % mvn clean package spring-boot:run
    % curl localhost:8080/cmd/version
    <h2>Version 1.0</h2>

Build Instructions using Docker
-------------------------------
You can also build this sample using Docker. To do this, run the following...

    % docker build . [-t gcr.io/$(gcloud config get-value project)/samples.cloudrun-fileapp:1.0]

This will compile and package the Docker image purely using Docker.

Testing the App
---------------
Once you have built the image, you can then run it locally via the Docker image as shown
below...

    % docker run -p 8080:8080 gcr.io/$(gcloud config get-value project)/samples.cloudrun-fileapp:1.0
      .   ____          _            __ _ _
     /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
    ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
     \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
      '  |____| .__|_| |_|_| |_\__, | / / / /
     =========|_|==============|___/=/_/_/_/
     :: Spring Boot ::        (v2.2.4.RELEASE)

    2021-04-20 19:56:46.085  INFO 1 --- [           main] samples.cloudrun.FileApp.FileApp         : Starting FileApp v1.0-SNAPSHOT on 148f80c2c92d with PID 1 (/samples.cloudrun-fileapp-1.0-SNAPSHOT.jar started by spring in /)
    2021-04-20 19:56:46.089  INFO 1 --- [           main] samples.cloudrun.FileApp.FileApp         : No active profile set, falling back to default profiles: default
    2021-04-20 19:56:47.695  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
    2021-04-20 19:56:47.711  INFO 1 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
    2021-04-20 19:56:47.712  INFO 1 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.30]
    2021-04-20 19:56:47.788  INFO 1 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
    2021-04-20 19:56:47.788  INFO 1 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 1604 ms
    2021-04-20 19:56:48.039  INFO 1 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
    2021-04-20 19:56:48.264  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
    2021-04-20 19:56:48.273  INFO 1 --- [           main] samples.cloudrun.FileApp.FileApp         : Started FileApp in 2.905 seconds (JVM running for 3.805)

To test the app, do...

    % ls > f.txt; ls -laR > f1.txt
    % curl -F 'file=@f.txt' http://localhost:8080/upload; curl -F 'file=@f1.txt' http://localhost:8080/upload
    % curl http://localhost:8080/files
    [{"name":"f.txt","url":"http://localhost:8080/files/f.txt"},{"name":"f1.txt","url":"http://localhost:8080/files/f1.txt"}]
    % curl http://localhost:8080/files/f.txt
    ...

The contents of the file `f.txt` will be displaid.

Deploying the App to CloudRun
-----------------------------
To deploy the app to CloudRun, you can do...

    % mvn clean compile jib:build -Dgcp.projectId=$(gcloud config get-value project)
    % gcloud run deploy --image gcr.io/$(gcloud config get-value project)/samples.cloudrun-fileapp --platform managed
    Deploying container to Cloud Run service [samplescloudrun-fileapp] in project [investdemo-300915] region [europe-west1]
    ... 
    Service [samplescloudrun-fileapp] revision [samplescloudrun-fileapp-fileapp-00001-roj] has been deployed and is serving 100 percent of traffic.
    Service URL: https://samplescloudrun-fileapp-r2aphpfqba-uc.a.run.app
    % gcloud run services list --platform managed

Then test as above using the specified URL.

Clean Up
--------
To clean up the app, you can do...

    % mvn clean 
    % gcloud run services list --platform managed
    # Use the appropriate service name from above to delete the one you want.
    # The name will depend on the deployment method used.
    # The delete statement below uses "samplescloudrun-fileapp" as the name...
    % gcloud run services delete samplescloudrun-fileapp --platform managed
    % docker image rm -f gcr.io/$(gcloud config get-value project)/samples.cloudrun-fileapp:1.0


References
----------
The following references might be of interest...
- https://www.eclipse.org/jkube/docs/kubernetes-maven-plugin
- https://www.eclipse.org/jkube/docs/kubernetes-maven-plugin?fbclid=IwAR215doMPlD91r-l4OKZ0954PuWILNPGY3i7XCWaER1M2mmyVUWWhtMqXUA#registry
- https://www.eclipse.org/jkube/docs/kubernetes-maven-plugin#enrichers
- https://spring.io/guides/gs/spring-boot-kubernetes/
- https://spring.io/why-spring
- https://cloud.google.com/run
- https://spring.io/guides/gs/spring-boot-docker/

[run_button_auto]: https://deploy.cloud.run/?git_repo=https://github.com/tpayne/CloudRun&dir=samples/FileSampleApp
