# Pathfinder::Cli

Command line utilities for the PathFinder application.

## Installation

Add this line to your application's Gemfile:

    gem 'pathfinder-cli'

And then execute:

    $ bundle

Or install it yourself as:

    $ gem install pathfinder-cli

## Available Utilities

### pf-indexer

Utility that submits files from a GitHub repository to PathFinder. It creates a local clone of the repository you point it to, scrapes the clone directory and sends every file to PathFinder for indexing.

TODOs:

* parametrise the PathFinder endpoint (currently hardcoded to localhost:9400)
* better error reporting for indexing failures (currently only says file x failed)
* maybe add optional filters to exclude certain files from indexing
* colors!
