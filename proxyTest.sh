#!/bin/zsh

#run using Git Bash promt: sh normal-user.se

# Configure URL to the GRA client HTTP server (the "gateway")
GRA_GATEWAY=http://10.0.8.100:7890

# normal-user.sh - Typical dating app user
curl -X GET -H "userId: abc122" -H "session: abcder" -H "ip_address: 1.2.3.5" -H "content-type: application/json" $GRA_GATEWAY/catfact.ninja/fact                                # The user opens the app and login. This returns the session that will be used for all following requests.
curl -X GET -H "userId: abc122" -H "session: abcder" -H "ip_address: 1.2.3.5" -H "content-type: application/json" $GRA_GATEWAY/catfact.ninja/fact                                # The user opens the app and login. This returns the session that will be used for all following requests.
curl -X GET -H "userId: abc122" -H "session: abcder" -H "ip_address: 1.2.3.5" -H "content-type: application/json" $GRA_GATEWAY/catfact.ninja/fact                                # The user opens the app and login. This returns the session that will be used for all following requests.
curl -X GET -H "userId: abc122" -H "session: abcder" -H "ip_address: 1.2.3.5" -H "content-type: application/json" $GRA_GATEWAY/catfact.ninja/fact                                # The user opens the app and login. This returns the session that will be used for all following requests.
curl -X GET -H "userId: abc122" -H "session: abcder" -H "ip_address: 1.2.3.5" -H "content-type: application/json" $GRA_GATEWAY/catfact.ninja/fact                                # The user opens the app and login. This returns the session that will be used for all following requests.


# If you want to capture the HTTP status code returned you can do this
#RETURN_BODY=$(curl -sb -H "userId: abc123" -H "session: abcdef" -H "ip_address: 1.2.3.4" -H "RequestMethod: GET" -H "RequestURL: https://GRAProxy.com/catfact.ninja/breeds"  $GRA_GATEWAY/some/path)
#echo "HTTP return body: $RETURN_BODY"
