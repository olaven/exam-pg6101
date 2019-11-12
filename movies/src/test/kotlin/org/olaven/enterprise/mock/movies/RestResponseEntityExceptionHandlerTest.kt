package org.olaven.enterprise.mock.movies

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.olaven.enterprise.mock.movies.controller.ControllerTestBase


internal class RestResponseEntityExceptionHandlerTest: ControllerTestBase() {


    /*
    * NOTE: This test is copied and slightly modified from:
    *https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/18f764c3123f60339ab98167790aa223641e7559/advanced/rest/exception-handling/src/test/kotlin/org/tsdes/advanced/rest/exceptionhandling/ExceptionHandlingApiTest.kt
    * */

    @Test
    fun `custom response on 404 not found`(){

        /*
            This needs special settings in application.properties to work
         */
        RestAssured.given().accept(ContentType.JSON)
                .get("/doesNotExist")
                .then()
                .statusCode(404)
                .body("code", Matchers.equalTo(404))
                .body("status", Matchers.equalToIgnoringCase(WrappedResponse.ResponseStatus.ERROR.toString()))
                .body("message", Matchers.containsString("No handler found"))
    }
}