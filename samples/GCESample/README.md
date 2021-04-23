GCE Compute Sample
===================

This is a Maven project for building and deploying a (very) small custom spring application for running
on Cloud Run that uses GCP GCE Compute services. It will allow you to create and delete VM instances.

Note - The output from these functions is in raw JSON as they are intended for client apps, not humans to view.
If you wish to make the output human readable, that is left as an action for the reader to do. It is doable
with functions like `toPrettyString()` on JSON slurpers etc.

Security Scanning
-----------------
The `pom.xml` used for this project build has been modified to also support optional security scans for OWASP
dependencies, these can be invoked by doing...

     % mvn clean install -Psecurity-scans
     
The checks can take a long time to run, so are not done by default. 

The results files are...
- CloudRun/samples/GCESample/target/dependency-check-report.html
- CloudRun/samples/GCESample/target/sonatype-clm/module.xml

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
    
To test functions...

    % curl https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app/compute/version
    <h2>Version 1.0</h2>

Create functions...

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

Query functions...

    % curl https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app/compute/list
    {"List of created instances":{"testme1":{"instanceName":"testme1","zone":"us-central1-a","imageName":"ubuntu-os-cloud/global/images/ubuntu-2004-focal-v20200529","networkInterface":"ONE_TO_ONE_NAT","networkConfig":"External NAT","url":"","machineType":"f1-micro"},"testme":{"instanceName":"testme","zone":"us-central1-a","imageName":"ubuntu-os-cloud/global/images/ubuntu-2004-focal-v20200529","networkInterface":"ONE_TO_ONE_NAT","networkConfig":"External NAT","url":"","machineType":"f1-micro"}}}

    % curl "https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app/compute/listAll?projectId=investdemo-300915&zone=us-central1-a"
    {"List of all instances":[{"cpuPlatform":"Intel Haswell","creationTimestamp":"2021-04-23T04:26:57.967-07:00","deletionProtection":false,"disks":[{"autoDelete":true,"boot":true,"deviceName":"persistent-disk-0","diskSizeGb":10,"guestOsFeatures":[{"type":"VIRTIO_SCSI_MULTIQUEUE"},{"type":"UEFI_COMPATIBLE"}],"index":0,"interface":"SCSI","kind":"compute#attachedDisk","licenses":["https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/licenses/ubuntu-2004-lts"],"mode":"READ_WRITE","source":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/disks/testme","type":"PERSISTENT"}],"fingerprint":"P_f5fkx2ldw=","id":2759746860031611374,"kind":"compute#instance","labelFingerprint":"42WmSpB8rSM=","lastStartTimestamp":"2021-04-23T04:27:08.222-07:00","machineType":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/machineTypes/f1-micro","metadata":{"fingerprint":"hFIa-7-XpjY=","kind":"compute#metadata"},"name":"testme","networkInterfaces":[{"accessConfigs":[{"kind":"compute#accessConfig","name":"External NAT","natIP":"34.121.188.214","networkTier":"PREMIUM","type":"ONE_TO_ONE_NAT"}],"fingerprint":"TpJsNPcuG9w=","kind":"compute#networkInterface","name":"nic0","network":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/global/networks/default","networkIP":"10.128.0.26","subnetwork":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/regions/us-central1/subnetworks/default"}],"scheduling":{"automaticRestart":true,"onHostMaintenance":"MIGRATE","preemptible":false},"selfLink":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/instances/testme","serviceAccounts":[{"email":"127131513455-compute@developer.gserviceaccount.com","scopes":["https://www.googleapis.com/auth/devstorage.full_control","https://www.googleapis.com/auth/compute"]}],"shieldedInstanceConfig":{"enableIntegrityMonitoring":true,"enableSecureBoot":false,"enableVtpm":true},"shieldedInstanceIntegrityPolicy":{"updateAutoLearnPolicy":true},"startRestricted":false,"status":"RUNNING","tags":{"fingerprint":"42WmSpB8rSM="},"zone":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a"},{"cpuPlatform":"Intel Haswell","creationTimestamp":"2021-04-23T04:26:39.266-07:00","deletionProtection":false,"disks":[{"autoDelete":true,"boot":true,"deviceName":"persistent-disk-0","diskSizeGb":10,"guestOsFeatures":[{"type":"VIRTIO_SCSI_MULTIQUEUE"},{"type":"UEFI_COMPATIBLE"}],"index":0,"interface":"SCSI","kind":"compute#attachedDisk","licenses":["https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/licenses/ubuntu-2004-lts"],"mode":"READ_WRITE","source":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/disks/testme1","type":"PERSISTENT"}],"fingerprint":"momERo1d1ZU=","id":916676668472365057,"kind":"compute#instance","labelFingerprint":"42WmSpB8rSM=","lastStartTimestamp":"2021-04-23T04:26:47.428-07:00","machineType":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/machineTypes/f1-micro","metadata":{"fingerprint":"hFIa-7-XpjY=","kind":"compute#metadata"},"name":"testme1","networkInterfaces":[{"accessConfigs":[{"kind":"compute#accessConfig","name":"External NAT","natIP":"34.123.160.56","networkTier":"PREMIUM","type":"ONE_TO_ONE_NAT"}],"fingerprint":"ew2NDI6hn5A=","kind":"compute#networkInterface","name":"nic0","network":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/global/networks/default","networkIP":"10.128.0.25","subnetwork":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/regions/us-central1/subnetworks/default"}],"scheduling":{"automaticRestart":true,"onHostMaintenance":"MIGRATE","preemptible":false},"selfLink":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/instances/testme1","serviceAccounts":[{"email":"127131513455-compute@developer.gserviceaccount.com","scopes":["https://www.googleapis.com/auth/devstorage.full_control","https://www.googleapis.com/auth/compute"]}],"shieldedInstanceConfig":{"enableIntegrityMonitoring":true,"enableSecureBoot":false,"enableVtpm":true},"shieldedInstanceIntegrityPolicy":{"updateAutoLearnPolicy":true},"startRestricted":false,"status":"RUNNING","tags":{"fingerprint":"42WmSpB8rSM="},"zone":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a"}]}    

    % curl "https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app/compute/listAll/investdemo-300915/us-central1-a"
    {"List of all instances":[{"cpuPlatform":"Intel Haswell","creationTimestamp":"2021-04-23T12:10:57.548-07:00","deletionProtection":false,"disks":[{"autoDelete":true,"boot":true,"deviceName":"persistent-disk-0","diskSizeGb":10,"guestOsFeatures":[{"type":"VIRTIO_SCSI_MULTIQUEUE"},{"type":"UEFI_COMPATIBLE"}],"index":0,"interface":"SCSI","kind":"compute#attachedDisk","licenses":["https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/licenses/ubuntu-2004-lts"],"mode":"READ_WRITE","source":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/disks/testme","type":"PERSISTENT"}],"fingerprint":"bxBRPbclsk4=","id":4598894054232466734,"kind":"compute#instance","labelFingerprint":"42WmSpB8rSM=","lastStartTimestamp":"2021-04-23T12:11:06.563-07:00","machineType":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/machineTypes/f1-micro","metadata":{"fingerprint":"hFIa-7-XpjY=","kind":"compute#metadata"},"name":"testme","networkInterfaces":[{"accessConfigs":[{"kind":"compute#accessConfig","name":"External NAT","natIP":"34.122.238.17","networkTier":"PREMIUM","type":"ONE_TO_ONE_NAT"}],"fingerprint":"sROInMfZX-o=","kind":"compute#networkInterface","name":"nic0","network":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/global/networks/default","networkIP":"10.128.0.29","subnetwork":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/regions/us-central1/subnetworks/default"}],"scheduling":{"automaticRestart":true,"onHostMaintenance":"MIGRATE","preemptible":false},"selfLink":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/instances/testme","serviceAccounts":[{"email":"127131513455-compute@developer.gserviceaccount.com","scopes":["https://www.googleapis.com/auth/devstorage.full_control","https://www.googleapis.com/auth/compute"]}],"shieldedInstanceConfig":{"enableIntegrityMonitoring":true,"enableSecureBoot":false,"enableVtpm":true},"shieldedInstanceIntegrityPolicy":{"updateAutoLearnPolicy":true},"startRestricted":false,"status":"RUNNING","tags":{"fingerprint":"42WmSpB8rSM="},"zone":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a"},{"cpuPlatform":"Intel Haswell","creationTimestamp":"2021-04-23T12:11:33.619-07:00","deletionProtection":false,"disks":[{"autoDelete":true,"boot":true,"deviceName":"persistent-disk-0","diskSizeGb":10,"guestOsFeatures":[{"type":"VIRTIO_SCSI_MULTIQUEUE"},{"type":"UEFI_COMPATIBLE"}],"index":0,"interface":"SCSI","kind":"compute#attachedDisk","licenses":["https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/licenses/ubuntu-2004-lts"],"mode":"READ_WRITE","source":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/disks/testme1","type":"PERSISTENT"}],"fingerprint":"GNOrJsy4P48=","id":8958709614030791946,"kind":"compute#instance","labelFingerprint":"42WmSpB8rSM=","lastStartTimestamp":"2021-04-23T12:11:41.702-07:00","machineType":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/machineTypes/f1-micro","metadata":{"fingerprint":"hFIa-7-XpjY=","kind":"compute#metadata"},"name":"testme1","networkInterfaces":[{"accessConfigs":[{"kind":"compute#accessConfig","name":"External NAT","natIP":"34.66.172.202","networkTier":"PREMIUM","type":"ONE_TO_ONE_NAT"}],"fingerprint":"Z8bsHN05fTA=","kind":"compute#networkInterface","name":"nic0","network":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/global/networks/default","networkIP":"10.128.0.30","subnetwork":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/regions/us-central1/subnetworks/default"}],"scheduling":{"automaticRestart":true,"onHostMaintenance":"MIGRATE","preemptible":false},"selfLink":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/instances/testme1","serviceAccounts":[{"email":"127131513455-compute@developer.gserviceaccount.com","scopes":["https://www.googleapis.com/auth/devstorage.full_control","https://www.googleapis.com/auth/compute"]}],"shieldedInstanceConfig":{"enableIntegrityMonitoring":true,"enableSecureBoot":false,"enableVtpm":true},"shieldedInstanceIntegrityPolicy":{"updateAutoLearnPolicy":true},"startRestricted":false,"status":"RUNNING","tags":{"fingerprint":"42WmSpB8rSM="},"zone":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a"}]}

    % curl -H "Authorization: bearer $(gcloud auth print-identity-token)" \
        "https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app/compute/describe?projectId=investdemo-300915&zone=us-central1-a&instanceName=testme"
    {"Instance:":{"cpuPlatform":"Intel Haswell","creationTimestamp":"2021-04-23T03:47:03.837-07:00","deletionProtection":false,"disks":[{"autoDelete":true,"boot":true,"deviceName":"persistent-disk-0","diskSizeGb":10,"guestOsFeatures":[{"type":"VIRTIO_SCSI_MULTIQUEUE"},{"type":"UEFI_COMPATIBLE"}],"index":0,"interface":"SCSI","kind":"compute#attachedDisk","licenses":["https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/licenses/ubuntu-2004-lts"],"mode":"READ_WRITE","source":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/disks/testme","type":"PERSISTENT"}],"fingerprint":"WA7t78QVSo4=","id":7496947984810056520,"kind":"compute#instance","labelFingerprint":"42WmSpB8rSM=","lastStartTimestamp":"2021-04-23T03:47:13.913-07:00","machineType":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/machineTypes/f1-micro","metadata":{"fingerprint":"hFIa-7-XpjY=","kind":"compute#metadata"},"name":"testme","networkInterfaces":[{"accessConfigs":[{"kind":"compute#accessConfig","name":"External NAT","natIP":"34.123.160.56","networkTier":"PREMIUM","type":"ONE_TO_ONE_NAT"}],"fingerprint":"rLroH_4xk-0=","kind":"compute#networkInterface","name":"nic0","network":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/global/networks/default","networkIP":"10.128.0.24","subnetwork":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/regions/us-central1/subnetworks/default"}],"scheduling":{"automaticRestart":true,"onHostMaintenance":"MIGRATE","preemptible":false},"selfLink":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/instances/testme","serviceAccounts":[{"email":"127131513455-compute@developer.gserviceaccount.com","scopes":["https://www.googleapis.com/auth/devstorage.full_control","https://www.googleapis.com/auth/compute"]}],"shieldedInstanceConfig":{"enableIntegrityMonitoring":true,"enableSecureBoot":false,"enableVtpm":true},"shieldedInstanceIntegrityPolicy":{"updateAutoLearnPolicy":true},"startRestricted":false,"status":"RUNNING","tags":{"fingerprint":"42WmSpB8rSM="},"zone":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a"}    

    % curl -H "Authorization: bearer $(gcloud auth print-identity-token)" \
        https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app/compute/describe/investdemo-300915/us-central1-a/testme
    {"Instance:":{"cpuPlatform":"Intel Haswell","creationTimestamp":"2021-04-23T12:10:57.548-07:00","deletionProtection":false,"disks":[{"autoDelete":true,"boot":true,"deviceName":"persistent-disk-0","diskSizeGb":10,"guestOsFeatures":[{"type":"VIRTIO_SCSI_MULTIQUEUE"},{"type":"UEFI_COMPATIBLE"}],"index":0,"interface":"SCSI","kind":"compute#attachedDisk","licenses":["https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/licenses/ubuntu-2004-lts"],"mode":"READ_WRITE","source":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/disks/testme","type":"PERSISTENT"}],"fingerprint":"bxBRPbclsk4=","id":4598894054232466734,"kind":"compute#instance","labelFingerprint":"42WmSpB8rSM=","lastStartTimestamp":"2021-04-23T12:11:06.563-07:00","machineType":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/machineTypes/f1-micro","metadata":{"fingerprint":"hFIa-7-XpjY=","kind":"compute#metadata"},"name":"testme","networkInterfaces":[{"accessConfigs":[{"kind":"compute#accessConfig","name":"External NAT","natIP":"34.122.238.17","networkTier":"PREMIUM","type":"ONE_TO_ONE_NAT"}],"fingerprint":"sROInMfZX-o=","kind":"compute#networkInterface","name":"nic0","network":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/global/networks/default","networkIP":"10.128.0.29","subnetwork":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/regions/us-central1/subnetworks/default"}],"scheduling":{"automaticRestart":true,"onHostMaintenance":"MIGRATE","preemptible":false},"selfLink":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a/instances/testme","serviceAccounts":[{"email":"127131513455-compute@developer.gserviceaccount.com","scopes":["https://www.googleapis.com/auth/devstorage.full_control","https://www.googleapis.com/auth/compute"]}],"shieldedInstanceConfig":{"enableIntegrityMonitoring":true,"enableSecureBoot":false,"enableVtpm":true},"shieldedInstanceIntegrityPolicy":{"updateAutoLearnPolicy":true},"startRestricted":false,"status":"RUNNING","tags":{"fingerprint":"42WmSpB8rSM="},"zone":"https://www.googleapis.com/compute/v1/projects/investdemo-300915/zones/us-central1-a"}

Delete functions...

    % curl -X POST -H "Content-Type: application/json" \
        -H "Authorization: bearer $(gcloud auth print-identity-token)" \
        -d '{"projectId":"investdemo-300915","instanceName": "testme1","zone":"us-central1-a"}' \
        "https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app/compute/delete"
    {"message":"Instance deleted successfully"}
    % curl -X DELETE -H "Authorization: bearer $(gcloud auth print-identity-token)" \
        https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app/compute/$(gcloud config get-value project)/us-central1-a/testme
    {"message":"Instance deleted successfully"}
    % curl https://samplescloudrun-gcesample-r2aphpfqba-uc.a.run.app/compute/list
    {"List of created instances":{}}

The following RESTful calls are supported...
- /compute/create - POST JSON call for creating VM instances
- /compute/delete - POST JSON call for deleting VM instances
- /compute/[projectId]/[zone]/[instanceName] - DELETE call for deleting VM instances
- /compute/list - GET call for viewing created instances
- /compute/describe - GET call for viewing details of specific instance
- /compute/describe/[projectId]/[zone]/[instanceName] - GET call for viewing details of specific instance
- /compute/listAll - GET call for viewing details of all instances
- /compute/listAll/[projectId]/[zone] - GET call for viewing details of all instances

All results are returned in JSON.

(Note - The above may not comply to RESTful standards they are primarily shown as examples of how to do it).

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
