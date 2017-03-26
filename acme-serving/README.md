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
Start the compiled openscoring server binary:

```bash
java -jar bin/server-executable-*.jar
```

Load the current model:

```bash
java -cp bin/client-executable-*.jar org.openscoring.client.Deployer \
  --model http://localhost:8080/openscoring/model/OccupancyDetection \
  --file src/main/resources/model.pmml
```

Try scoring an instance:

```bash
java -cp bin/client-executable-*.jar org.openscoring.client.Evaluator \
  --model http://localhost:8080/openscoring/model/OccupancyDetection \
  -XTemperature=20.89 \
  -XHumidity=27 \
  -XLight=14 \
  -XCO2=1427 \
  -XHumidityRatio=0.00412229841538944
```

The output gives a probability for "0" (not occupied) and "1" (occupied). You can see that
the model believes it's about 15% likely that this room is occupied.

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