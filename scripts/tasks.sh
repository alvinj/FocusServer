#!/bin/sh

curl \
--request GET \
--cookie PLAY_SESSION="01799bc6775946f3ffd93d7fc61f05357855f2cf-username=alvin&authenticated=yes&uuid=22203c3e-4dab-4133-8d25-dca67ae17310" \
http://localhost:8080/tasks?projectId=1

