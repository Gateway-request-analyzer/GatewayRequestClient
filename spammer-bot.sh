#!/bin/zsh
#!/bin/bash



# Configure URL to the GRA client HTTP server (the "gateway")
GRA_GATEWAY=http://localhost:7890

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
