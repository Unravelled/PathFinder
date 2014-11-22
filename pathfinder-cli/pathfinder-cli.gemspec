# coding: utf-8
lib = File.expand_path('../lib', __FILE__)
$LOAD_PATH.unshift(lib) unless $LOAD_PATH.include?(lib)
require 'pathfinder/cli/version'

Gem::Specification.new do |spec|
  spec.name          = "pathfinder-cli"
  spec.version       = Pathfinder::Cli::VERSION
  spec.authors       = ["cimi", "gregsexton", "robochuck84"]
  spec.email         = ["alex.ciminian@gmail.com"]
  spec.summary       = %q{Command-line utilities for PathFinder.}
  spec.description   = %q{Collection of command line utilities to interact with the PathFinder code index.}
  spec.homepage      = "https://github.com/Unravelled/PathFinder"
  spec.license       = "Apache 2.0"

  spec.files         = `git ls-files -z`.split("\x0") # `find .`.split("\n")
  spec.executables   = spec.files.grep(%r{^bin/}) { |f| File.basename(f) }
  spec.test_files    = spec.files.grep(%r{^(test|spec|features)/})
  spec.require_paths = ["lib"]

  spec.add_runtime_dependency "git"

  spec.add_development_dependency "bundler", "~> 1.6"
  spec.add_development_dependency('rdoc')
  spec.add_development_dependency('aruba')
  spec.add_development_dependency('rake')
  spec.add_dependency('methadone', '~> 1.8.0')
end
