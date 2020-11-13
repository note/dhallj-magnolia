#!/usr/bin/env bash

if [  [ "$TRAVIS_PULL_REQUEST" == "false" ]]; then
  openssl aes-256-cbc -K $encrypted_f7a2d53f3383_key -iv $encrypted_f7a2d53f3383_iv -in secrets.tar.enc -out secrets.tar -d
  tar xvf secrets.tar
fi
