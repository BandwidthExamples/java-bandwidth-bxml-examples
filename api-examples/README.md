java-bandwidth-api-examples
===========================

There are two examples of how to use the bandwidth sdk to implement an event server using Java servlets, HelloFlipperServlet and ChaosCorpServlet (in progress).

You'll need the following prereqs to run these:

You'll need a Bandwidth Application Platform account (https://catapult.inetwork.com/pages/signup.jsf)
You'll need Maven installed (http://maven.apache.org/download.cgi#Installation)
And, of course, you'll need the jdk installed (http://docs.oracle.com/javase/7/docs/webnotes/install/index.html)

Now we want to deploy it to a running web server. The steps to deploy to Heroku and AWS are included below.

Heroku Deployment

For the Heroku deployment, you'll first need to ensure that you have a Heroku account and that the Heroku toolbelt has been installed for your environment (https://devcenter.heroku.com/articles/heroku-command)

The steps to deploy the example app to Heroku are:

1. Set up the git repo 
2. Create a new Heroku app 
3. Configure the Heroku app
4. Push the project to Heroku 

Step 1 - Set up the git repo

From the java-bandwidth-examples directory
	
	git init
	git add .
	git commit -m "my bandwidth example"

Step 2 - Create a new Heroku app
	
	heroku apps:create
	git remote add heroku git@heroku.com:new-app.git

Step 3 - Configure the new Heroku app with your App Platform credentials

	heroku config:set BANDWIDTH_USER_ID='your user id from the app platform'
	heroku config:set BANDWIDTH_API_TOKEN='your api token from the app platform'
	heroku config:set BANDWIDTH_API_SECRET='your api secret from the app platform'

	heroku config:set BANDWIDTH_OUTGOING_NUMBER='the number you want the example app to call' (you can set this code if you prefer. Setting it here let's you deploy it as is, without changing code.)

Note that your Bandwidth user id, api token and api secret are obtained by logging into the app platform UI and going to the Account tab.

Step 4 - Push the project to heroku

	git push heroku master
	heroku ps:scale web=1

You can now verify that the app is 	successfully deployed - 
	
	heroku logs -tail --app <heroku-app-name> 

And verify the app is running 
	heroku 	open

To setup the phone number see the Routing the Phone Number section below.


AWS Installation


For the AWS installation, you'll need to have an AWS account and be signed into the AWS Management Console

The steps to deploy the example app to AWS EC2 are:

1. Create an AWS EC2 instance with Tomcat
2. Copy the example war file to the EC2 instance
3. ssh to the EC2 instance
4. Move the war to the Tomcat webapp directory
5. Set the environment variables with App Platform credentials
6. Restart tomcat
7. Verify that the app is successfully deploy

Step 1. Create an AWS instance with Tomcat installed. For this example we’ll use a Tomcat/Ubuntu instance by Openlogic (https://aws.amazon.com/marketplace/search/results/ref=srh_navgno_search_box?page=1&searchTerms=+openlogic+tomcat) 

Step 2. Once your instance is up copy the bandwidth-sdk-examples-1.0-SNAPSHOT.war from your local machine to the EC2 instance - 
	
	scp -i <key-pair-file>.pem <local path to examples dir>/java-bandwidth-examples/target/bandwidth-sdk-examples-1.0-SNAPSHOT.war ubuntu@<ec2-url>:bandwidth-sdk-examples-1.0-SNAPSHOT.war

Step 3. Log in to the EC2 instance (see AWS console for exact connect string)
	
	ssh -i <key-pair-file>.pem ubuntu@<ec2-url> 

Step 4. Move the bandwidth-sdk-examples-1.0-SNAPSHOT.war file to the tomcat webapps directory - 
	
	sudo mv bandwidth-sdk-examples-1.0-SNAPSHOT.war /var/lib/tomcat7/webapps

Step 5. Set environment variables for user id, api token and api secret (BANDWIDTH_USER_ID, BANDWIDTH_API_TOKEN and BANDWIDTH_API_SECRET) 

	vi setenv.sh (edit script to set env vars with user_id, token and secret)
	source setenv.sh (after copying it to the ec2 instance)

Step 6. Restart tomcat

	sudo /etc/init.d/tomcat7 restart

Step 7. Verify that the app is successfully deployed 
	
	tail -f /var/log/tomcat7/catalina.out 

Now Verify the app is running - in a browser type the url <path to AWS ec2 instance>/bandwidth-sdk-examples-1.0-SNAPSHOT

To setup the phone number see the Routing the Phone Number section below.


Routing the Phone Number


Now that the app is deployed, we want it to handle all inbound phone calls. To do that we'll want to:

 - Provision a phone number on the app platform
 - Create an Application and associate it with the phone number

- Provisioning a phone number on the app platform

 	Log in to the App Platform (https://catapult.inetwork.com/pages/login.jsf)
 	Go to the My Numbers tab and select Get New Numbers
 	Fill in the form for a number in your area and click the Search button
 	Select one of the numbers and click the Get Numbers button

- Creating an Application and associating the phone number with it

	Go to the My Apps tab and click the Create New link
	Fill in the name and the Call URL field, using the following:
		<heroku-app-name>.herokuapp.com/helloflipper?callLeg=incoming
	Click the Save button
	Select the number you provisioned
	Click the Add Numbers button

Now if you call the number, it will be routed to the demo app. You can trace the call flow in the log output of your app. 



