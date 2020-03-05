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
make init
gradle jar
make generate_demo_stats
make jupyter
# navigate to notebooks/facets.ipynb
```

## License
```
    Apache License 2.0
```
