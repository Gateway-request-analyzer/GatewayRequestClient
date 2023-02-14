#!/bin/bash

# Configure URL to the GRA client HTTP server (the "gateway")
GRA_GATEWAY=http://localhost:7890

# normal-user.sh - Typical dating app user
curl -X POST -H "userId: bbc123" -H "session: null" -H "ip_address: 3.2.3.4"    $GRA_GATEWAY/login                                  # The user opens the app and login. This returns the session that will be used for all following requests.
sleep 0.2
curl -X POST -H "userId: bbc123" -H "session: abcdef" -H "ip_address: 3.2.3.4"  $GRA_GATEWAY/config                                 # The app automatically fetches the app configuration
sleep 0.2
curl -X POST -H "userId: bbc123" -H "session: abcdef" -H "ip_address: 3.2.3.4"  $GRA_GATEWAY/search/nearby-users                    # The user lands on the start screen, in this example nearby users.
sleep 0.2
curl -X POST -H "userId: bbc123" -H "session: abcdef" -H "ip_address: 3.2.3.4"  $GRA_GATEWAY/profiles/qwerty123                     # The user found a user in the search result that was interesting and opens the profile page
sleep 0.2


# If you want to capture the HTTP status code returned you can do this
RETURN_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST -H "userId: abc123" -H "session: abcdef" -H "ip_address: 1.2.3.4"  $GRA_GATEWAY/some/path)
echo "HTTP return code: $RETURN_CODE"
