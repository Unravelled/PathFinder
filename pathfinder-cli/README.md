# Pathfinder::Cli

Command line utilities for the PathFinder application.

## Installation

Prerequisites: ruby >= 1.9, rubygems.

    $ gem build pathfinder-cli.gemspec
    $ gem install pathfinder-cli-0.0.1.gem

If rubygems are on your path, then you should be able to run all the utilities from your shell.

## Development:

    $ bundle install                      # pulls in dependencies
    $ rake                                # runs tests
    $ bundle exec bin/<utility> [params]  # runs executable with workspace code

## Available Utilities

### pf-indexer

Utility that submits files from a GitHub repository to PathFinder. It creates a local clone of the repository you point it to, scrapes the clone directory and sends every file to PathFinder for indexing.

Usage examples:

    $ pf-indexer --help
    $ pf-indexer Unravelled/PathFinder  # indexes from GitHub if no URL is specified
    $ pf-indexer papers https://cimi@bitbucket.org/cimi/school.git

__NOTE:__ This assumes you have an instance of PathFinder running locally and implicitly an instance of elasticsearch running locally (the ES endpoint is currently hardcoded in PathFinder).

To start PathFinder, run:

    $ lein repl
    user=> (reset)

This will start an instance on port 9400.

To start elasticsearch, please follow the docker/boot2docker instructions. PathFinder is currently hardcoded to look for elasticsearch on `docker:9200`, so make sure you've aliased your docker ip in `/etc/hosts`.

TODOs:

* parametrise the PathFinder endpoint (currently hardcoded to `localhost:9400`)
* if the server starts supporting it, wrap file contents with metadata
* maybe add optional filters to exclude certain files from indexing
* colors! :)
