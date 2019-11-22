import * as React from "react";
import {UserContext} from "../../context/UserContext";
import {ApiFetch} from "../../../utils/ApiFetch";
import {Container, Header} from "semantic-ui-react";

export const Advertisements = () => {

    const {auth} = React.useContext(UserContext);

    const [advertisements, setAdvertisements] = React.useState([]);

    React.useEffect(() => {

        const fetchAdvertisements = async () => {


            const query = "query { advertisementsForUser(userEmail: \"" +  auth.name +"\" ) {id voteCount message} }";
            const options = {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ query })
            };

            const response = await ApiFetch("/graphql", options);

            if (response.status === 200) {

                const advertisements = (await response.json()).data.advertisementsForUser;
                setAdvertisements(advertisements);
            } else {

                console.error("failed to fetch advertisements ", response);
            }
        };

        fetchAdvertisements();
    }, [auth]);

    return <Container>
        <Header as={"h1"}>Advertisements:</Header>
        {advertisements.map(advertisement =>
            <Container key={advertisement.id}>
                <Header>{advertisement.message}</Header>
                <p>voteCount: {advertisement.voteCount}</p>
            </Container>
        )}
    </Container>
};