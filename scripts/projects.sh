#!/bin/sh

curl \
--request GET \
--cookie PLAY_SESSION="b350f072db8268c6bb0ecb451d3e8a427c9095c9-username=alvin&authenticated=yes&uuid=c1ec2b89-8230-4f1c-ac46-e8de75ba4ddf" \
http://localhost:8080/projects

