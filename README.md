# Facets Overview Spark

## Architecture Overview

The [facets-overview](https://github.com/PAIR-code/facets/blob/master/facets_overview/README.md) is consists of

* Feature Statistics Protocol Buffer => [feature_statistics.proto](https://github.com/PAIR-code/facets/blob/master/facets_overview/proto/feature_statistics.proto)
* Feature Statistics Generation => _ generate stat with spark this project _
* Visualization => [facets-jupyter](https://github.com/PAIR-code/facets/tree/master/facets_overview#visualization)

This project provides an additional implementation for Statistics generation.
We can use Spark to leverage the spark generate stats with distributed computinig capability

![Overview visualization with Spark](src/main/images/facets_overview_spark.png)

The feature_statistics.proto was designed for TensorFlow and vectorized feature data (for deep learning?) in my view.
This project was initiated to adapt it to tabular data usally found to train decision tree models.
The assumption is the dataframes are already cleaned and only have [Categorical, Numerical](https://stats.idre.ucla.edu/other/mult-pkg/whatstat/what-is-the-difference-between-categorical-ordinal-and-numerical-variables/) data.

## Demo

```bash
# prepare the repository and python environment for notebook
make init

# produce build/libs/facets-overview-spark_2.11-0.2.0-SNAPSHOT.jar
gradle jar

# use the jar to generate ammonites/census_stats.txt
make -C ammonites demo_v2.sc

# start notebook server
make jupyter

# navigate to notebooks/facets.ipynb
```

## License
```
    Apache License 2.0
```
