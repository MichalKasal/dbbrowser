# Demo application - Database Browser
Task assignment for an interview:

Implement backend for saving, updating, listing and deleting connection details to you favourite
relational database. Connection details themselves should be stored in database of your choice.

Design and implement REST API for browsing structure and data using your stored database connections (listing of schemas, tables, columns, data preview of table). 

Design and implement REST API endpoints for statistics - Single endpoint for statistics about each column: min, max, avg, median value of the column.

Single endpoint for statistics about each table: number of records, number of attributes.

Document this REST API.

Instructions:
Build as any Spring Boot Maven project. 
Tests need docker in order to run.
Application needs PostgreSQL database, connection may be configured in application.properties
