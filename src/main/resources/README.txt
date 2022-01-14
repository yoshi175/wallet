For the service to be production-ready,
    - A security layer need to be added.
    - The choice of database changed to a non-in-memory database, for example MySQL.
    - Some optimization of what data is required to be accepted and sent upon all http requests.
    (I had to guess, since there is no specification requirement included. For example, the response data for calling
    the transaction history for a player, might be too much or redundant data sent over.)


A guide to get the application up and running, and how to use it.

Optional properties to be configured in the application.yaml file before usage of the application.

Data persistence configuration for the H2 in-memory database.
Set the corresponding property by desire.
There is two options:
1.  Default setting - The persisted data gets flushed upon application restart.
        url: jdbc:h2:mem:walletdb

2.  The persisted data gets stored into a file on your system and read upon application restart, the file path can be changed.
        For Mac and other OS (I think) -> url: jdbc:h2:file:/data/walletdb
        For Windows -> url: jdbc:h2:file:C:/data/walletdb

Sample data population configuration for the H2 in-memory database.
There is two options:
1.  Default setting - If the Data persistence option 1 is chosen. Then there is a 'data.sql' file in the 'resources'
    folder that can be used to populate sample data into the H2 in-memory database, by having the
    'defer-datasource-initialization' property set to 'true'. Having the property set to 'false' will result in not
    having the sample data populated, (the sql script is then executed in an earlier state, and hibernate is overriding
    the sql script).

2.  If the Data persistence option 2 is chosen, then there is an alternative to populate the database with sample data
    by calling the 'localhost:8080/populate-the-h2-database-with-sample-data' post endpoint (hardcoded data). Or, calling the
    'localhost:8080/add-currencies' post endpoint that will only persist a few currencies into the database and then use
    the 'localhost:8080/player/create' post endpoint to create players.


Once the application is ready for use.

API Documentation
    To access and see the API documentation, start the service and visit 'localhost:8080/api-doc'.

Database UI
    To access and see the H2 in-memory database UI, start the service and visit 'localhost:8080/h2'. You will be prompt
    with a login dialog window that also accepts additional database connection settings. Use the following settings:
    -Saved Settings: Generic H2 (Embedded)
    -Setting Name: Generic H2 (Embedded)
    -Driver Class: org.h2.Driver
    -JDBC URL: jdbc:h2:mem:walletdb         (Default. Need to match the selection of the data persistence configuration property)
    -User Name: sa
    -Password:                              (Empty)



