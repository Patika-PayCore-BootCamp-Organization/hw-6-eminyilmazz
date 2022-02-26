##About

---

&nbsp;&nbsp;&nbsp;&nbsp;This is Spring Boot project is developed for PayCore Java Spring Bootcamp by Patika.dev.  
Soul purpose of this project is to test my knowledge, research, and overcome my weaknesses.  

&nbsp;&nbsp;&nbsp;&nbsp;This is a backend service for loan application. 
It is designed to expose RESTful API sticking to the standards as much as possible.

####Tech Stack:
1. Java 8
2. Spring Boot 2.6.3
3. Spring Data JPA
4. PostgreSQL
5. Maven
6. RabbitMQ
7. Swagger

**Tech preferences**  

&nbsp;&nbsp;&nbsp;&nbsp;**RabbitMQ:** is a commonly used AMQP message broker. I preferred it
due to its simplicity and compatibility in both small and bigger scale. It is used to simulate an SMS Service that is supposedly
called on loan approval.  
  
&nbsp;&nbsp;&nbsp;&nbsp;**PostgreSQL:** PostgreSQL is a powerful, open source object-relational database system.
Being object-relational served better for my purposes and dependencies over other databases.  

&nbsp;&nbsp;&nbsp;&nbsp;**Maven:** Maven is a build automation, project management and comprehension tool. Maven is prefered
over other technologies for its enhanced dependency and plugin management.

&nbsp;&nbsp;&nbsp;&nbsp;**Spring Data JPA:** This Spring technology is used with hibernate ORM to build a simple persistance layer
quickly.  

&nbsp;&nbsp;&nbsp;&nbsp;**Swagger:** Swagger is used for API documentation following OpenAPI 3 specifications.

&nbsp;&nbsp;&nbsp;&nbsp;Architecture of the service is also done in n-layered design. This helped to achieve separation of components
such as service, repository, controller, model.

##How To Run and Requirements

---

**Required drivers and services:**
1. PostgreSQL - Developed in v14, is not tested for other versions.
2. RabbitMQ 3.0.0 or above.
3. Apache Maven 3.6.0 or above.

**Run:**

1. Configure data resource in application.properties
2. Configure RabbitMQ in application.properties unless default settings won't be used.
3. Navigate to the project files, and run ```mvn clean install```
4. Then run ```mvn spring-boot:run```

&nbsp;&nbsp;&nbsp;&nbsp;The server will run on localhost:8080 by default and PostgreSQL will have dummy data from dummy.sql.  
&nbsp;&nbsp;&nbsp;&nbsp;If you don't want to run with maven cmd commands, open the project as a maven project with your IDE.  
&nbsp;&nbsp;&nbsp;&nbsp;Don't forget to run it with maven clean install.

##Diagrams

---
**UML Diagram of components of the program.**


![application_uml](.\loanapplication_uml.png)

###Database Schema

---

![database_schema](.\database_schema.png)

##Improvements

---
* On service level, there are some unnecessary cross dependencies. In the future, I plan to optimize it
to have better and more isolated services for Loan and Customer.

* Some tests are not completely relevant. In controller integration tests, some lists of loans only compared as
JSON Strings. This is a go around solution for serialization / deserialization and comprassion issues with date data type. 



##Other

---

As I previously mentioned, this is a project I tried to learn with and experiment what I learnt. Any suggestions, feedbacks are always welcome. 
If you want to reach out to me for any reason, you can contact me on [Github](https://github.com/eminyilmazz) as well as [Linked.in](https://www.linkedin.com/in/eminyilmz/) and Discord @ Emin#4100.