CloudRun
--------
This repository provides various example applications which can be deployed to GCP Cloud Run.

Very crudely, Cloud Run are hosted images that act as services based on runtime containers like
Springboot. These services are cheaper to run than Cloud Functions and are able to provide faster
cold start times.

Samples
-------
The following samples are provided. You can use the `Run on Cloud` button to build and deploy the
samples directly to GCP.

|           Sample                |        Description       |     Deploy    |
| ------------------------------- | ------------------------ | ------------- |
|[samples/SimpleSpringApp](samples/SimpleSpringApp/) | A simple SpringApp sample | [<img src="https://storage.googleapis.com/cloudrun/button.svg" alt="Run on Google Cloud" height="30">][run_button_simplespringapp] |

Notes
-----
The following are additional Cloud Run samples and documentation
- https://github.com/GoogleCloudPlatform/java-docs-samples/tree/master/run
- https://cloud.google.com/run
- https://cloud.google.com/run/docs/continuous-deployment-with-cloud-build
- https://cloud.google.com/run/docs/tutorials
- https://cloud.google.com/run/docs/quickstarts
- https://cloud.google.com/run/docs/how-to

[run_button_simplespringapp]: https://deploy.cloud.run/?git_repo=https://github.com/tpayne/CloudRun&dir=samples/SimpleSpringApp

