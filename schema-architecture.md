This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for the
Admin and Doctor dashboards, while REST APIs serve all other modules. The application interacts with two
databases—MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions).
All controllers route requests through a common service layer, which in turn delegates to the appropriate
repositories. MySQL uses JPA entities while MongoDB uses document models.

1.User accesses the AdminDashboard or Appointment pages through their web browser.

2.The request is routed to the appropriate Thymeleaf or REST controller based on the URL.

3.The controller processes the request and calls the corresponding method in the service layer.

4.The service layer executes business logic and communicates with the repository layer for data operations.

5.The repository interacts with the database to fetch, save, or update the required information.

6.The database returns the result to the repository, which passes it back up through the service layer to the controller.

7.The controller sends the final data to the Thymeleaf engine to render the view or returns it as a JSON response to the user.
