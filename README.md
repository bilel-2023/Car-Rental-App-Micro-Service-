Car rental project with spring boot micro service architecture and MySQL database
this application allows to users to register as Client and create locations for cars already added by agencies 
**technologies used 
JPA ,JWT , OpenFeign,Configserver , Discovery Client and service , kafka , Zipkin for monitoring , 


**guide 
microservices list:
-authservice:   handles signup(for clients) and login [kafka topic producer]
-servie-agence : handles agency reqeusts (managing cars and dashboard), Uses Zipkin for distributed tracing 
-service-client : handles rentals
-congifservice  :centralized server for configuration ( and auto push configurations to github when running the project)
-discovservice (eureka server that handles registration )
-kafka-dokcer to run containers with zoo keeper
-notificationservice as Kafka consumer (welcome message after successful registration )
-gatewayservice to route the different requests on the same port 
