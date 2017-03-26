# Modeling Lifecycle with ACME Occupancy Detection and Cloudera

Data science is more than just modeling. The complete data science lifecycle also includes data
engineering and model deployment. This project offers a simplified yet credible example of 
all three elements, as implemented using [Apache Spark](http://spark.apache.org), 
[Cloudera's Data Science Workbench](https://www.cloudera.com/products/data-science-and-engineering/data-science-workbench.html),
and [JPMML / OpenScoring](https://github.com/openscoring/openscoring).

In this project, the ACME corporation is productionizing a connected-house platform. Part of this
service requires predicting the occupancy of a room given sensor readings.

This example project includes simplified examples of:

- Data Engineering
  - Ingest
  - Cleaning
- Data Science
  - Modeling
  - Tuning and evaluation
- Model Serving
  - Model management
  - Testing
  - REST API

## Requirements

- Cloudera Data Science Workbench
- CDH 5.10+ cluster
- [Spark 2 CSD](https://www.cloudera.com/downloads/spark2/2-0.html) for CDH
- [Apache Maven](http://maven.apache.org) 3.2+

## Get Started

To continue, review documentation for each of the three modules, which contains more information
about what it show and how to run it.

- [Data Engineering](acme-dataeng/) 
- [Data Science](acme-datasci/) 
- [Model Serving](acme-serving/) 


[![Build Status](https://travis-ci.org/srowen/cdsw-simple-serving.svg?branch=master)](https://travis-ci.org/srowen/cdsw-simple-serving)