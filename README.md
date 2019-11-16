# mock-exam-pg6101

## Extras 
* I have added styling with [Semantic UI](https://semantic-ui.com). This required adding css-loaders to webpack.config.js.
* Added Swagger documentation and wrapped responses on authentication API

## Assumptions
* The `movies/lb_id`-endpoint does not serve any purpose other than testing load balancing in e2e-test. 
My assumption is that this is fine in an exam, as I want to show that load-balancing is working.  
 
- [X] Load balancing between three instances of same service
- [X] end-to-end-tests
- [X] custom exception handling 
- [ ] GraphQL 
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
- [ ] building frontend 
