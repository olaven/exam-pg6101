# Exam Enterprise [![Build Status](https://travis-ci.com/olaven/exam-pg6101.svg?token=zTzVh5wrqM89cpyf9qVd&branch=master)](https://travis-ci.com/olaven/exam-pg6101)

## Documentation

### About the Application 
This aplication models a social network akin to Facebook. 
From the home-page, users may go to their profiles. 
They may also search for other users, and make friends with them. 
If they are friends, they may see each others timelines. 

### Test information 
Some users are added by default: 
* username: "admin@mail.com", password, "admin" (is administrator)
* username: "adam@mail.com", password, "adam"
* username: "charlie@mail.com", password, "charlie" 

### General notes
The end-to-end tests fails sometimes, due to limited system resources
on my machine/docker and the fact that there are a lot of services.
I have addressed this issue by increasing the time they take to run.
I have also given Docker more memory. 
If the integration tests fails due to a timeout, I would greatly appreachiate
if this could be kept in mind.

### Assumptions
* The `movies/lb_id`-endpoint does not serve any purpose other than testing load balancing in e2e-test. 
My assumption is that this is fine in an exam, as I want to show that load-balancing is working.
* To authorize users, I am using `Authentication` as an argument to endpoint-functions instead of the method-based
approach shown in class. This is because it gave me easy and readable access to the dto-object. My assumption is 
that this is OK because the same functionality is achieved, and it is still achieved with Spring Security. 
* I have removed the basic-auth setup that was shown during class, as it caused an annoying login-popup in the browser. 
My assumption is that this is OK, as login with body is still supported.
* The assignment states that there should be an API to handle user details. Since this is a requirement before B-grades/security,
I am assuming that this API should be a separate one, and not part of an auth-service. I.e. I am assuming that it 
should have the same role that the movies-API did in the mock-exam.
* In my JSON Merge Patch implementation, I am allowing the user to enter null-values for `.givenName` and `.familyName`. 
This is redundant, as it will result in a `ConstraintViolation` (i.e. 400). I have chosen to implement it anyway, 
for the sake of showing how JSON Merge Patch treats null values. 
* The assignment specifies "messages". As an E requirement (C in frontend) a user should be able to 
create a message for _some user_. It is not clear to me wether the "current user" is the logged in user 
or the displayed user. However, in B requirement, it is specified that a user should only be able to 
create a user for his/her own timeline. As B is a later requirements, I am considering that the valid 
interpretation. As a result, I am assuming that a message should only be sent by the user "owning" the timeline. 
* TODO: assumption about package/verify
 
### Extras 
* I have added styling with [Semantic UI](https://semantic-ui.com). 
* Added Swagger documentation and wrapped responses on authentication API
* The project is running on Travis-CI
* There is a `mail`-service. This service receives a message through AMQP when 
a user is signed up. The message triggers a "welcome mail"-function that could, 
for example, send an email to the user. (`WelcomeMessageController.kt`)
* TODO: write about test coverage 


## Checklists

### Startup checklist 
- [X] Go through entire task, setting up main checklist 
- [X] Think about the schema -> keep it simple

### Main checklist
#### E mark
#### General
- [X] Endpoints 
    - [X] GET
    - [X] POST
    - [X] PUT
    - [X] PATCH (JSON merge patch)
    - [X] DELETE
- [X] Wrapped Responses
- [X] Pagination  
- [X] Swagger Doc
- [X] One test per endpoint 
- [X] Using postgres in production 
- [X] Flyway 
- [X] at least 70% test coverage

##### Application
- [X] API for user details (bruk custom security funksjon) 
- [X] Friendship requests
- [X] Messages on a users timeline 
- [X] Providing default data
#### D mark
##### General
- [X] single entrypoint is gateway 
- [X] whole app started with docker compose 
- [X] e2e _for each_ REST API 
##### Application 
nothing.
#### C mark 
##### General
- [X] Provide a frontend
- [X] all major features in app is executable from frontend
##### Application 
- [X] GUI
    - [X] Search and display users
    - [X] Register a new user 
    - [X] See details of other users 
    - [X] Post on  timeline
    - [X] Display messages sorted by time (sort in DB!)
    - [X] Create and accept friend requests 
#### B mark 
##### General
- [X] Security protecting REST API
- [X] Security is session based, through redis
- [X] frontend can signup/sign in user
##### Application 
- [X] Login/logout -> handled by other API 
- [X] Logged in user should see a welcome message
- [X] A user can create message on their own timeline
    - [X] A test to verify this -> `MessageControllerTest.kt`, specifically `403 if not sender`
- [X] Sikkerhet på timeline 
- [X] A user should see timelines of friends
    - [X] A test to verify this -> `UserControllerTest.kt`, specifically `can only see timeline of friends` 
#### A mark 
##### General
- [X] Provide graphQL endpoint 
    - [X] at least one mutation
    - [X] at least one query
- [X] Communication between two services, with AMQP
##### Application 
- [X] GraphQL should handle advertisements
- [X] Display advertisements on home-page
- [X] Accepted requests should send a message to GQL -> `FriendRequestController`, specifically `#133` 
- [X] This information should change what type of advertisement 
the user receives -> Add order will change if friends have email addresses starting with the same letter. 
This can be seen in `QueryResolver.kt`, specifically `receiveFromAMQP` 

    
### Delivery checklist
- [ ] Make sure that Swagger doc is available
- [ ] Make sure that only gateway is exposed on `./docker-compose.yml`
- [ ] Make sure that jpa is always set to `validate`
- [ ] Run project with `mvn clean verify`
- [ ] run `mvn clean`
- [ ] remove `node_modules`
- [ ] ensure that `target`/`node_modules` is removed
- [ ] remove `.git` 
- [ ] zip project folder to `.zip`
- [ ] ensure that folder size is < 10MB
- [ ] rename folder to pg6100_ID_FROM_WIZEFLOW.zip