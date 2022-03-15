# Urban Observatory WebSocket Stream (via Monix)

This project creates a web socket connection to the [Urban Observatory](http://www.urbanobservatory.ac.uk/) consuming an unbounded stream of data.
The stream is filtered by metric name where potential metric include:

* CO 
* NO2 
* NO 
* O3 
* Particle Count 
* PM1 
* PM10 
* PM2.5 
* PM 4 


It then applies a statistical [Kriging](https://en.wikipedia.org/wiki/Kriging) algorithm to the stream and creates a websocket server for the frontend application.

## Prerequisites
You will need Scala installed on your local environment.

## Running the application
`sbt run`

Open in a browser the following url:

`http://localhost:7000/`

Select a metric then hit plot.

## Authors

* Andy Hardy -  [email](mailto:andrew.hardy@newcastle.ac.uk)