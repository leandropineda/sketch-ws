# sketch-ws

This project implements data streaming techniques to detect anomalies 
on streams of data.
Given a series of events the system detects those known as _heavy keys_: events whose frequency, on a short period of time is higher than a certain threshold value, and those events whose frequency difference exceeds yet another threshold value.

## How to configure the service
There are a few configurations you may want to change before running the service. See more here: [config.yml](https://github.com/leandropineda/sketch-ws/blob/master/config.yml).

A brief description of what you'll find in there:

* `sketchConfig`: used by the `sketchFactory` when creating new _sketches_. Keep in mind `prime` must be greater than `rows`.
* `detectionParameters`: here you can change _heavy keys_ thresholds and time window length.
* `database`: redis server address.

## How to run the service
The system can by started by using the `docker-compose` file located on [sketch-ws/ci/compose/](https://github.com/leandropineda/sketch-ws/tree/master/ci/compose):

```
$ docker-compose up
```

You will see how the service spins up. Available resources will be shown on the console:

```
GET     /health (com.lpineda.dsketch.resources.Health)
GET     /heavykeys (com.lpineda.dsketch.resources.HeavyKeysHistoryResource)
GET     /heavykeys/heavychangers (com.lpineda.dsketch.resources.HeavyKeysHistoryResource)
GET     /heavykeys/heavyhitters (com.lpineda.dsketch.resources.HeavyKeysHistoryResource)
GET     /status (com.lpineda.dsketch.resources.Status)
```
You can start submitting events to the service by submitting `POST` request with an `application-json` body to `http://localhost:8080/event`:

```
{
	"event": "a_event"
}
```
To compile the code and generate Docker images, use `sketch-ws/ci/build_docker_image.sh`

### Using jar file
If you want to compile on your local machine, you can use the script `sketch-ws/ci/build_maven_package.sh`. Once it is compiled, you'll find the `jar` file on `target/sketchws-1.0-SNAPSHOT.jar`. To start the service, run the command:
```
$ java -jar sketchws-1.0-SNAPSHOT.jar server ../config.yml
```
You will need a Redis database running locally:
```
$ docker run --rm --name redis -p 6379:6379 redis
```
---

More at: [https://github.com/leandropineda/proyecto-final-de-carrera](https://github.com/leandropineda/proyecto-final-de-carrera) (spanish)