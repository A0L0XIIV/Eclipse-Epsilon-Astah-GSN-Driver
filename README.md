# Eclipse-Epsilon-Astah-GSN-Driver
Eclipse Epsilon Astah GSN Driver with EMC-XMI integration

* GSN = Goal Structuring Notations
* Only works with Astah GSN models
* Use Astah GSN XMI export tool to get XMI version of the models
* Only works with `XMI` files, not `AGML` files

## How to Run
* Install the latest interim version of Epsilon or clone its Eclipse Epsilon Git repo and import all projects under /plugins to your Eclipse workspace
* Clone this Git repo
* Import the `org.eclipse.epsilon.emc.astahgsn` and `org.eclipse.epsilon.emc.astahgsn.dt` projects into your workspace
* Run the main method of the `GsnModel` class in `org.eclipse.epsilon.emc.astahgsn` **or**
* Start a nested Eclipse instance by selecting the `org.eclipse.epsilon.emc.astahgsn project` and clicking `Run As -> Eclipse Application`

## Examples

* gsn.all --> For accessing all GSN elements
* var main = gsn.all;
* main.goal --> Returns all goal elements
* main.goal.content --> Returns all goal elements' content
* main.G1 --> Returns G1 goal element
* main.G1.content --> Returns G1's content
* main.G1.gsntype --> Returns G1's type (Goal)
* main.G1.target, main.G1.source --> Returns G1 element's all target or source link elements
* main.G1.id --> Returns G1's id (G1)

* Currently Setters doesn't work but they are going to use the same principles
