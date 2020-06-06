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
* This step is the same as [Epsilon EMC-HTML](https://github.com/epsilonlabs/emc-html)

## Getter Examples

* All elements: *gsn.all* --> For accessing all GSN elements
* Specific type: gsn.goal --> Returns all goal elements
* `Types:` **goal, strategy, solution, context, assumption, justification, inference, evidence, assertedcontext**
* Element by ID: *gsn.G1* --> Returns G1 goal element
* Element content: *gsn.G1.content* --> Returns G1's content
* Element type: *gsn.G1.gsntype* --> Returns G1's type (Goal)
* Element (node) links: *gsn.G1.target, gsn.G1.source* --> Returns G1 element's all targeted or sourced link elements
* Element ID: *gsn.G1.id* --> Returns G1's id (G1)
* All links: *gsn.links* --> Returns all link elements
* All nodes: *gsn.nodes* --> Returns all node elements
* Get specific link element: *gsn.t_G3_s_J4* --> Returns link element with target: G3 and sourceL J4
* `PRINTING: gsn.C5.content.println():`

## Setter Examples

* Set element (node) content: *gsn.Sn5.content = "Example";*
* Set link element's source: *gsn.t_A12_s_G7.source = gsn.Sn7;*
* Set link element's target: *gsn.t_A12_s_G7.target = gsn.Sn7;*
* Set element's (node) gsn type: *gsn.S9.gsntype = "goal";* --> Changes element's type and assigns new id (last/highest)
