CloudRun Sample
===============

This is a Maven project for building and deploying a (very) small custom spring application for running
on Cloud Run

Dependencies
------------
Before you attempt this example, please ensure you have done the following: -
- That you have installed Maven (mvn) and - optionally - GCP cloud cli (gcloud)
- Logged into a terminal window that will allow you to do deployments to a valid K8 cluster
- Have your Kubernetes context set to a system you have permission to deploy to

Fully Automated Build/Deploy
----------------------------
To do a fully automated build and deploy, press the button below.

[<img src="https://storage.googleapis.com/cloudrun/button.svg" alt="Run on Google Cloud" height="30">][run_button_auto]

Build Instructions on Local Machine
-----------------------------------
To run this sample do the following.

First, find out your current GCP project...

    % gcloud config get-value project 
    % gcloud info
    
The following commands will build and deploy the application...

    % mvn compile jib:dockerBuild -Dgcp.projectId=$(gcloud config get-value project)
    % docker images gcr.io/investdemo-300915/web8k-example
    REPOSITORY                               TAG       IMAGE ID       CREATED        SIZE
    gcr.io/investdemo-300915/web8k-example   latest    1ae6f324852b   51 years ago   148MB

If you wish to test (or change the app) on your local system, then you can either use the 
Docker image or run the app directly using...

    % mvn clean package spring-boot:run
    % curl localhost:8080/cmd/version
    <h2>Version 1.0</h2>

Build Instructions using Docker
-------------------------------
You can also build this sample using Docker. To do this, run the following...

    % docker build . [-t gcr.io/$(gcloud config get-value project)/web8k-example:1.0]

This will compile and package the Docker image purely using Docker.

Testing the App
---------------
You can then run it locally with the Docker image, you can...

    % docker run -p 8080:8080 gcr.io/$(gcloud config get-value project)/web8k-example
      .   ____          _            __ _ _
     /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
    ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
     \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
      '  |____| .__|_| |_|_| |_\__, | / / / /
     =========|_|==============|___/=/_/_/_/
     :: Spring Boot ::        (v2.2.2.RELEASE)

    2021-04-19 12:45:02.809  INFO 1 --- [           main] application.Webk8App                     : Starting Webk8App on 075d36156371 with PID 1 (/app/classes started by root in /)
    2021-04-19 12:45:02.814  INFO 1 --- [           main] application.Webk8App                     : No active profile set, falling back to default profiles: default
    2021-04-19 12:45:04.403  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
    2021-04-19 12:45:04.418  INFO 1 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
    2021-04-19 12:45:04.419  INFO 1 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.29]
    2021-04-19 12:45:04.526  INFO 1 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
    2021-04-19 12:45:04.527  INFO 1 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 1582 ms
    2021-04-19 12:45:04.806  INFO 1 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
    2021-04-19 12:45:04.932  INFO 1 --- [           main] o.s.b.a.w.s.WelcomePageHandlerMapping    : Adding welcome page: class path resource [static/index.html]
    2021-04-19 12:45:05.116  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
    2021-04-19 12:45:05.120  INFO 1 --- [           main] application.Webk8App                     : Started Webk8App in 2.888 seconds (JVM running for 3.44)    

    % curl localhost:8080/cmd/version
    <h2>Version 1.0</h2>

To test using POST and GET methods, you can do...

    % curl -X POST -H "Content-Type: application/json" \
            -d '{"name": "james", "surName": "bailey"}' \
            http://localhost:8080/user/create
    % curl http://localhost:8080/user/list
    <p><b>The following users are registered...</b><br><ol><li>james bailey</li><ol></p>


Deploying the App to CloudRun
-----------------------------
To deploy the app to CloudRun, you can do...

    % mvn clean compile jib:build -Dgcp.projectId=$(gcloud config get-value project)
    % gcloud run deploy --image gcr.io/$(gcloud config get-value project)/web8k-example --platform managed
    Deploying container to Cloud Run service [web8k-example] in project [investdemo-300915] region [europe-west1]
    ... 
    Service [web8k-example] revision [web8k-example-00001-roj] has been deployed and is serving 100 percent of traffic.
    Service URL: https://web8k-example-r2aphpfqba-ew.a.run.app
    % gcloud run services list --platform managed
    % curl https://web8k-example-r2aphpfqba-ew.a.run.app/cmd/version
    <h2>Version 1.0</h2>

Clean Up
--------
To clean up the app, you can do...

    % mvn clean jib:clean -Dgcp.projectId=$(gcloud config get-value project)
    % gcloud run services list --platform managed
    % gcloud run services delete web8k-example --platform managed
    % docker image rm -f gcr.io/$(gcloud config get-value project)/web8k-example 


References
----------
The following references might be of interest...
- https://www.eclipse.org/jkube/docs/kubernetes-maven-plugin
- https://www.eclipse.org/jkube/docs/kubernetes-maven-plugin?fbclid=IwAR215doMPlD91r-l4OKZ0954PuWILNPGY3i7XCWaER1M2mmyVUWWhtMqXUA#registry
- https://www.eclipse.org/jkube/docs/kubernetes-maven-plugin#enrichers
- https://spring.io/guides/gs/spring-boot-kubernetes/
- https://spring.io/why-spring
- https://rohaan.medium.com/deploy-any-spring-boot-application-into-kubernetes-using-eclipse-jkube-a4167d27ee45


[run_button_auto]: https://deploy.cloud.run/?git_repo=https://github.com/tpayne/CloudRun&dir=run/samples/SimpleSpringApp
