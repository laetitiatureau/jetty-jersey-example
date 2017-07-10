Run `mvn clean package` and then start server with:
 
 ```
 java -Dpages=foo,bar,baz [...] -jar <jar-file>
 ```

You can customize the following config settings:
 - pages     -- comma separated list of page names (mandatory)
 - workdir   -- directory for the datastore - (optional, default: tmp folder -
                files will be deleted on shutdown)
 - http.port -- which port to run on          (optional, default: 8080)
 - http.uri  -- which interfaces to listen on (optional, default: http://0.0.0.0)


You can also start the server with with maven:

```
mvn clean compile exec:java
```

but this is only useful during development.
 

The index page is at `http://localhost:8080`. This will load `src/main/resource/static/index.html`
which contains a bit of javascript to call the web service.

The webservice is at `http://localhost:8080/app/pages`. It supports the following operations.

- GET `/app/pages`: get all pages and their state
- GET `/app/pages/foo`: get page foo and its state
- PUT `/app/pages/bar`: activate page bar
- DELETE `/app/pages/baz`: deactivate page baz

Page names are configured in `src/main/resources/app.properties`

## Work in progress

- Extend unit tests for DefaultPageService
- Implement file reading and writing in DefaultPageService
- Extend javascript to display current page status
- Extend javascript to call PUT and DELETE operations
