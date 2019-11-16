import org.olaven.enterprise.mock.grapqhql.GraphQLApplication
import org.springframework.boot.SpringApplication

/*
    API accessible at
    http://localhost:8080/graphql

    UI accessible at
    http://localhost:8080/graphiql
    (note the "i" between graph and ql...)

    UI graph representation at
    http://localhost:8080/voyager
 */
fun main(args: Array<String>) {
    SpringApplication.run(GraphQLApplication::class.java, *args)
}

