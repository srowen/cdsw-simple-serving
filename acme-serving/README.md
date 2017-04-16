# Overview

This module represents the work of deployment engineers, who maintain model serving APIs 
and related infrastructure

# Running

## Creating a Model

The data scientists have provided code in `acme-datasci` that constructs a model. Here,
that code is run on a cluster in order to create a final model. This model is then exported
as PMML for deployment.

Check out this code on a cluster edge node or other location that can build this code
and then run it on the cluster. Run `mvn package` at the top level to create a runnable 
Spark application.

From this module root directory, run:

```bash
spark2-submit --master yarn --deploy-mode client target/acme-serving-1.0.0.jar
```

This should update the model in `src/main/models/model.pmml`. This updated model could
be checked in via `git` then. This offers a basic strategy for model versioning.

## Testing the model

Because the `model.pmml` file is versioned with the project, it can be used in automated
tests that are executed by a continuous integration system like 
[https://travis-ci.org/](https://travis-ci.org/). This means that the resulting model can
be verified automatically.

It can also be tested locally with simply `mvn test` at the top level.

## Deploying the Model

Given a PMML description of the model, it's easy to deploy it as a REST API with
[OpenScoring](https://github.com/openscoring/openscoring). 
Start the compiled openscoring server binary in the background, 
possibly choosing a less-common port like 8081 to run on:

```bash
java -jar bin/server-executable-*.jar --port 8081 &
```

Load the current model using `curl` and the JSON API:

```bash
curl -X PUT --data-binary @src/main/resources/model.pmml \
 -H "Content-type: text/xml" \
 http://localhost:8081/openscoring/model/OccupancyDetection
```

Try scoring an instance:

```bash
curl -X POST --data-binary \
  '{"arguments":{"Temperature":20.89, "Humidity":27, "Light":14, "CO2":1427, "HumidityRatio":0.004122}}' \
  -H "Content-type: application/json" \
  http://localhost:8081/openscoring/model/OccupancyDetection
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

# OpenScoring

The compiled copy of `openscoring-server` is compiled from 
[source code](https://github.com/openscoring/openscoring).
It is licensed under the GNU Affero GPL, a copy of which is 
[available from the project](https://github.com/openscoring/openscoring/blob/master/LICENSE.txt) and also
included here as `openscoring/openscoring-LICENSE.txt`.