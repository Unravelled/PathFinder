module Pathfinder
  module Cli
    class Utils
      include Methadone::CLILogging

      def self.clean_directory!(tmpdir)
        info("Clearing #{tmpdir}")
        FileUtils.rm_rf(tmpdir)
        Dir.mkdir tmpdir
      end

      def self.clone_project(github_project, tmpdir)
        author, project_name = github_project.split('/')
        parent_dir = "#{tmpdir}#{author}"
        clone_dir = "#{tmpdir}#{github_project}/"
        Dir.mkdir parent_dir

        github_url = "http://github.com/#{github_project}.git"
        info("Cloning #{github_url} into #{clone_dir}")

        g = Git.clone(github_url, project_name, :path => parent_dir)
        info("Clone complete")
        clone_dir
      end

      def self.upload_file(project_name, file_name, file_content)
        pathfinder_resource = "http://localhost:9400/projects/#{project_name}/#{file_name}"
        debug("Indexing #{pathfinder_resource}")
        header = { 'Content-Type' => 'text/plain'}
        req = Net::HTTP::Put.new(pathfinder_resource, initheader = header)
        req.body = file_content
        Net::HTTP.new("localhost", 9400).start {|http| http.request(req) }
      end

      def self.upload_all_from(project_name, target_dir)
        info("Sending #{project_name} files for indexing from #{target_dir}")
        Dir.glob(target_dir + '**/*').each do |file_name|
          next if File.directory?(file_name) # skip over directories
          file_content = File.read(file_name)
          file_name.slice!(target_dir)
          response = upload_file(project_name, file_name, file_content)
          if response.code != "200"
            warn "HTTP #{response.code} received for #{file_name}, was not indexed!"
            debug "Response message was: #{response.message}"
            # TODO: the response body is currently empty in the case of faliures
            debug "Response body was:\n#{response.body()}"
          else
            debug "Successfully indexed #{file_name}"
          end
          response
        end
      end
    end
  end
end
