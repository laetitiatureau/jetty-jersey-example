
Compile and run with:

```
mvn clean compile exec:java
```

You can also just run `mvn clean package` and then start the jar file with `java -jar xxx.jar`.


The index page is at `http://localhost:8080`. This will load `src/main/resource/static/index.html`
which contains a bit of javascript to call the web service.

The webservice is at `http://localhost:8080/pages`. It supports the following operations.

- GET `/pages`: get all pages and their state
- GET `/pages/foo`: get page foo and its state
- PUT `/pages/bar`: activate page bar
- DELETE `/pages/baz`: deactivate page baz

Page names are configured in `src/main/resources/app.properties`

## Work in progress

- Extend unit tests for DefaultPageService
- Implement file reading and writing in DefaultPageService
- Extend javascript to display current page status
- Extend javascript to call PUT and DELETE operations
