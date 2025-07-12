# GCE Compute Sample

This is a Maven project for building and deploying a (very) small custom Spring application for running on Cloud Run that uses GCP GCE Compute services. It will allow you to create and delete VM instances.

### Note
The output from these functions is in raw JSON as they are intended for client apps, not humans to view. If you wish to make the output human-readable, that is left as an action for the reader to do. It is doable with functions like `toPrettyString()` on JSON slurpers etc.

### Security Scanning
The `pom.xml` used for this project build has been modified to also support optional security scans for OWASP dependencies. These can be invoked by doing:

```bash
    mvn clean install -Psecurity-scans
```

The checks can take a long time to run, so are not done by default. 

The results files are:
- `CloudRun/samples/GCESample/target/dependency-check-report.html`
- `CloudRun/samples/GCESample/target/sonatype-clm/module.xml`

### Dependencies
Before you attempt this example, please ensure you have done the following:
- Installed Maven (mvn)
- Logged into a (Unix) terminal window that will allow you to do deployments to a valid GCP account

### Build Instructions on Local Machine
To run this sample on your local machine do the following.

First, find out your current GCP project:

```bash
gcloud config get-value project 
gcloud config get-value run/region
```

You can set it up with:

```bash
gcloud projects list 
gcloud compute regions list 
gcloud config set project <id> 
gcloud config set run/region <region>
```

**Build and Run Locally**:
```bash
    mvn clean package spring-boot:run
    curl localhost:8080/compute/version
```

### Deploying the App to Cloud Run
To deploy the app to Cloud Run, you can do the following or use the docker image built above:

```bash
    mvn clean compile jib:build -Dgcp.projectId=$(gcloud config get-value project)
    gcloud run deploy --image gcr.io/$(gcloud config get-value project)/samples.cloudrun-gcesample --platform managed
```

### Testing the Functions
After deploying, you can use the following commands to test the functionalities via the Cloud Run URL:

```bash
    curl https://<your-service-url>/compute/version
    curl -X POST -H "Content-Type: application/json" -d '{"projectId":"<your-project-id>","instanceName": "testme","zone":"<your-zone>"}' "https://<your-service-url>/compute/create"
```

### Clean Up
To clean up the app, you can do:

```bash
    mvn clean
    gcloud run services delete samplescloudrun-gcesample --platform managed
    docker image rm -f gcr.io/$(gcloud config get-value project)/samples.cloudrun-gcesample:1.0
```

### References
- [jkube](https://www.eclipse.org/jkube/docs/kubernetes-maven-plugin)
- [Spring Boot for Kubernetes](https://spring.io/guides/gs/spring-boot-kubernetes/)
- [Google Cloud Run](https://cloud.google.com/run)
