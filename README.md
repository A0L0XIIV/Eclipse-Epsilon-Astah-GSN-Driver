# Eclipse-Epsilon-Astah-GSN-Driver
Eclipse Epsilon Astah GSN Driver with EMC-XMI integration

* [Eclipse Epsilon](https://www.eclipse.org/epsilon/) is a Model-Driven Engineering tool
* GSN: [Goal Structuring Notation](https://modeling-languages.com/goal-structuring-notation-introduction/)
* [Astah GSN](https://astah.net/products/astah-gsn/)
* Only works with Astah GSN models
* Use Astah GSN XMI export tool to get XMI version of the models
* Only works with `XMI` files, not `AGML` files
* This Epsilon EMC integration driver is a heavily modified version of [Epsilon Plain-XML Driver](https://www.eclipse.org/epsilon/doc/articles/plain-xml/).

## How to Run
* Install the latest interim version of Epsilon or clone its Eclipse Epsilon Git repo and import all projects under /plugins to your Eclipse workspace
* Clone this Git repo
* Import the `org.eclipse.epsilon.emc.astahgsn` and `org.eclipse.epsilon.emc.astahgsn.dt` projects into your workspace
* Run the main method of the `GsnModel` class in `org.eclipse.epsilon.emc.astahgsn` **or**
* Start a nested Eclipse instance by selecting the `org.eclipse.epsilon.emc.astahgsn project` and clicking `Run As -> Eclipse Application`
* This step is the same as [Epsilon EMC-HTML Integration](https://github.com/epsilonlabs/emc-html)

## Element Types

* `Node Types:` **goal, strategy, solution, context, assumption, justification**
* `Link Types:` **inference (or assertedinference), evidence (or assertedevidence), assertedcontext**
* These types are same for getters, setters, and new element creator

## EOL Examples (Epsilon Object Language)

### Getter Examples

* All elements: `gsn.all` --> *For accessing all GSN elements*
* Specific type: `gsn.goal` --> *Returns all goal elements*
* Element by ID: `gsn.G1` --> *Returns G1 goal element*
  * *gsn.goal.G1.content is not correct access, use gsn.G1*
  * *If the custom ID has characters other than numbers or letters, remove special characters to access the element by ID. For instance, ID: R-CA1 --> use `gsn.RCA1`*
* Element content: `gsn.G1.content` --> *Returns G1's content*
* Element type: `gsn.G1.gsntype` --> *Returns G1's type (Goal)*
* Element xmi:id : `gsn.G1.xmiid` OR `gsn.G1.xmi_id` --> *Returns G1's xmi:id attribute. It's unique for every element*
* Element xsi:type: `gsn.G1.xsitype` OR `gsn.g1.xsi_type` --> *Returns G1's xsi:type attribute (ARM:Claim)*
* Element ID: `gsn.G1.id` --> *Returns G1's id (G1)*
* All links: `gsn.links` --> *Returns all link elements*
* All nodes: `gsn.nodes` --> *Returns all node elements*
* Get specific link element: 
  * `gsn.t_C3_s_G4` --> *Returns link element with target: C3 and source: G4*
  * `gsn.s_G5_t_Sn1` --> *Returns link element with source: G5 and target: Sn1*
* Target/Source for link elements:
  * `gsn.t_C3_s_G4.target` --> *Returns the node element targeted by the given link element (C3)*
  * `gsn.t_c3_s_g4.source` --> *Returns the given link element's source node (G4)*
* Target/Source for node elements:
  * `gsn.G1.target` --> *Returns all link elements ending in G1*
  * `gsn.G1.source` --> *Returns all link elements starting from G1*
* Get first or last element: `gsn.all.last, gsn.solution.first` --> Returns last element of the GSN and the second one returns first goal element
* Both last and first methods are right and returns same results:
  * `gsn.goal.last.content.println();` --> *Returns last goal element's content*
  * `gsn.goal.content.last.println();` --> *Returns goal content sequence's (list) last content*
* `PRINTING: gsn.C5.content.println();`
* `NOTE:` *If element ID has non-alpha-numerical characters such as -+, don't use these characters in the query. Only use letters and digits in the ID. For example: for ID E-CA1 --> use gsn.ECA1*

### Setter Examples

* Set element (node) content: `gsn.Sn5.content = "Example";`
* Set element id: `gsn.Sn5.id = "Sn14";` --> *MUST BE UNIQUE*
* Set element xmi:id : `gsn.Sn5.xmiid = "_fvLpH5q4Eeqyz11T9RpXrQ";` --> *MUST BE UNIQUE*
* Set element xsi:type: `gsn.Sn5.xsitpye = "ARM:ArgumentReasoning";` --> *MUST MATCH THE ID TYPE (G3 = ARM:Claim)*
* Set link element's source: `gsn.t_A12_s_G7.source = gsn.Sn7;`
* Set link element's target: `gsn.t_A12_s_G7.target = gsn.Sn7;`
* Set element's (node) gsn type: `gsn.S9.gsntype = "goal";` --> *Changes element's type and assigns new id (last/highest)*

### Creating a New Element

* Unless you know what are you doing, I do NOT recommend creating a new element via Epsilon. Because every element requires UNIQUE xmi:id value and Astah GSN generates these IDs with location and document values. This driver cannot create unique ID's for new elements; therefore, creating a new element via Epsilon might cause ERRORS in Astah GSN scheme or model.
* Element creater generates 'argumentElement' tag with given type xsi:type, xmi:id (Type prefix + MustBeUnique, e.g. GMustBeUnique), id (type prefix, e.g. G) and empty content, description attributes. User has to update xmi:id attribute according to other elements' xmi:ids.
* Create a new goal element: `var newElement = new goal;`
* Setting up the new element attributes: `newElement.content = "New Goal Element Content"`
* Appeding new element into model: `gsn.all.append = newElement;` --> *If new element's id attribute doens't have any digits (e.g. G), driver will automatically assign new ID number to the new element. This ID number is going to be the last id + 1 (e.g. G20, new ID = G21)*
* Updating the new element: `gsn.goal.last.content = "New Goal Element Content!"`
* OR get the last element: `gsn.all.last.content = "New Goal Element Content!"`
* OR get new element's ID and use it update element: `gsn.all.last.id.println();`

### Deleting an Element

* Deleting an element: `delete gsn.G10;`

## EVL Examples (Epsilon Validation Language)

```
context gsn {
	constraint Example1 {
		check: self.g1.target.size() == 2
		message: "Goal " + self.g1.id + " must have exactly 2 outgoing targets!"
	}
}

context goal {
	constraint Example2 {
		check: not self.content.contains("")
		message: "Goals must have non-empty content!"
	}
}
```

## EGL Examples (Epsilon Code Generation Language)

* EGX script to run EGL:
```
pre { "Transformation starting".println(); }
rule AstahGSN2HTML
	transform gsn : GSN {
		template : "YourEGLFileName.egl"
		target : "output.html"
	}
post { "Transformation finished".println(); }
```

* EGL code for generating Goal HTML tables:
```
<table border="1">
	<tr><td>Goal ID</td><td>Content</td></tr>
	[%for (g in gsn.goal.sortBy(g|g.id)){%]
	<tr>
		<td>[%=g.id%]</td>
		<td>[%=g.content%]</td>
	</tr>
	[%}%]
</table>
```