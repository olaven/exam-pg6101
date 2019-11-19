# Exam Enterprise [![Build Status](https://travis-ci.com/olaven/exam-pg6101.svg?token=zTzVh5wrqM89cpyf9qVd&branch=master)](https://travis-ci.com/olaven/exam-pg6101)

## Checklists

### Startup checklist 
- [ ] Go through entire task, setting up main checklist 
- [ ] Think about the schema -> keep it simple

### Main checklist
#### E mark
#### General
- [ ] Endpoints 
    - [ ] GET
    - [ ] POST
    - [ ] PUT
    - [ ] PATCH (JSON merge patch)
    - [ ] DELETE
- [ ] Wrapped Responses
- [ ] Pagination 
- Swagger Doc
- [ ] One test per endpoint 
- [ ] Usingp posgres in production 
- [ ] Flyway 
##### Application
- [ ] API for user details (bruk custom security funksjon) 
- [ ] Friendship requests
- [ ] Messages on a users timeline 
- [ ] Providing default data
#### D mark
##### General
- [ ] single entrypoint is gateway 
- [ ] whole app started with docker compose 
- [ ] e2e _for each_ REST API
##### Application 
nothing.
#### C mark 
##### General
- [ ] Provide a frontend
- [ ] all major features in app is executable from frontend
##### Application 
- [ ] GUI
    - [ ] Search and display users
    - [ ] Register a new user 
    - [ ] See details of other users 
    - [ ] Post on other users timeline
    - [ ] Display messages sorted by time (sort in DB!)
    - [ ] Create and accept friend requests 
#### B mark 
##### General
- [ ] Security protecting REST API
- [ ] Security is session based, through redis
- [ ] frontend can signup/sign in user
##### Application 
- [ ] Login/logout -> handled by other API (write assumption about this being auth )
- [ ] Logged in user should see a welcome message
- [ ] A user can create message on their own timeline
    - [ ] A test to verify this 
- [ ] A user  should see timelines of friends (in separate view -> assumption?)
    - [ ] A test to verify this 
#### A mark 
##### General
- [ ] Provide graphQL endpoint 
    - [ ] at least one mutation
    - [ ] at least one query
- [ ] Communication between two services, with AMQP
##### Application 
- [ ] GraphQL should handle advertisements
- [ ] Display advertisements on home-page
- [ ] Accepted requests shoud send a message to GQL, 
- [ ] This information should change what type of advertisement 
the user receives

    
### Delivery checklist
- [ ] Make sure that only gateway is exposed on `./docker-compose.yml`
- [ ] Make sure that jpa is always set to `validate`
- [ ] Run project with `mvn clean verify`
- [ ] run `mvn clean`
- [ ] remove `node_modules`
- [ ] ensure that `target`/`node_modules` is removed 
- [ ] zip project folder to `.zip`
- [ ] ensure that folder size is < 10MB
- [ ] rename folder to pg6100_ID_FROM_WIZEFLOW.zip


## Documentation

### About the Application 

### Test information 
Some users are added by default: 
* username: "admin", password, "admin" (is administrator)
* username: "adam", password, "adampass"
* username: "charliepass", password, "charliepass" 

### Extras 
* I have added styling with [Semantic UI](https://semantic-ui.com). This required adding css-loaders to webpack.config.js.
* Added Swagger documentation and wrapped responses on authentication API
* The project is running on Travis-CI

### Assumptions
* The `movies/lb_id`-endpoint does not serve any purpose other than testing load balancing in e2e-test. 
My assumption is that this is fine in an exam, as I want to show that load-balancing is working.  
* I have removed the basic-auth setup that was shown during class, as it caused an annoying login-popup in the browser. 
My assumption is that this is OK, as login with body is still supported.
 
