#!/bin/zsh

#run using Git Bash promt: sh normal-user.se

# Configure URL to the GRA client HTTP server (the "gateway")
GRA_GATEWAY=http://localhost:8888

# normal-user.sh - Typical dating app user
curl -X POST -H "userId: abc123" -H "session: null" -H "ip_address: 1.2.3.4" -H "RequestMethod: GET" -H "RequestURL: https://GRAProxy.com/catfact.ninja/breeds" -H "Content-Type: application/x-www-form-url-encoded" -d grant_type=client_credentials    $GRA_GATEWAY/catfact.ninja/breeds                                  # The user opens the app and login. This returns the session that will be used for all following requests.
sleep 0.2



# If you want to capture the HTTP status code returned you can do this
RETURN_BODY=$(curl -sb -H "userId: abc123" -H "session: abcdef" -H "ip_address: 1.2.3.4" -H "RequestMethod: GET" -H "RequestURL: https://GRAProxy.com/catfact.ninja/breeds"  $GRA_GATEWAY/some/path)
echo "HTTP return body: $RETURN_BODY"
