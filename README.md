PathFinder
==========

## Starting up the elasticsearch docker container

First, make sure you have docker installed. For Ubuntu this should be as straightforward as installing an aptitude package. For OS X please see the boot2docker instructions below. Then do:

    export DOCKER_HOST=tcp://127.0.0.1:2375

or, if you're on OS X:

    export "DOCKER_HOST=tcp://`boot2docker ip`:2375"

You should also add a host alias to your hosts file:

    echo "$DOCKER_HOST docker" | sudo tee --append /etc/hosts

To build the elasticsearch image and register it as pathfinder/elasticsearch run the following command from the directory containing the Dockerfile:

    docker build -t pathfinder/elasticsearch .

To start the container and expose the 9200 and 9300 ports do:

    docker run --name elasticsearch -d -p 9200:9200 -p 9300:9300 pathfinder/elasticsearch

Now the following links should work for you:

* [http://docker:9200/_plugin/kopf](http://docker:9200/_plugin/kopf) - cluster management
* [http://docker:9200/_plugin/kibana](http://docker:9200/_plugin/kibana) - cluster data visualization
* [http://docker:2375/containers/json](http://docker:2375/containers/json) - list of running containers

### OSX specific instructions

Download the [boot2docker OSX installer](https://github.com/boot2docker/osx-installer/releases) and run it. Alternatively you can use homebrew (untested):

    brew install docker boot2docker

After the installation is complete, to enable directory sharing between the host and the boot2docker VM do the following:

```
# make sure the vm is stopped
boot2docker stop
# enables sharing directories between the host and the boot2docker VM
wget -O ~/.boot2docker/boot2docker.iso http://static.dockerfiles.io/boot2docker-v1.1.2-virtualbox-guest-additions-v4.3.12.iso
VBoxManage sharedfolder add boot2docker-vm -name home -hostpath /Users
# now to test it, you should see the directories you have in /Users on the host
boot2docker up && boot2docker ssh "ls /Users"
