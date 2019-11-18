# Exam Enterprise 

TODO: Tomorrow: 
- [ ] write gql-tests 
- [ ] amqp for updating available tickets in movies 
- [ ] amqp for general welcome-mail to new user
- [ ] Fix integration tests 
- [ ] calls from frontend 
    - [ ] movies 
    - [ ] GraphQL

## Extras 
* I have added styling with [Semantic UI](https://semantic-ui.com). This required adding css-loaders to webpack.config.js.
* Added Swagger documentation and wrapped responses on authentication API
* The project is running on Travis-CI

## Assumptions
* The `movies/lb_id`-endpoint does not serve any purpose other than testing load balancing in e2e-test. 
My assumption is that this is fine in an exam, as I want to show that load-balancing is working.  
* I have removed the basic-auth setup that was shown during class, as it caused an annoying login-popup in the browser. 
My assumption is that this is OK, as login with body is still supported.
 
- [X] Load balancing between three instances of same service
- [X] end-to-end-tests
- [X] custom exception handling 
- [X] GraphQL 
- [ ] Stoette XML paa et par endepunkt med content negiotiation
- [ ] Generify controllers
- [X] full set of REST-methods in API 
- [X] Wrapped Responses on everything 
- [ ] Test data 
- [X] Pagination
- [X] full set of REST-methods in API
- [ ] AMQP-kommunikasjon mellom to services
    - [ ] Generisk mail-server som man kan nå med amqp  
- [X] Authorization
- [X] Bruke Authorization paa API 
- [ ] AMPQL 
- [X] building frontend auth
- [ ] fetch more data on frontend / post
