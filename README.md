CloudRun
--------
This repository provides various example applications which can be deployed to GCP Cloud Run.

Very crudely, Cloud Run are hosted containers that act as services based on runtime images like
Springboot. These services are cheaper to run than [Cloud Functions](https://github.com/tpayne/CloudFunctions) and are able to provide faster cold start times.

These samples assume you have a basic understanding of GCP, hosted compute services and Docker.

Status
------
````
Cloud Run Status: Ready for use
````

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
|[samples/GCESample](samples/GCESample/) | A simple GCE compute sample | [<img src="https://storage.googleapis.com/cloudrun/button.svg" alt="Run on Google Cloud" height="30">][run_button_gcesample] |
|[samples/SimpleSpringApp](samples/SimpleSpringApp/) | A simple SpringApp sample | [<img src="https://storage.googleapis.com/cloudrun/button.svg" alt="Run on Google Cloud" height="30">][run_button_simplespringapp] |
|[samples/StorageGCSSample](samples/StorageGCSSample/) | A simple GCS storage sample | [<img src="https://storage.googleapis.com/cloudrun/button.svg" alt="Run on Google Cloud" height="30">][run_button_gcssample] |

Cloud Functions vrs CloudRun
----------------------------
Which one to use? Well, that is essentially up to the scenario you are coding for. They both achieve
the same functionally, but do it in slightly different ways.

Generally, I would suggest using Cloud Run for REST and HTTP/S service endpoints and using Cloud Functions
for event based operations. There is a fairly long discussion about which approach to use [here](https://medium.com/google-cloud/cloud-run-and-cloud-function-what-i-use-and-why-12bb5d3798e1).

One of the challenges with the public cloud providers is that there is always multiple ways of doing
the samething. You have to choose the one that best serves the problem you are trying to solve.

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

Liability Warning
-----------------
The contents of this repository (documents and examples) are provided “as-is” with no warrantee implied 
or otherwise about the accuracy or functionality of the examples.

You use them at your own risk. If anything results to your machine or environment or anything else as a 
result of ignoring this warning, then the fault is yours only and has nothing to do with me.

[run_button_simplespringapp]: https://deploy.cloud.run/?git_repo=https://github.com/tpayne/CloudRun&dir=samples/SimpleSpringApp
[run_button_filesampleapp]: https://deploy.cloud.run/?git_repo=https://github.com/tpayne/CloudRun&dir=samples/FileSampleApp
[run_button_gcssample]: https://deploy.cloud.run/?git_repo=https://github.com/tpayne/CloudRun&dir=samples/StorageGCSSample
[run_button_gcesample]: https://deploy.cloud.run/?git_repo=https://github.com/tpayne/CloudRun&dir=samples/GCESample
