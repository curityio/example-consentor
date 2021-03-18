# Example Consentor

[![Quality](https://img.shields.io/badge/quality-experiment-red)](https://curity.io/resources/code-examples/status/)
[![Availability](https://img.shields.io/badge/availability-source-blue)](https://curity.io/resources/code-examples/status/)

A consentor that presents the value of a (prefix) scope in the oauth-token and asks the user to confirm the value.

This project provides an example of a consentor plugin for Curity Identity Server. The code shows how to configure and implement a custom consentor of type example-consentor. You can do basically anything in a plugin but this example is very basic and the resulting consentor will just show a pop-up for the user with information retrieved from the session created during login.

## System Requirements
Curity Identity Server 5.2.0 and its system requirements <https://developer.curity.io/docs/latest/system-admin-guide/system-requirements.html> .

## Building the Pluging
The source code is written entirely in Java. You will need Maven 3 and Java SDK 8 to be able to compile the plugin. 

You can build the plugin by issuing the command ``mvn package``. This will produce a JAR file in the ``target`` directory. Use this JAR file during installation.

## Installation
As of now there is no binary version of the plugin yet. Before installing compile the code and create a package (see above). The resulting JAR file and its dependencies are then copied in the directory `${IDSVR_HOME}/usr/share/plugins/consent.exampleconsentor` on each node, including the admin node. You can replace the plugin group `consent.exampleconsentor` with any other arbitrary name of your choice.
For a list of the dependencies and their versions, run ``mvn dependency:list``. Ensure that all of these are installed in
the plugin group; otherwise, they will not be accessible to this plugin and run-time errors will result.

## Creating the Example Consentor
The easiest way to configure a new Consentor is using the Curity admin UI.

- Go to the `Profiles` page. Select a profile of type Token Service Profile. We refer to it as the `Token Service Profile`. This is where the consentor instance should be created.
- Navigate lower in the `General` section and at the `Consentors` sub-section click the `New consentor` button.
- Once the pop up shows up, type a name/identifier for the consentor and select the type `Example`. Click `Next`.
- Enter the prefix of the scopes that will be presented to the user.
- Commit the configuration changes.

## Using the Example Consentor
Assign the Example Consentor to a client and make sure the client may use the scope defined in the configuration of the Example Consentor. When a user logs in using the client Curity Identity Server will ask for consent before issuing any tokens. 

- In `Token Service Profile` open `Clients` and select a client of your choice. We will use a client called `www`.
- Navigate to the section called `OAuth\OpenID Settings` and scroll to the `User Consent` part of the section.
- Enable `User Consent` and `Only Consentors` since we only want to use the custom consentor from this example and not the built-in screens.
- In the list of consentors add the consentor configured above.
- Commit the configuration changes.

The consentor is now ready. When a user logs in with the client Curity Identity Server will show a prompt asking the user for consent.

### Note
This code is just for demonstration. Its purpose is to show the basic features of a consent plugin and provide basic classes. It is by no means complete and has known limitations. For example, it is missing an opt-out workflow and lacks error handling, but it includes everything to get started with a consent plugin.

#### License

This plugin and its associated documentation is listed under the `Apache 2 license` <http://www.apache.org/licenses/LICENSE-2.0>.

#### More Information

Please visit **curity.io** <https://curity.io/> for more information about the **Curity Identity Server**.

*Copyright (C) 2021 Curity AB*.
