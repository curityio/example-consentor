# Example Consentor

[![Quality](https://img.shields.io/badge/quality-experiment-red)](https://curity.io/resources/code-examples/status/)
[![Availability](https://img.shields.io/badge/availability-source-blue)](https://curity.io/resources/code-examples/status/)

A Consentor that takes the value of a (prefix) scope, queries an API for details and presents a screen to the user for confirmation.

This project provides an example of a Consentor plugin for Curity Identity Server. The code shows how to configure and implement a custom Consentor of type `price-consent`. You can do basically anything in a plugin. 

In this example we show how you can call an API for getting data during an approval flow. We also show how to present this data to the user.
The resulting Consentor will use an id from the user's session and take the value to query an external API. We assume that the id is part of a prefix scope used by the client. The prefix in this example is called `transactionId`.

## System Requirements
Curity Identity Server 6.0.0 and its system requirements <https://developer.curity.io/docs/latest/system-admin-guide/system-requirements.html> .

## Building the Plugin
The source code is written entirely in Java. You will need Maven 3 and Java SDK 8 to be able to compile the plugin. 

You can build the plugin by issuing the command `mvn package`. This will produce a JAR file in the `target` directory. Use this JAR file during installation.

## Installation
As of now there is no binary version of the plugin yet. Before installing compile the code and create a package (see above). The resulting JAR file and its dependencies are then copied in the directory `${IDSVR_HOME}/usr/share/plugins/consent.exampleconsentor` on each node, including the admin node. You can replace the plugin group `consent.exampleconsentor` with any other arbitrary name of your choice.
For a list of the dependencies and their versions, run `mvn dependency:list`. Ensure that all of these are installed in the plugin group; otherwise, they will not be accessible to this plugin and run-time errors will result.

## Creating the Example Consentor
The easiest way to configure a new Consentor is using the Curity admin UI.

- Go to the `Profiles` page. Select a profile of type Token Service Profile. This is where the Consentor instance should be created.
- Navigate lower in the `General` section and at the `Consentors` sub-section click the `New consentor` button.
- Once the pop-up shows up, type a name/identifier for the Consentor and select the type `Price Consent`. Click `Next`.
- Enter the prefix of the scope for the transaction id that the client will request during login. The plugin will take the first scope that starts with the prefix and parse the value (suffix).
- Configure the API endpoint to query. In this example the query will look like `<protocol>://<hostname>:<port><path>?transactionId=<value from scope>` where `<value from scope>` is the suffix from the scope as described above. 
- Select an HTTP client that will be used for communicating with the API. The HTTP client defines among others the protocol to be used, i.e. HTTP or HTTPS.
- Commit the configuration changes.

![Create and update the Example Consentor](docs/images/update-example-consentor.png?raw=true "Update Example Consentor")

## Using the Example Consentor
Assign the Example Consentor to a client and make sure the client may use the scope defined in the configuration of the Example Consentor. When a user logs in using the client Curity Identity Server will ask for consent before issuing any tokens. 

- In `Token Service Profile` open `Clients` and select a client of your choice. We will use a client called `www`.
- Navigate to the section called `OAuth\OpenID Settings` and scroll to the `User Consent` part of the section.
- Enable `User Consent` and `Only Consentors` since we only want to use the custom Consentor from this example and not the built-in screens.
- In the list of Consentors add the Example Consentor configured above.
- Commit the configuration changes.

![Configure Client with Example Consentor](docs/images/configure-client-with-example-consentor.png?raw=true "Enable Consentors on Client")


The Consentor is now ready to be used. 

## Testing
When a user logs in with the client Curity Identity Server will show a prompt asking the user for consent. The text on the consent screen was retrieved from the external API. The screen will look similar to this one:

![Consent Screen](docs/images/example-consentor-screen.png?raw=true "Consent Screen")

## Under the Hood
### Assumptions
The plugin expects the API to support the query-parameter `transactionId`. The response from the API is a JSON array with the objects that match the query. The plugin then reads the first JSON object in the list. It takes the value of the attribute `total_price` and includes it in the confirmation screen. 

### Calling the API
 The request to the API looks similar to the following one but changes according to the configuration of the Consentor plugin:

```
  GET http://hostname:80/orders?transactionId=123
  
  HTTP/1.1 200 OK
  Content-Type: application/json; charset=utf-8 
  [
    {
      "id": 123,
      "item_count": 2,
      "total_price": 198,
      "status": "created",
      "transactionId": 123
    }
  ]
```

### Note
This code is just for demonstration. Its purpose is to show the basic features of a consent plugin and provide basic classes. It is by no means complete and has known limitations. For example, it is missing an opt-out workflow, lacks error handling and has hardcoded values, but it includes everything to get started with a Consentor plugin.

#### License

This plugin and its associated documentation is listed under the `Apache 2 license` <http://www.apache.org/licenses/LICENSE-2.0>.

#### More Information

Please visit **curity.io** <https://curity.io/> for more information about the **Curity Identity Server**.

*Copyright (C) 2021 Curity AB*.
