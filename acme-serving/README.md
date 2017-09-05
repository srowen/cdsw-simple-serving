# Overview

This module represents the work of deployment engineers, who maintain model serving APIs 
and related infrastructure

# Running

## Creating a Model

The data scientists have provided code in `acme-datasci` that constructs a model. Here,
that code is run on a cluster in order to create a final model. This model is then exported
as an MLeap bundle for deployment.

Check out this code on a cluster edge node or other location that can build this code
and then run it on the cluster. Run `mvn package` at the top level to create a runnable 
Spark application.

From this module root directory, run:

```bash
spark2-submit --master yarn --deploy-mode client target/acme-serving-1.0.0.jar
```

This should update the model in `src/main/resources/model.zip`. This updated model could
be checked in via `git` then. This offers a basic strategy for model versioning.

## Testing the model

Because the `model.zip` file is versioned with the project, it can be used in automated
tests that are executed by a continuous integration system like 
[https://travis-ci.org/](https://travis-ci.org/). This means that the resulting model can
be verified automatically.

It can also be tested locally with simply `mvn test` at the top level.

## Deploying the Model

Given an Mleap bundle for the model, it's easy to deploy it as a REST API with
the [mleap-serving module](https://github.com/combust/mleap/tree/master/mleap-serving).
Start the compiled mleap server binary in the background:

```bash
tar -xzf bin/mleap-serving-0.7.0.tgz
mleap-serving-0.7.0/bin/mleap-serving
```

Load the current model using `curl` and the JSON API:

```bash
curl -XPUT -H "content-type: application/json" \
 -d '{"path":"src/main/resources/model.zip"}' \
 http://localhost:65327/model
```

Try scoring an instance:

```bash
  curl -XPOST -H "accept: application/json" -H "content-type: application/json" -d '{
    "schema": {
      "fields": [{
        "name": "Temperature",
        "type": "double"
      }, {
        "name": "Humidity",
        "type": "double"
      }, {
        "name": "Light",
        "type": "double"
      }, {
        "name": "CO2",
        "type": "double"
      }, {
        "name": "HumidityRatio",
        "type": "double"
      }]
    },
    "rows": [[20.89, 27, 14.0, 1427, 0.004122]]
  }' http://localhost:65327/transform
```

The output gives a probability for "0" (not occupied) and "1" (occupied). You can see that
the model believes it's about 95% likely that this room is unoccupied.

# Data

The data used here is originally distributed at 
http://archive.ics.uci.edu/ml/datasets/Occupancy+Detection+#

Citation:

Accurate occupancy detection of an office room from light, temperature, humidity and CO2 
measurements using statistical learning models. Luis M. Candanedo, Véronique Feldheim. 
Energy and Buildings. Volume 112, 15 January 2016, Pages 28-39.

# MLeap

The compiled copy of `mleap-serving` is compiled from
[source code](https://github.com/combust/mleap).
It is licensed under the Apache 2.0 license, a copy of which is
[available from the project](https://github.com/combust/mleap/blob/master/LICENSE) and also
included here as `mleap/mleap-LICENSE.txt`.