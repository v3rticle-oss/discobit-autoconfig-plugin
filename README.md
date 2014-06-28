discobit-jenkins-plugin-autoconfig
==================================


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
This plugin creates templated configurations via the discoBit API and returns configuration UUIDs.
