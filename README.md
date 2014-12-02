discobit-autoconfig-plugin for Jenkins
======================================


discoBit - Remote Application Configuration Management
------------------------------------------------------------------
*discoBit is a service which enables remote configuration of applications.
Configurations are read from the central server during bootstrap or on demand by your software.*

#About
Instead of providing each installed application, script or process with local configuration you shift everything to a central instance which serves as a single point of truth. All properties are organized in configuration buckets which are uniquely identified and secured from unauthorized access.

Your software calls the central service cluster via REST and reads the applied properties when appropriate. You need to provide a single remote-configuration-ID or a unique identifier for the calling node (hostname, IP) and security credentials to access the remote configuration.

Apply changes to your configs for massive infrastructures without any need for rollouts or restarts.

See www.discobit.com

# Jenkins AutoConfig Plugin
This plugin pushes selected property files from the Jenkins workspace to the discoBit configuration repository. The repository/configuration is specified by a UUID. 

This repo is forked by JenkinsCI (https://github.com/jenkinsci/discobit-autoconfig-plugin) and will be maintained there in future.

# Installation from Jenkins
The plugin is release to the Jenkins Plugin Repository and can be retrieved directly within Jenkins:
https://wiki.jenkins-ci.org/display/JENKINS/discoBit+Autoconfig+Plugin
