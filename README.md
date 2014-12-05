java-bandwidth-examples
=======================

Bandwidth Application Platform exposes two access mechanisms:

- API
- BAML (Bandwidth Markup Language)

Under api-examples folder, you can find servlet based app showing how to use the Bandwidth SDK to implement an event
server using Java servlets, HelloFlipperServlet and ChaosCorpServlet (in progress).

And under xml-examples folder, you can find servlet based app that shows how to xml can be generated
in order to implement and application based on BAML.

In each of the folder, you have README.md indicating all the requirements and what is needed
for the applications to run.

Building the Example apps

First we want to clone the repo and build the app

	git clone 	https://github.com/bandwidthcom/java-bandwidth-examples.git
	mvn clean install

Two separate and independent war files will be generated for project:

api-examples
xml-examples

Those war are deployed separately depending on what you need to test,
you must cd to the specific folder and read specific README file before deploy.
