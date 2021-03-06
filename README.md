# Game Catalog in Hexagonal Architecture
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://travis-ci.org/cjcalmeida/hexagonal-architecture.svg?branch=master)](https://travis-ci.org/cjcalmeida/hexagonal-architecture)
[![Codecov](https://codecov.io/gh/cjcalmeida/hexagonal-architecture/branch/master/graph/badge.svg)](https://codecov.io/gh/cjcalmeida/hexagonal-architecture)

Sample project that implements a Create, List and Search operations of 
Game Catalog System Admin using Hexagonal Architecture Pattern.

This Pattern was proposed by Alistair Cockburn, to learning more
see [Alistair Cockburn in the Hexagone](https://www.youtube.com/watch?v=th4AgBcrEHA)

The main goal of this repository, is show in easy way how organize your 
application to use this Architectural Pattern while use Spring Boot 
Framework. 

## Architecture Overview

````
       +------------------------------+
       |  +------+  +-----+  +----+   |
       |  | Rest |  | WEB |  | WS |   |   Primary Adapters
       |  +------+  +-----+  +----+   |
       +------|--------|--------|-----+
              V        V        V
       +------------------------------+
       |  +------------------------+  |
       |  |         UseCase        |  |
       |  +----|--------------|----+  |
       |       V              V       |
       |  +----------+   +---------+  |
       |  | Business | > |  Model  |  |    Domain
       |  +----|-----+   +---------+  |
       |       V                      |
       |  +------------------------+  |
       |  |     RepositoryPort     |  |
       |  +------------------------+  |
       +---------------|--------------+
                       V              
    +-----------------------------------+
    |  +----------+  +------+  +-----+  | 
    |  | InMemory |  | JDBC |  | JPA |  | Secondary Adapters
    |  +----------+  +------+  +-----+  |
    +-----------------------------------+
     
````

**Obs 1**: *Configuration Package only exists to Spring Configuration (Bean Creation, Web Config and WS Config)*
**Obs 2**: *JPA Package only contains classes to detach JPA Entity (Table) from Domain Model (Core Business)
 and to use Spring Data JPA features (JpaRepositories)* 

## How to use 
This project has many Spring profiles that active some adapters, 
the list of supported profiles are:

**Primary Adapters (These profiles can be used together)**

At least one must be active to interact with application

- *ws* - This profile enable SOAP Adapter
- *web* - This profile enable WEB Site Adapter
- *api* - This profile enable Rest API Adapter

**Secondary Adapters (Only one profile at a time)**
  
- *jpa* - This profile enable JPA as Repository Adapter
- *jdbc* - This profile enable JDBC as Repository Adapter
- *inmemory* - This profile enable InMemory strategy as Repository Adapter 
(**default**)
 
### Running Locally

Follow these steps to run locally:

#### Direct way
````
mvn spring-boot:run -Dspring-boot.run.profiles=web,api,ws,jpa
````
#### Local build
**Build**
````sh
mvn clean package
````
**Run**
````sh
java -jar target/hexagonal-architecture-0.0.2.jar --spring.profiles.active=web,api,ws,jpa
````

*You can use any profile as described in the How to Use section*

### Entry Points
Each profile enable a entry point to interact with application, 
in **web** profile you can download projects to test REST and SOAP service
in Postman and SOAP-UI, respectively.

**WEB** - [http://localhost:8080/](http://localhost:8080/)

**REST** - [http://localhost:8080/api/game](http://localhost:8080/api/game)

**SOAP** - [http://localhost:8080/ws/Game?wsdl](http://localhost:8080/ws/Game?wsdl)

**Aditional Entry Point**

To see in-memory DB, when *jpa* or *jdbc* profiles are active, 
you can use the follow endpoint:

**H2** - [http://localhost:8080/h2](http://localhost:8080/h2)