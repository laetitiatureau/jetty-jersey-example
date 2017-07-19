[![Build Status](https://travis-ci.org/aski/jetty-jersey-example.svg?branch=master)](https://travis-ci.org/aski/jetty-jersey-example)[![Coverage Status](https://coveralls.io/repos/github/aski/jetty-jersey-example/badge.svg?branch=master)](https://coveralls.io/github/aski/jetty-jersey-example?branch=master)


This project consists of two parts:
1. **backend:** a java application (jax-rs, [jersey 2][2]) with embedded grizzly web server 
2. **frontend:** a webpage (javascript, [vuejs 2][1])

In order to run the backend server run the following commands:


```
cd backend
mvn clean install
mvn exec:java

```

This will start a server on port `8080`. To interact with the rest api, point
your browser at `http://localhost:8080/api/pages`

The api supports the following operations.

- GET `/api/pages`: get all pages and their state
- GET `/api/pages/foo`: get page foo and its state
- PUT `/api/pages/bar`: activate page bar
- DELETE `/api/pages/baz`: deactivate page baz

To start the frontend application you need `nodejs` and `npm` installed on
you system. Once that's installed, run the following commands:

```
cd frontend
npm install
npm run dev

```

This will start a server on port `9090` that will serve the `vuejs`
application which will talk to the backend server running on port `8080`.
Trying editing any of the files in the `src` directory and the page will
reload automatically.

To bundle the backend and the frontend together into one jar file for 
deployment, run the following commands from the root directory:

```
mvn clean install

```

The self-contained jar file will be put into `bundle/target`.

Start it with:

```
java -Dpages=foo,bar,baz -jar <jar-file>

```

You can customize the following config settings:
 - `pages`     -- comma separated list of page names (mandatory)
 - `workdir`   -- directory for the datastore - (optional, default: tmp folder -
                files will be deleted on shutdown)
 - `http.port` -- which port to run on          (optional, default: 8080)
 - `http.uri`  -- which interfaces to listen on (optional, default: http://0.0.0.0)
 - `http.cors` -- whether grizzly should allow cross-site requests: 
                this is useful during development where the frontend
                might run in on a webpack dev server
 - `webroot`   -- path to the static web resources, i.e. html
                and js files (optional, default: read web resources from classpath/jar file)
 - `webcache`  -- switch of grizzly's file cache by setting this to 'false' - slower
                but useful during development
                
 You should at least define `pages` and `workdir`. `webroot` and `webcache`
 and `http.cors` should be left undefined.
 
 [1]: https://vuejs.org/
 [2]: https://jersey.github.io/
