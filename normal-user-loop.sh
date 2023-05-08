#!/bin/zsh
#!/bin/bash



# Configure URL to the GRA client HTTP server (the "gateway")
GRA_GATEWAY=http://192.168.0.139:7890

# normal-user-loop.sh a normal user making continuous requests
curl -X GET -H "userId: abc122" -H "session: abcder" -H "ip_address: 1.2.3.5" -H "content-type: application/json" $GRA_GATEWAY/172.20.0.40:8081/login                                   # The bot starts
sleep 1
while true
do
  curl -X GET -H "userId: abc122" -H "session: abcder" -H "ip_address: 1.2.3.5" -H "content-type: application/json" "$GRA_GATEWAY/172.20.0.40:8081/search?keyword=123abc"                    # Get a list of users to spam, page through all users in the system.
  sleep 5
    # Do some things
  curl -X GET -H "userId: abc122" -H "session: abcder" -H "ip_address: 1.2.3.5" -H "content-type: application/json" "$GRA_GATEWAY/172.20.0.40:8081/profile?id=123abc"                     # Please click my very spammy link I am sending you
  sleep 3
  curl -X GET -H "userId: abc122" -H "session: abcder" -H "ip_address: 1.2.3.5" -H "content-type: application/json" $GRA_GATEWAY/172.20.0.40:8081/send/123abc                      # Please click my very spammy link I am sending you
  sleep 0.2
  curl -X GET -H "userId: abc122" -H "session: abcder" -H "ip_address: 1.2.3.5" -H "content-type: application/json" $GRA_GATEWAY/172.20.0.40:8081/send/123abc                     # Please click my very spammy link I am sending you
  sleep 0.2
  curl -X GET -H "userId: abc122" -H "session: abcder" -H "ip_address: 1.2.3.5" -H "content-type: application/json" $GRA_GATEWAY/172.20.0.40:8081/send/123abc                    # Please click my very spammy link I am sending you
  sleep 10
  curl -X GET -H "userId: abc122" -H "session: abcder" -H "ip_address: 1.2.3.5" -H "content-type: application/json" $GRA_GATEWAY/172.20.0.40:8081/profile/abc122
  sleep 7
  curl -X GET -H "userId: abc122" -H "session: abcder" -H "ip_address: 1.2.3.5" -H "content-type: application/json" $GRA_GATEWAY/172.20.0.40:8081/profile/abc122/upload
  sleep 10

done # ... Repeat forever



# If you want to capture the HTTP status code returned you can do this
RETURN_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST -H "userId: abc123" -H "session: abcdef" -H "ip_address: 1.2.3.4"  $GRA_GATEWAY/some/path)
echo "HTTP return code: $RETURN_CODE"
