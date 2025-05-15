#!/bin/bash
if [ -f wiremock.pid ]; then
  if kill -0 $(cat wiremock.pid) 2>/dev/null; then
    kill $(cat wiremock.pid) && rm wiremock.pid
  else
    rm wiremock.pid
  fi
fi