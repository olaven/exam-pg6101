import * as React from "react";
import {Container, Header, Input} from "semantic-ui-react";

export const Search = () => {

   const [email, setEmail] = React.useState(null);

   React.useEffect(() => {

      if (email === null) {

         //TODO: just do a get
      } else {

         //TODO do a search
      }
   }, [email]);

   return <Container>
      <Header as={"h2"}>Search for friends:</Header>
      <Input placeholder={"email"} onChange={(event) => {setEmail(event.target.value)}}/>
   </Container>;
};