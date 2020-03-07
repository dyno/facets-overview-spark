# Facets Overview Spark

## Architecture Overview

The [facets-overview](https://github.com/PAIR-code/facets/blob/master/facets_overview/README.md) is consists of

* Feature Statistics Protocol Buffer
* Feature Statistics Generation
* Visualization

This project provides an additional implementation for Statistics generation.
We can use Spark to leverage the spark generate stats with distributed computinig capability

![Overview visualization with Spark](src/main/images/facets_overview_spark.png)

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
