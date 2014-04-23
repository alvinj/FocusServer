#!/bin/sh

curl \
--request GET \
--cookie PLAY_SESSION="21f606025a43af47c6404d15224ed206813e1742-username=alvin&authenticated=yes&uuid=29b56dfd-0b40-4f12-b16d-9faf909395cc" \
http://localhost:8080/projects

