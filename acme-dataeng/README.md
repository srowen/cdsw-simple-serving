# Overview

This module represents the work of the data engineers. Data engineers are responsible for
ingesting, transforming, securing and possibly cleaning data, and making it available for analysis.

In this instance, the data set is a small, simple open data set containing several sensor readings
about a room, like its temperature and humidity. It also includes a flag to indicate whether the
room is occupied. This input will become the basis of a classifier model that predicts whether
a room is occupied.

The data needs to be ingested first, and will need a little cleaning before it's useful.

# Ingest

In order to prepare an Apache Hadoop environment for use with this example, check out
this project in a location that has access to the cluster, such as an edge node.

Run `hdfs dfs -put -f data/datatraining.txt /tmp` on an edge node, or
within the Terminal provided by Cloudera Data Science Workbench.
It can also be uploaded easily from a local file using Hue.
It will create a training data set on HDFS that the code in this 
module expects to be in place. This represents a (very simple) ingest process.

# Running

The source code here uses Spark via its Scala API to do some basic data cleaning in order to fix
a problem in the source, and then expose a cleaned `DataFrame` for analysis.

This code could be packaged and run on a cluster, but, really it is source code intended to be used
by data scientists in their exploratory sessions running on the cluster. That is, this module
really produces a bit of library code to be consumed in `acme-datasci`.

Normally, this might be managed by publishing compiled artifacts to a private Maven repository,
and then handling dependencies using this standard mechanism. For simplicity, this module actually
just "publishes" a compiled `.jar` file to the `lib/` directory in this project, where it's 
checked into source control.

Run `mvn package` at the top level to create a binary artifact from this module that can be used by
data scientists, using CDSW, in the `acme-datasci` module.

# Data

The data used here is originally distributed at 
http://archive.ics.uci.edu/ml/datasets/Occupancy+Detection+#

Citation:

Accurate occupancy detection of an office room from light, temperature, humidity and CO2 
measurements using statistical learning models. Luis M. Candanedo, Véronique Feldheim. 
Energy and Buildings. Volume 112, 15 January 2016, Pages 28-39.
