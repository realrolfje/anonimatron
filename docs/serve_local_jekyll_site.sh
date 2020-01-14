#!/bin/bash
#
# From https://help.github.com/en/articles/setting-up-your-github-pages-site-locally-with-jekyll#requirements
#
# Install the ruby bundler:
# sudo ruby install bundler
#
# Install jekyll locally (as local user, in the current directory):
# bundle install --path ruby/bundle
#
# Serve jekyll pages locally:
bundle exec jekyll serve
open http://127.0.0.1:4000
