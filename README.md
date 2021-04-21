CloudRun
--------
This repository provides various example applications which can be deployed to GCP Cloud Run.

Very crudely, Cloud Run are hosted containers that act as services based on runtime images like
Springboot. These services are cheaper to run than [Cloud Functions](https://github.com/tpayne/CloudFunctions) and are able to provide faster cold start times.

These sample assume you have a basic understanding of GCP, hosted compute services and Docker.

Build Status
------------
[![Java CI with Maven](https://github.com/tpayne/CloudRun/actions/workflows/maven.yml/badge.svg)](https://github.com/tpayne/CloudRun/actions/workflows/maven.yml)

Samples
-------
The following samples are provided. You can use the `Run on Cloud` button to build and deploy the
samples directly to GCP - assuming you have an account setup to use GCP!

|           Sample                |        Description       |     Deploy    |
| ------------------------------- | ------------------------ | ------------- |
|[samples/FileSampleApp](samples/FileSampleApp/) | A simple File loading sample | [<img src="https://storage.googleapis.com/cloudrun/button.svg" alt="Run on Google Cloud" height="30">][run_button_filesampleapp] |
|[samples/SimpleSpringApp](samples/SimpleSpringApp/) | A simple SpringApp sample | [<img src="https://storage.googleapis.com/cloudrun/button.svg" alt="Run on Google Cloud" height="30">][run_button_simplespringapp] |
|[samples/StorageGCSSample](samples/StorageGCSSample/) | A simple GCS storage sample | [<img src="https://storage.googleapis.com/cloudrun/button.svg" alt="Run on Google Cloud" height="30">][run_button_gcssample] |

Notes
-----
The following are additional Cloud Run samples and documentation
- https://github.com/GoogleCloudPlatform/java-docs-samples/tree/master/run
- https://cloud.google.com/run
- https://cloud.google.com/run/docs/continuous-deployment-with-cloud-build
- https://cloud.google.com/run/docs/tutorials
- https://cloud.google.com/run/docs/quickstarts
- https://cloud.google.com/run/docs/how-to
- https://docs.spring.io/spring-framework/docs/4.3.1.RELEASE/javadoc-api/index.html?overview-summary.html

[run_button_simplespringapp]: https://deploy.cloud.run/?git_repo=https://github.com/tpayne/CloudRun&dir=samples/SimpleSpringApp
[run_button_filesampleapp]: https://deploy.cloud.run/?git_repo=https://github.com/tpayne/CloudRun&dir=samples/FileSampleApp
[run_button_gcssample]: https://deploy.cloud.run/?git_repo=https://github.com/tpayne/CloudRun&dir=samples/StorageGCSSample
