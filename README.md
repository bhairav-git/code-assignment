# Changes around production readiness

1. Swagger creation for API documentation, mock request/response stubbing and agreement purposes.
2. Monitoring of API using actuator framework for APM purposes.
3. Dockerize the API's and using DevOps pipeline for build and deployement.
4. Perform static code analysis, DevSecOps checks, Automated integration test exceution within the DevOps pipeline.
5. Usage of industry standard logging tool as Splunk or ELK.
6. API Gateway usage for exposing the API over the internet.
7. Usage of service discovery i.e eureka as well as service load balacing i.e zuul usage.  
8. Usage of  the circuit breaker i.e hysterix for complying with SAGA pattern implementation.
9. CQRS and event driven design implementation following asynchronous design principle.
10. Inclusion of retrial mechansim in account transfer for failure management.
11. Environment specific property file externalization. 
12. Performing API benchmarking, performance testing in terms of TPS baseline.
13. Include API authentication and authorization capabilities using SAML or Auth2.0.
14. Perform vulnarability assessment of APIs.  
