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
* if the server starts supporting it, wrap the file contents in the appropriate metadata
* maybe add optional filters to exclude certain files from indexing
* colors!
