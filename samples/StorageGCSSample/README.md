StorageGCS Sample
=================

This is a Maven project for building and deploying a (very) small custom spring application for running
on Cloud Run that uses GCP Storage GCS.

Dependencies
------------
Before you attempt this example, please ensure you have done the following: -
- That you have installed Maven (mvn) and - optionally - GCP cloud cli (gcloud)

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
    % docker images gcr.io/investdemo-300915/samples.cloudrun-storagegcssample
    REPOSITORY                                                   TAG       IMAGE ID       CREATED          SIZE
    gcr.io/investdemo-300915/samples.cloudrun-storagegcssample   1.0       870b1d5dc67e   16 minutes ago   288MB

If you wish to test (or change the app) on your local system, then you can either use the 
Docker image or run the app directly using...

    % mvn clean package spring-boot:run
    % curl localhost:8080/bucket/version
    <h2>Version 1.0</h2>

Build Instructions using Docker
-------------------------------
You can also build this sample using Docker. To do this, run the following...

    % docker build . [-t gcr.io/$(gcloud config get-value project)/samples.cloudrun-storagegcssample:1.0]

This will compile and package the Docker image purely using Docker.

Testing the App
---------------
Once you have built the image, you can then run it locally via the Docker image as shown
below...

    % docker run -p 8080:8080 gcr.io/$(gcloud config get-value project)/samples.cloudrun-storagegcssample:1.0

      .   ____          _            __ _ _
     /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
    ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
     \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
      '  |____| .__|_| |_|_| |_\__, | / / / /
     =========|_|==============|___/=/_/_/_/
     :: Spring Boot ::        (v2.2.4.RELEASE)

    2021-04-21 14:29:43.534  INFO 1 --- [           main] s.c.StorageGCSSample.StorageGCSSample    : Starting StorageGCSSample v1.0-SNAPSHOT on 235f974f3016 with PID 1 (/samples.cloudrun-storagegcssample-1.0-SNAPSHOT.jar started by spring in /)
    2021-04-21 14:29:43.539  INFO 1 --- [           main] s.c.StorageGCSSample.StorageGCSSample    : No active profile set, falling back to default profiles: default
    2021-04-21 14:29:45.105  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
    2021-04-21 14:29:45.127  INFO 1 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
    2021-04-21 14:29:45.128  INFO 1 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.30]
    2021-04-21 14:29:45.244  INFO 1 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
    2021-04-21 14:29:45.244  INFO 1 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 1590 ms
    2021-04-21 14:29:45.539  INFO 1 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
    2021-04-21 14:29:45.861  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
    2021-04-21 14:29:45.868  INFO 1 --- [           main] s.c.StorageGCSSample.StorageGCSSample    : Started StorageGCSSample in 3.252 seconds (JVM running for 4.146)

    % curl localhost:8080/bucket/version
    <h2>Version 1.0</h2>
    % curl localhost:8080/bucket/list
    {"List of created buckets":{}} 
    % curl -X POST -H "Content-Type: application/json" \
        -d '{"projectId":"investdemo-300915","bucketName": "testme"}' \
        "localhost:8080/bucket/create"
    {"message":"Bucket creation failed with error: 401 Unauthorized\nPOST https://storage.googleapis.com/storage/v1/b?project=investdemo-300915&projection=full"}

To test the function for real you will need to deploy it to CloudRun.

Deploying the App to CloudRun
-----------------------------
To deploy the app to CloudRun, you can do or use the docker image built above...

    % mvn clean compile jib:build -Dgcp.projectId=$(gcloud config get-value project)
    % gcloud run deploy --image gcr.io/$(gcloud config get-value project)/samples.cloudrun-storagegcssample \
         --platform managed
    Deploying container to Cloud Run service [web8k-example] in project [investdemo-300915] region [europe-west1]
    ... 
    Service [samplescloudrun-storagegcssample] revision [samplescloudrun-storagegcssample-00002-len] has been deployed 
    Service URL: https://samplescloudrun-storagegcssample-r2aphpfqba-uc.a.run.app
    % gcloud run services list --platform managed
    % curl https://samplescloudrun-storagegcssample-r2aphpfqba-uc.a.run.app/bucket/version
    <h2>Version 1.0</h2>
    % curl https://samplescloudrun-storagegcssample-r2aphpfqba-uc.a.run.app/bucket/list
    {"List of created buckets":{}} 
    % curl -X POST -H "Content-Type: application/json" \
        -H "Authorization: bearer $(gcloud auth print-identity-token)" \
        -d '{"projectId":"investdemo-300915","bucketName": "investdemo-300915-bucket"}' \
        "https://samplescloudrun-storagegcssample-r2aphpfqba-uc.a.run.app/bucket/create"
    {"message":"Bucket created successfully"}
    % curl -X POST -H "Content-Type: application/json" \
        -H "Authorization: bearer $(gcloud auth print-identity-token)" \
        -d '{"projectId":"investdemo-300915","bucketName": "investdemo-300915-bucket2"}' \
        "https://samplescloudrun-storagegcssample-r2aphpfqba-uc.a.run.app/bucket/create"
    {"message":"Bucket created successfully"}
    % curl https://samplescloudrun-storagegcssample-r2aphpfqba-uc.a.run.app/bucket/list
    {"List of created buckets":{"investdemo-300915-bucket2":{"name":"investdemo-300915-bucket2","url":"https://www.googleapis.com/storage/v1/b/investdemo-300915-bucket2"},"investdemo-300915-bucket":{"name":"investdemo-300915-bucket","url":"https://www.googleapis.com/storage/v1/b/investdemo-300915-bucket"}}}

Clean Up
--------
To clean up the app, you can do...

    % mvn clean 
    % gcloud run services list --platform managed
    # Use the appropriate service name from above to delete the one you want.
    # The name will depend on the deployment method used.
    # The delete statement below uses "samplescloudrun-storagegcssample" as the name...
    % gcloud run services delete samplescloudrun-storagegcssample --platform managed
    % gsutil ls
    % gsutil rm -fr gs://investdemo-300915-bucket/ gs://investdemo-300915-bucket2/
    % docker image rm -f gcr.io/$(gcloud config get-value project)/samples.cloudrun-storagegcssample:1.0 

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
- https://cloud.google.com/storage/docs/listing-buckets#gsutil
- https://cloud.google.com/storage/docs/samples/storage-create-bucket
- https://googleapis.dev/java/google-cloud-storage/latest/index.html
- https://cloud.google.com/storage/docs/json_api/v1/


[run_button_auto]: https://deploy.cloud.run/?git_repo=https://github.com/tpayne/CloudRun&dir=samples/StorageGCSSample
