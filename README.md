# EOBALY web crawler

PDS Proj 2018
Spring boot web application which parses eshop data, and then visualizes the amount and price of the items over time.
The front end is created using [Thymeleaf](https://www.thymeleaf.org/). 

Author: David Kozak

Contact: dkozak94@gmail.com

## Dependencies:
* java 8 runtime
* maven
* mysql database

## Setup
1) provide credentials to access database in /src/main/resources/application.properties
2) run with mvn  spring-boot:run
3) open your browser and go to localhost:8080
4) login with credentials user user
    
## Known issues
* parsing single product does not persist the product with all dependencies
* parsing fails for a couple of products - see Log
* no pagination
* cannot limit number of threads to use for the parsing
* problems with Czech encoding     
