# scientific-workflows-managerapp
Runtime application for the ActionManager, DatasetManager and DecisionManager provided in the scientific-workflow project.

## Instructions of how to use it.
Be sure to have Apache Maven and git installed.  

Also, you will need to have a configuration file with all the configurations ready, and the environment variable SW_CONFIGURATION_FILE set to the path of the configuration file.

Configuration file example:
```
decisionmanager.usageratio=0.1
workflows.definition.maxFolderSize=256
mongodb.host=52.39.159.33
mongodb.port=27017
nameNode=hdfs://ec2-52-43-33-190.us-west-2.compute.amazonaws.com:8020
jobTracker=ec2-52-43-33-190.us-west-2.compute.amazonaws.com:8032
managedFolder=/workflows
oozie.base.url=http://ec2-52-43-33-190.us-west-2.compute.amazonaws.com:11000/oozie
```

Environment variable:
```
export SW_CONFIGURATION_FILE="/home/ec2-user/scientific-workflows-conf/workflows.prop"
```

=~/Documents/workspace/scientific-workflows-conf/workflows.prop
Follow the following sequence of commands:

```
git clone https://github.com/jadielam/scientific-workflows
git clone https://github.com/jadielam/scientific-workflows-managerapp
cd scientific-workflows
mvn clean install
cd ../scientific-workflows-manager-app
mvn clean compile assembly:single
cd target
java -cp scientific-workflows-managerapp-0.0.1-SNAPSHOT-jar-with-dependencies.jar io.biblia.workflows.app.App

```
