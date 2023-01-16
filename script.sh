#!/bin/zsh
#!/bin/bash

# Configure URL to the GRA client HTTP server (the "gateway")
GRA_GATEWAY=http://localhost:7890

# normal-user.sh - Typical dating app user
curl -X POST -H "userId: abc123" -H "session: null" -H "ip_address: 1.2.3.4"    $GRA_GATEWAY/login                                  # The user opens the app and login. This returns the session that will be used for all following requests.
sleep 0.2
curl -X POST -H "userId: abc123" -H "session: abcdef" -H "ip_address: 1.2.3.4"  $GRA_GATEWAY/config                                 # The app automatically fetches the app configuration
sleep 0.2
curl -X POST -H "userId: abc123" -H "session: abcdef" -H "ip_address: 1.2.3.4"  $GRA_GATEWAY/search/nearby-users                    # The user lands on the start screen, in this example nearby users.
sleep 0.2
curl -X POST -H "userId: abc123" -H "session: abcdef" -H "ip_address: 1.2.3.4"  $GRA_GATEWAY/profiles/qwerty123                     # The user found a user in the search result that was interesting and opens the profile page
sleep 0.2
curl -X POST -H "userId: abc123" -H "session: abcdef" -H "ip_address: 1.2.3.4"  $GRA_GATEWAY/profile/qwerty123/record-profile-view  # The app automatically records a profile view, this usually sends some kind of notification to the viewed user
sleep 0.2
curl -X POST -H "userId: abc123" -H "session: abcdef" -H "ip_address: 1.2.3.4"  $GRA_GATEWAY/send-message/qwerty123                 # The user decides to send the user a message
sleep 0.2
                                                                                                                                    # The User closes the qwerty123 profile page and goes back to the search result. No API request for this since this is entierly client side.
curl -X POST -H "userId: abc123" -H "session: abcdef" -H "ip_address: 1.2.3.4"  $GRA_GATEWAY/profiles/asdf123                       # The user opens another profile from the same search result
sleep 0.2
curl -X POST -H "userId: abc123" -H "session: abcdef" -H "ip_address: 1.2.3.4"  $GRA_GATEWAY/profile/qwerty123/record-profile-view  # Profile view is automatically recorded
sleep 0.2                                                                                                                                    # The profile was not interesting, so the user closes it.

curl -X POST -H "userId: abc123" -H "session: null" -H "ip_address: 1.2.3.4"    $GRA_GATEWAY/logout                                  # Log out
sleep 0.2


# spammer-bot.sh - Typical spammer bot. Should get blocked
curl -X POST -H "userId: abc123" -H "session: null" -H "ip_address: 2.2.3.4"    $GRA_GATEWAY/login                                  # The bot starts
sleep 0.2
while true
do
  curl -X POST -H "userId: abc123" -H "session: abcdef" -H "ip_address: 2.2.3.4"  $GRA_GATEWAY/search/nearby-users                    # Get a list of users to spam, page through all users in the system.
sleep 0.2
  # For all users in the returned search result, spam them with messages
  curl -X POST -H "userId: abc123" -H "session: abcdef" -H "ip_address: 2.2.3.4"  $GRA_GATEWAY/send-message/user1                     # Please click my very spammy link I am sending you
  sleep 0.2
  curl -X POST -H "userId: abc123" -H "session: abcdef" -H "ip_address: 2.2.3.4"  $GRA_GATEWAY/send-message/user2                     # Please click my very spammy link I am sending you
  sleep 0.2
  curl -X POST -H "userId: abc123" -H "session: abcdef" -H "ip_address: 2.2.3.4"  $GRA_GATEWAY/send-message/user3                     # Please click my very spammy link I am sending you
  sleep 0.2
  curl -X POST -H "userId: abc123" -H "session: abcdef" -H "ip_address: 2.2.3.4"  $GRA_GATEWAY/send-message/user4                     # Please click my very spammy link I am sending you
  sleep 0.2
done # ... Repeat forever



# If you want to capture the HTTP status code returned you can do this
RETURN_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST -H "userId: abc123" -H "session: abcdef" -H "ip_address: 1.2.3.4"  $GRA_GATEWAY/some/path)
echo "HTTP return code: $RETURN_CODE"
