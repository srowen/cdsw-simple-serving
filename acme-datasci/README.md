# Overview

This module represents the work of data scientist, who explore data and then tune and
create a predictive model. The task here is to create a classifier model that will predict
room occupancy given the data set made available in `acme-dataeng`.

The data scientists will work in Cloudera's Data Science Workbench. Although this work will
also use Scala and Spark, executing code in a shell or notebook-like tool is somewhat different
than writing code to be compiled

# Setup

In the workbench, create a new Git project from this repository on github.com: 
`https://github.com/srowen/cdsw-simple-serving`. Once the project
is open, navigate to this directory, `acme-datasci`. Start a Spark Scala engine; 2 vCPU and
4GB will work fine.

# Running

Although the source code can be compiled by Maven and opened in an IDE, it's written with a
notebook like the workbench in mind. The source code executed by the workbench need not fall 
into a directory structure or even class declaration. To try to accommodate both this usage
and the standards of a Maven build, the data scientist works in the file 
[src/main/scala/acme/ACMEModel.scala](src/main/scala/acme/ACMEModel.scala). This file has a 
simplistic wrapper declaration but most of the code inside is intended to be executed as a block
in the workbench.

Once this file is open in the workbench, select the code between `// Start workbench session`
and `// End workbench session` and press command-Enter. It will execute. A data scientist
could change the code, create a different model, and then check in the result using `git`

The result is, however, compilable Scala code that can be reused by a deployment module,
`acme-serving`. It can be tested and evaluated by a continuous integration system like with
any software because it also follows a standard Maven build structure.