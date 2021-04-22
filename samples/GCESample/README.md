GCE Compute Sample
===================

This is a Maven project for building and deploying a (very) small custom spring application for running
on Cloud Run that uses GCP GCE Compute services. It will allow to create and delete VM instances.

Dependencies
------------
Before you attempt this example, please ensure you have done the following: -
- That you have installed Maven (mvn) and - optionally - GCP cloud cli (gcloud)
- You have logged into a (Unix) terminal window that will allow you to do deployments to a valid GCP account

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
    % docker images gcr.io/investdemo-300915/samples.cloudrun-gcesample
    REPOSITORY                                            TAG       IMAGE ID       CREATED        SIZE
    gcr.io/investdemo-300915/samples.cloudrun-gcesample   latest    78d327eb3911   51 years ago   156MB

If you wish to test (or change the app) on your local system, then you can either use the 
Docker image or run the app directly using...

    % mvn clean package spring-boot:run
    % curl localhost:8080/compute/version
    <h2>Version 1.0</h2>

Build Instructions using Docker
-------------------------------
You can also build this sample using Docker. To do this, run the following...

    % docker build . [-t gcr.io/$(gcloud config get-value project)/samples.cloudrun-gcesample:1.0]

This will compile and package the Docker image purely using Docker.

Testing the App
---------------
Once you have built the image, you can then run it locally via the Docker image as shown
below...

    % docker run -p 8080:8080 gcr.io/$(gcloud config get-value project)/samples.cloudrun-gcesample:1.0
      .   ____          _            __ _ _
     /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
    ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
     \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
      '  |____| .__|_| |_|_| |_\__, | / / / /
     =========|_|==============|___/=/_/_/_/
     :: Spring Boot ::        (v2.2.4.RELEASE)

    2021-04-22 15:54:53.214  INFO 1 --- [           main] samples.cloudrun.GCESample.GCESample     : Starting GCESample v1.0-SNAPSHOT on 26b7dc5d3ae3 with PID 1 (/samples.cloudrun-gcesample-1.0-SNAPSHOT.jar started by spring in /)
    2021-04-22 15:54:53.218  INFO 1 --- [           main] samples.cloudrun.GCESample.GCESample     : No active profile set, falling back to default profiles: default
    2021-04-22 15:54:54.567  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
    2021-04-22 15:54:54.584  INFO 1 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
    2021-04-22 15:54:54.584  INFO 1 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.30]
    2021-04-22 15:54:54.675  INFO 1 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
    2021-04-22 15:54:54.676  INFO 1 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 1370 ms
    2021-04-22 15:54:55.393  INFO 1 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
    2021-04-22 15:54:55.718  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
    2021-04-22 15:54:55.729  INFO 1 --- [           main] samples.cloudrun.GCESample.GCESample     : Started GCESample in 3.143 seconds (JVM running for 3.999)
    2021-04-22 15:57:26.933  INFO 1 --- [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
    2021-04-22 15:57:26.934  INFO 1 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
    2021-04-22 15:57:26.945  INFO 1 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 11 ms


    % curl localhost:8080/compute/version
    <h2>Version 1.0</h2>
    % curl localhost:8080/compute/list
    {"List of created instances":{}} 
    % curl -X POST -H "Content-Type: application/json" \
        -d '{"projectId":"investdemo-300915","instanceName": "testme","zone":"europe-west6-a"}' \
        "localhost:8080/compute/create"
    {"message":"Instance creation failed with error: The Application Default Credentials are not available. They are available if running in Google Compute Engine. Otherwise, the environment variable GOOGLE_APPLICATION_CREDENTIALS must be defined pointing to a file defining the credentials. See https://developers.google.com/accounts/docs/application-default-credentials for more information."}

To test the function for real you will need to deploy it to CloudRun.

Deploying the App to CloudRun
-----------------------------
To deploy the app to CloudRun, you can do or use the docker image built above...

    % mvn clean compile jib:build -Dgcp.projectId=$(gcloud config get-value project)
    % gcloud run deploy --image gcr.io/$(gcloud config get-value project)/samples.cloudrun-gcesample \
         --platform managed
    Deploying container to Cloud Run service [samplescloudrun-gcesample] in project [investdemo-300915] region [europe-west1]
    ... 
    Service [samplescloudrun-gcesample] revision [samplescloudrun-gcesample-00005-sim] has been deployed and is serving 100 percent of traffic.
    Service URL: https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app    % gcloud run services list --platform managed
    % curl https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app/compute/version
    <h2>Version 1.0</h2>
    % curl https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app/compute/list
    {"List of created instances":{}} 
    % curl -X POST -H "Content-Type: application/json" \
        -H "Authorization: bearer $(gcloud auth print-identity-token)" \
        -d '{"projectId":"investdemo-300915","instanceName": "testme","zone":"us-central1-a"}' \
        "https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app/compute/create"
    {"message":"Instance created successfully"}
    % curl -X POST -H "Content-Type: application/json" \
        -H "Authorization: bearer $(gcloud auth print-identity-token)" \
        -d '{"projectId":"investdemo-300915","instanceName": "testme1","zone":"us-central1-a"}' \
        "https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app/compute/create"
    {"message":"Instance created successfully"}
    % curl https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app/compute/list
    {"List of created instances":{"testme1":{"instanceName":"testme1","zone":"us-central1-a","imageName":"ubuntu-os-cloud/global/images/ubuntu-2004-focal-v20200529","networkInterface":"ONE_TO_ONE_NAT","networkConfig":"External NAT","url":"","machineType":"f1-micro"},"testme":{"instanceName":"testme","zone":"us-central1-a","imageName":"ubuntu-os-cloud/global/images/ubuntu-2004-focal-v20200529","networkInterface":"ONE_TO_ONE_NAT","networkConfig":"External NAT","url":"","machineType":"f1-micro"}}}
    % curl -X POST -H "Content-Type: application/json" \
        -H "Authorization: bearer $(gcloud auth print-identity-token)" \
        -d '{"projectId":"investdemo-300915","instanceName": "testme1","zone":"us-central1-a"}' \
        "https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app/compute/delete"
    {"message":"Instance deleted successfully"}
    % curl -X POST -H "Content-Type: application/json" \
        -H "Authorization: bearer $(gcloud auth print-identity-token)" \
        -d '{"projectId":"investdemo-300915","instanceName": "testme","zone":"us-central1-a"}' \
        "https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app/compute/delete"
    {"message":"Instance deleted successfully"}
    % curl https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app/compute/list
    {"List of created instances":{}}

Clean Up
--------
To clean up the app, you can do...

    % mvn clean 
    % gcloud run services list --platform managed
    # Use the appropriate service name from above to delete the one you want.
    # The name will depend on the deployment method used.
    # The delete statement below uses "samplescloudrun-gcesample" as the name...
    % gcloud run services delete samplescloudrun-gcesample --platform managed
    % docker image rm -f gcr.io/$(gcloud config get-value project)/samples.cloudrun-gcesample:1.0 

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
- https://developers.google.com/resources/api-libraries/documentation/compute/beta/java/latest/index.html?com/google/api/services/compute/Compute.Instances.List.html

[run_button_auto]: https://deploy.cloud.run/?git_repo=https://github.com/tpayne/CloudRun&dir=samples/GCESample
