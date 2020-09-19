REST API with Fuse
---
This REST API shows how to map business operations to a remote procedure call endpoint over HTTP by using a REST framework. 

It consumes a remote SOAP webservice, does little data transformation and then produce a REST endpoint.

Prerequisites
---
You need to run remote SOAP web service from [**soap-api-in-springboot** project](https://github.com/fsantagostinobietti/soap-api-in-springboot).

Run
---
```
$ mvn -Dspring-boot.run.profiles=dev spring-boot:run
```

Try it in your browser
---
Point your browser to ```http://localhost:8080```.

In this landing page you'll get links to invoke REST endpoint and swagger documentation.
