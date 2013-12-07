mongo-datasource-itests
=======================

Integration tests for moe repo
Prerequisites
=============

* [XAP](http://www.gigaspaces.com/xap)
  * [Download](http://www.gigaspaces.com/xap-download) (9.6 or later) and follow the [installation instructions](http://wiki.gigaspaces.com/wiki/display/XAP97/Installation)
  * Create environment variable `GS_HOME` = `XAP installation directory`
  * Add `GS_HOME\bin` to `PATH` environment variable
* [MongoDB](http://www.mongodb.org/)
  * [Download](http://www.mongodb.org/downloads) and follow the [installation instructions](http://docs.mongodb.org/manual/installation/)
  * Create environment variable `MONGO_HOME` = `mongo installtion directory`
  * Add `MONGO_HOME\bin` to `PATH` environment variable

Build
=====

* Clone the project: `git clone https://github.com/Gigaspaces/mongo-datasource-itests.git`
* Navigate to the `mongo-datasource-itests` project directory
* run integration tests `mvn install` 

> ##### Notes #####
> * its recommended that you run `mvn clean install` and then running the testing phases separately `mvn surefire:test integration-test` 
  because integration and system test can take long time
> * eclipse users uses m2e plugin sometimes miss synchronization its recommended to right click on 
  mongodb-datasource project and from the menu [Maven]-> [Update project]



Dependencies
============
    <dependency>
			<groupId>com.gigaspaces</groupId>
			<artifactId>mongo-datasource</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
		<dependency>
			<groupId>com.gigaspaces.quality</groupId>
			<artifactId>tf</artifactId>
			<version>1.5.0</version>
		</dependency>
