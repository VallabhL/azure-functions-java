package com.realtime.azure.functions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.EventHubTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
	/**
	 * This function listens at endpoint "/api/HttpTrigger-Java". Two ways to invoke
	 * it using "curl" command in bash: 1. curl -d "HTTP Body" {your
	 * host}/api/HttpTrigger-Java&code={your function key} 2. curl "{your
	 * host}/api/HttpTrigger-Java?name=HTTP%20Query&code={your function key}"
	 * Function Key is not needed when running locally, it is used to invoke
	 * function deployed to Azure. More details:
	 * https://aka.ms/functions_authorization_keys
	 */
    @FunctionName("HttpTrigger-Java")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("name");
        String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }

	@FunctionName("event-hub-processor")
	public void eventHubProcessor(
			@EventHubTrigger(name = "message", eventHubName = "rteventhub", connection = "connectionURL") String message,
			final ExecutionContext context) {
		context.getLogger().info(message);

		URL obj;
		int responseCode = 0;
		HttpURLConnection con = null;
		try {
			obj = new URL("https://y53a2ca6hb.execute-api.us-east-1.amazonaws.com/dev/TestLambdaForREST");

			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			responseCode = con.getResponseCode();

			System.out.println("GET Response Code :: " + responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// print result
				System.out.println(response.toString());
			} else {
				System.out.println("GET request not working");
			}
		} catch (MalformedURLException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
