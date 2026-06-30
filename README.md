**Swiss Train Timetable Application**

A Java application reproducing the main functionalities of the Swiss Federal Railways (CFF/SBB) timetable application.

The project was developed from scratch as part of my Computer Science studies at EPFL. The train timetable data for Switzerland was provided, while the application architecture, route-search logic, data processing, and graphical interface were implemented as part of the project.

**Overview**

The application allows users to search for train connections between Swiss stations and view the corresponding journey details.

**Features**

* Search for connections between two Swiss train stations
* Select a departure date and time
* Display available train journeys
* View departure and arrival times
* View transfers and intermediate connections
* Graphical user interface built with JavaFX
* Processing of Swiss public-transport timetable data

**Technologies**

* Java
* JavaFX
* Object-oriented programming
* Java collections and hash maps
* Advanced generics
* Design patterns
* Unit testing
* Gradle

**Project Structure**

The application is divided into separate components responsible for:

* importing and processing timetable data;
* representing stations, routes, stops, and journeys;
* calculating connections;
* managing user input;
* displaying results through the JavaFX interface.

This structure separates the data-processing logic from the graphical interface and makes the application easier to test and maintain.

**Route Optimisation**

The application uses a Pareto-front approach to identify efficient train connections according to several criteria, such as arrival time, departure time, and the number of transfers.

Rather than selecting a single connection based on only one criterion, the algorithm keeps connections that are not dominated by another alternative. A connection is considered dominated when another journey is at least as good in every relevant criterion and strictly better in at least one.

This approach makes it possible to present users with several meaningful alternatives, for example:

* the fastest connection;
* a connection with fewer transfers;
* a later departure with a similar arrival time.

Using Pareto optimisation helped model the trade-offs involved in real timetable searches and avoid discarding useful journey options too early.
